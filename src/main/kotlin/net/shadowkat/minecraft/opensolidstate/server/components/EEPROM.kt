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
import net.minecraftforge.common.DimensionManager
import net.shadowkat.minecraft.opensolidstate.common.Settings
import net.shadowkat.minecraft.opensolidstate.server.drivers.EepromDriver
import net.shadowkat.minecraft.opensolidstate.server.utils.StorageDeviceManager
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardOpenOption
import java.util.*
import kotlin.math.min

class EEPROM(val tier : Int, val formfactor : Int, val host : EnvironmentHost, val driver : EepromDriver, val stk : ItemStack) : AbstractManagedEnvironment(), DeviceInfo {
    val node = Network.newNode(this, Visibility.Neighbors)!!.withComponent("ossm_prom", Visibility.Neighbors)!!.withConnector().create()
    var uuid : String = ""
    /*val promdata : Array<ByteArray> = Array<ByteArray>(Settings.storage.eepromSizes[tier]) {
        ByteArray(Settings.storage.eepromBlksize)
    }*/
    val sdev : StorageDeviceManager
    val capacity : Int

    init {
        setNode(node)
        //uuid = stk.getSubCompound("oc:data")?.getCompoundTag("network")?.getString("address") ?: "" // fuck OFF
        sdev = StorageDeviceManager(stk, null, Settings.storage.eepromBlksize, Settings.storage.eepromSizes[tier]) // because the uuid is always fucking wrong
        capacity = sdev.blkSize*sdev.blks
    }

    val model_letters = "DCU"

    override fun getDeviceInfo(): MutableMap<String, String> {
        return mutableMapOf(
            DeviceInfo.DeviceAttribute.Class to DeviceInfo.DeviceClass.Disk,
            DeviceInfo.DeviceAttribute.Description to "PROM",
            DeviceInfo.DeviceAttribute.Vendor to "Shadow Kat Semiconductor",
            DeviceInfo.DeviceAttribute.Version to "Rev ${tier+1}",
            DeviceInfo.DeviceAttribute.Capacity to "$capacity",
            DeviceInfo.DeviceAttribute.Product to "ROMCOM-${capacity/1024}${model_letters[formfactor]}${if (tier > 0) "K" else ""}",
            DeviceInfo.DeviceAttribute.Clock to "${capacity/Settings.storage.eepromFlashTime}"
        );
    }

    private fun getBlks() : Int {
        return sdev.blks
    }

    @Callback(direct = true, limit = 256, doc = "blockRead(block:number):string -- Returns the data in a block")
    fun blockRead(ctx: Context, args: Arguments): Array<Any?> {
        val blk = args.checkInteger(0)
        if (blk < 1 || blk > getBlks())
            return arrayOf(null, "block $blk out of bounds")
        else
            return arrayOf(sdev.readBlk(blk-1))
    }

    @Callback(doc = "erase():boolean -- Erases the entire EEPROM")
    fun erase(ctx: Context, args: Arguments): Array<Any> {
        sdev.erase(0xFF.toByte())
        driver.dataTag(stk).setByteArray("written", ByteArray(getBlks()) {0})
        ctx.pause(Settings.storage.eepromFlashTime)
        return arrayOf(true)
    }

    @Callback(doc = "blockWrite(block:number, data:string):boolean -- Writes a block")
    fun blockWrite(ctx: Context, args: Arguments): Array<Any?> {
        val blk = args.checkInteger(0)
        val data = args.checkByteArray(1)
        if (blk < 1 || blk > getBlks())
            return arrayOf(null, "block $blk out of bounds")
        if (driver.dataTag(stk).getByteArray("written")[blk - 1] > 0) {
            return arrayOf(null, "block is read-only")
        }
        //System.arraycopy(data, 0, promdata[blk - 1], 0, min(data.size, Settings.storage.eepromBlksize))
        sdev.writeBlk(blk-1, data)
        val bs = driver.dataTag(stk).getByteArray("written")
        //println("write size: " + bs.size)
        bs[blk - 1] = 1
        driver.dataTag(stk).setByteArray("written", bs)
        ctx.pause((Settings.storage.eepromFlashTime / getBlks()))
        return arrayOf(true)
    }

    @Callback(direct = true, doc = "numBlocks():number -- Returns the number of blocks.")
    fun numBlocks(ctx: Context, args: Arguments): Array<Any> {
        return arrayOf(getBlks())
    }

    @Callback(direct = true, doc = "blockSize():number -- Returns the block size in bytes.")
    fun blockSize(ctx: Context, args: Arguments): Array<Any> {
        return arrayOf(sdev.blkSize)
    }

    @Callback(direct = true, doc = "hasElectronicErase():boolean -- Returns if the PROM has electronic erase or not")
    fun hasElectronicErase(ctx: Context, args: Arguments): Array<Any> {
        return arrayOf(tier > 0)
    }

    @Callback(direct = true, doc = "hc():boolean -- Returns if the PROM is a high capacity model")
    fun hc(ctx: Context, args: Arguments): Array<Any> {
        return arrayOf(tier > 1)
    }

    @Callback(doc = "setLabel(label:string):string -- Sets the label of the EEPROM.")
    fun setLabel(ctx: Context, args: Arguments): Array<Any?> {
        //nbt.setString("label", args.checkString(0))
        //stk.setStackDisplayName(args.checkString(0))
        if (args.count() == 0) {
            driver.dataTag(stk).removeTag("oc:label")
            return arrayOf("")
        }
        driver.dataTag(stk).setString("oc:label", args.checkString(0).substring(0, 15))
        return arrayOf(driver.dataTag(stk).getString("oc:label"))
    }

    @Callback(direct = true, doc = "getLabel():string -- Gets the label of the EEPROM.")
    fun getLabel(ctx: Context, args: Arguments): Array<Any?> {
        return arrayOf(driver.dataTag(stk).getString("oc:label"))
    }

    @Callback(direct = true, doc = "blockFree():boolean -- Gets if block has been written to or not.")
    fun blockFree(ctx: Context, args: Arguments): Array<Any?> {
        val blk = args.checkInteger(0)
        val bs = driver.dataTag(stk).getByteArray("written")
        if (blk < 1 || blk > getBlks())
            return arrayOf(null, "block $blk out of bounds")
        return arrayOf(bs[blk-1] > 0)
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
        /*if (n.getByteArray("written").size < getBlks() ) {
            n.setByteArray("written", ByteArray(getBlks()))
        }*/
    }

    override fun save(n: NBTTagCompound) {
        super.save(n)
        if (uuid != "")
            sdev.uuid = uuid
        if (uuid == "") {
            uuid = node?.address() ?: "" // this literally never works^2
            if (uuid != "")
                sdev.uuid = uuid
            else
                println("node still has no uuid?")
        }
        sdev.flush()
    }
}