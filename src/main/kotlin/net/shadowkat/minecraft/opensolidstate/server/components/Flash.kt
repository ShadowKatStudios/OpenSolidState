package net.shadowkat.minecraft.opensolidstate.server.components

import li.cil.oc.api.Network
import li.cil.oc.api.driver.DeviceInfo
import li.cil.oc.api.machine.Arguments
import li.cil.oc.api.machine.Callback
import li.cil.oc.api.machine.Context
import li.cil.oc.api.network.EnvironmentHost
import li.cil.oc.api.network.Visibility
import li.cil.oc.api.prefab.AbstractManagedEnvironment
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.shadowkat.minecraft.opensolidstate.common.Settings
import net.shadowkat.minecraft.opensolidstate.server.drivers.EepromDriver
import net.shadowkat.minecraft.opensolidstate.server.drivers.FlashDriver
import net.shadowkat.minecraft.opensolidstate.server.utils.StorageDeviceManager

class Flash(val tier : Int, val host : EnvironmentHost, val driver : FlashDriver, val stk : ItemStack) : AbstractManagedEnvironment(), DeviceInfo {

    val node = Network.newNode(this, Visibility.Neighbors)!!.withComponent(if (Settings.storage.flashEmulateDrive) "drive_flash" else "ossm_flash", Visibility.Neighbors)!!.withConnector().create()
    var uuid : String = ""
    /*val promdata : Array<ByteArray> = Array<ByteArray>(Settings.storage.eepromSizes[tier]) {
        ByteArray(Settings.storage.eepromBlksize)
    }*/
    val sdev : StorageDeviceManager
    val capacity : Int


    init {
        setNode(node)
        //uuid = stk.getSubCompound("oc:data")?.getCompoundTag("network")?.getString("address") ?: node.address()
        sdev = StorageDeviceManager(stk, null, Settings.storage.flashBlksize, Settings.storage.flashSizes[tier])
        capacity = sdev.blks*sdev.blkSize
    }

    override fun getDeviceInfo(): MutableMap<String, String> {
        return mutableMapOf(
            DeviceInfo.DeviceAttribute.Class to DeviceInfo.DeviceClass.Disk,
            DeviceInfo.DeviceAttribute.Description to "Flash",
            DeviceInfo.DeviceAttribute.Vendor to "Shadow Kat Semiconductor",
            DeviceInfo.DeviceAttribute.Version to "Rev ${tier+1}",
            DeviceInfo.DeviceAttribute.Capacity to "$capacity",
            DeviceInfo.DeviceAttribute.Product to "FISH-${capacity/1024}"
        );
    }

    @Callback(direct = true, doc = "readByte(offset:number):number -- Read a single byte at the specified offset.")
    fun readByte(ctx: Context, args: Arguments) : Array<Any?> {
        val offset = args.checkInteger(0);
        if (offset < 1 || offset > sdev.blks*sdev.blkSize)
            return arrayOf(null, "invalid offset")
        val blk = sdev.readBlk((offset - 1)/sdev.blkSize)
        return arrayOf(blk[(offset - 1)%sdev.blkSize])
    }

    @Callback(direct = true, doc = "writeByte(offset:number, value:number):number -- Writes a single byte at the specified offset.")
    fun writeByte(ctx: Context, args: Arguments) : Array<Any?> {
        val offset = args.checkInteger(0);
        val value = args.checkInteger(1)
        if (offset < 1 || offset > sdev.blks*sdev.blkSize)
            return arrayOf(null, "invalid offset")
        val blk = sdev.readBlk((offset - 1)/sdev.blkSize)
        blk[(offset - 1)%sdev.blkSize] = (value and 0xFF).toByte()
        sdev.writeBlk((offset - 1)/sdev.blkSize, blk)
        return arrayOf()
    }

    @Callback(direct = true, doc = "readSector(offset:number):number -- Read the current contents of the specified sector.")
    fun readSector(ctx: Context, args: Arguments) : Array<Any?> {
        val offset = args.checkInteger(0)
        if (offset < 1 || offset > sdev.blks)
            return arrayOf(null, "invalid offset")
        val blk = sdev.readBlk(offset-1)
        return arrayOf(blk)
    }

    @Callback(direct = true, doc = "writeSector(offset:number, value:string) -- Write the specified contents to the specified sector.")
    fun writeSector(ctx: Context, args: Arguments) : Array<Any?> {
        val offset = args.checkInteger(0)
        val data = args.checkByteArray(1)
        if (offset < 1 || offset > sdev.blks)
            return arrayOf(null, "invalid offset")
        sdev.writeBlk(offset-1, data)
        return arrayOf()
    }

    @Callback(direct = true, doc = "getSectorSize():number -- Returns the size of a single sector on the drive, in bytes.")
    fun getSectorSize(ctx: Context, args: Arguments) : Array<Any?> {
        return arrayOf(sdev.blkSize)
    }

    @Callback(direct = true, doc = "getCapacity():number -- Returns the total capacity of the drive, in bytes.")
    fun getCapacity(ctx: Context, args: Arguments) : Array<Any?> {
        return arrayOf(sdev.blkSize*sdev.blks)
    }

    @Callback(direct = true, doc = "getPlatterCount()():number -- Returns the number of platters in the drive.")
    fun getPlatterCount(ctx: Context, args: Arguments) : Array<Any?> {
        return arrayOf(0)
    }

    override fun load(n: NBTTagCompound) {
        super.load(n)
        if (uuid == "") {
            if (node() != null) {
                uuid = node()?.address() ?: "" // this literally never works
            }
            if (uuid == "") {
                uuid = n.getCompoundTag("node").getString("address")
            }
            if (uuid != "")
                sdev.uuid = uuid
            else
                println("node still has no uuid?")
        }
        if (uuid != "")
            sdev.uuid = uuid
    }
    override fun save(nbt: NBTTagCompound?) {
        super.save(nbt)
        if (uuid == "") {
            if (node() != null) {
                uuid = node()?.address() ?: "" // this literally never works
            }
            if (uuid == "") {
                uuid = nbt!!.getCompoundTag("node").getString("address")
            }
            if (uuid != "")
                sdev.uuid = uuid
            else
                println("node still has no uuid?")
        }
        sdev.flush()
    }
}