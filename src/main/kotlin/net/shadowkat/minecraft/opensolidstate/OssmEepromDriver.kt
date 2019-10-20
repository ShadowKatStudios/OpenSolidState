package net.shadowkat.minecraft.opensolidstate

import li.cil.oc.api.Network
import li.cil.oc.api.driver.DeviceInfo
import li.cil.oc.api.driver.item.Slot
import li.cil.oc.api.machine.Arguments
import li.cil.oc.api.machine.Callback
import li.cil.oc.api.machine.Context
import li.cil.oc.api.network.EnvironmentHost
import li.cil.oc.api.network.ManagedEnvironment
import li.cil.oc.api.network.Node
import li.cil.oc.api.network.Visibility
import li.cil.oc.api.prefab.AbstractManagedEnvironment
import li.cil.oc.api.prefab.DriverItem
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.common.DimensionManager
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardOpenOption
import java.util.*
import kotlin.math.min

class OssmEepromDriver : DriverItem {
    public val BLKSIZE : Short = 512
    public val BLKS : ShortArray = shortArrayOf(128, 256)
    fun getBlkSize() : Short {
        return BLKSIZE
    }

    fun getNBlks() : Short {
        if (tier > 1)
            return BLKS[1]
        else
            return BLKS[0]
    }

    val tier : Int
    val card : Boolean
    constructor(i : Item) : super(ItemStack(i)) {
        tier = (i as OssmEeprom).getTier();
        card = (i as OssmEeprom).getCard();
    }

    override fun tier(stack: ItemStack?): Int {
        return tier
    }

    override fun createEnvironment(stack: ItemStack, host: EnvironmentHost): ManagedEnvironment {
        return OssmEeepromEnviroment(stack, host, tier, card, this)
    }

    override fun slot(stack: ItemStack): String {
        return if (card) Slot.Card else Slot.HDD
    }

    public class OssmEeepromEnviroment : AbstractManagedEnvironment, DeviceInfo {
        private val tier : Int
        private val card : Boolean
        private val stk : ItemStack
        private var nbt : NBTTagCompound
        private val node : Node?
        private var promdata : Array<ByteArray>
        private var uuid : String
        private val driver : OssmEepromDriver
        constructor(stack : ItemStack, host: EnvironmentHost, t : Int, c : Boolean, d : OssmEepromDriver) {
            driver = d
            tier = t
            card = c
            stk = stack
            nbt = NBTTagCompound()
            promdata = Array(driver.getNBlks().toInt()) {ByteArray(driver.getBlkSize().toInt())}
            uuid = ""
            var newnode = Network.newNode(this, Visibility.Neighbors)!!.withComponent("ossm_prom", Visibility.Neighbors)!!.withConnector()

            node = newnode!!.create()
            setNode(node)
        }
        /*
            Internal API!
         */
        private fun blkErase(blk: Int): Boolean {
            if (blk < 1 || blk > promdata.size)
                return false
            Arrays.fill(promdata[blk - 1], 0.toByte())
            return true
        }

        private fun blkWrite(blk: Int, wdat: ByteArray): Boolean {
            if (blk < 1 || blk > promdata.size)
                return false
            System.arraycopy(wdat, 0, promdata[blk - 1], 0, min(wdat.size, driver.getBlkSize().toInt()))
            return true
        }

        private fun getCapacity() : Int {
            return driver.getBlkSize() * driver.getNBlks()
        }

        private fun getLetters() : String {
            var str = ""
            if (nbt.getBoolean("erase"))
                str += "K"
            if (card)
                str += "C"
            else
                str += "H"
            if (tier > 0)
                str += "E"
            return str
        }

        /* Device information! */
        override fun getDeviceInfo() : Map<String, String> {
            return mapOf(
                    DeviceInfo.DeviceAttribute.Class to DeviceInfo.DeviceClass.Disk,
                    DeviceInfo.DeviceAttribute.Description to "PROM",
                    DeviceInfo.DeviceAttribute.Vendor to "Shadow Kat Semiconductor",
                    DeviceInfo.DeviceAttribute.Version to "Rev ${tier+1}",
                    DeviceInfo.DeviceAttribute.Capacity to "${getCapacity()}",
                    DeviceInfo.DeviceAttribute.Product to "OSSM-${getCapacity()/1024}${getLetters()}",
                    DeviceInfo.DeviceAttribute.Clock to "${getCapacity()/2}"
            );
        }

        @Callback(direct = true, limit = 256, doc = "blockRead(block:number):string -- Returns the data in a block")
        fun blockRead(ctx: Context, args: Arguments): Array<Any> {
            val blk = args.checkInteger(0)
            return if (blk < 1 || blk > promdata.size) arrayOf(false, "invalid block id") else arrayOf(promdata[blk - 1])
        }

        @Callback(direct = true, limit = 32, doc = "getWearLevel(block:number):number -- Returns the wear level of a sector.")
        fun getWearLevel(ctx: Context, args: Arguments): Array<Any> {
            return if (!nbt.getBoolean("erase")) {
                arrayOf(false, "no block erase support")
            } else arrayOf(nbt.getIntArray("wear")[args.checkInteger(0)])
        }

        @Callback(doc = "erase():boolean -- Erases the entire EEPROM")
        fun erase(ctx: Context, args: Arguments): Array<Any> {
            if (tier < 1)
                return arrayOf(false, "no electronic erase support")
            var wl: IntArray? = null
            if (nbt.getBoolean("erase")) {
                wl = nbt.getIntArray("wear")
            }
            for (i in promdata.indices) {
                blkErase(i + 1)
                if (nbt.getBoolean("erase")) {
                    wl?.set(i + 1, 1)
                }
            }
            if (nbt.getBoolean("erase")) {
                nbt.setIntArray("wear", wl!!)
            }
            nbt.setByteArray("write", ByteArray(promdata.size))
            ctx.pause(2.0)
            return arrayOf(true)
        }

        @Callback(doc = "blockErase(block:number):boolean -- Erases a block")
        fun blockErase(ctx: Context, args: Arguments): Array<Any> {
            if (!nbt.getBoolean("erase")) {
                return arrayOf(false, "no block erase support")
            }
            val blk = args.checkInteger(0)
            if (blkErase(blk)) {
                val wl = nbt.getIntArray("wear")
                wl[blk] -= 2
                nbt.setIntArray("wear", wl)
                val bs = nbt.getByteArray("write")
                bs[blk] = 0
                nbt.setByteArray("write", bs)
                ctx.pause((2 / driver.getNBlks().toInt()).toDouble())
                return arrayOf(true)
            }
            return arrayOf(false, "unable to erase block")
        }

        @Callback(doc = "blockWrite(block:number, data:string):boolean -- Writes a block")
        fun blockWrite(ctx: Context, args: Arguments): Array<Any> {
            val blk = args.checkInteger(0)
            val data = args.checkByteArray(1)
            println("nbt size:" + nbt.getByteArray("write").size)
            if (nbt.getByteArray("write")[blk - 1] > 0) {
                return arrayOf(false, "block is read-only")
            }
            if (!blkWrite(blk, data))
                return arrayOf(false, "write failed")
            val bs = nbt.getByteArray("write")
            println("write size: " + bs.size)
            bs[blk - 1] = 1
            nbt.setByteArray("write", bs)
            ctx.pause((2 / driver.getNBlks().toInt()).toDouble())
            return arrayOf(true)
        }

        @Callback(direct = true, doc = "numBlocks():number -- Returns the number of blocks.")
        fun numBlocks(ctx: Context, args: Arguments): Array<Any> {
            return arrayOf(driver.getNBlks().toInt())
        }

        @Callback(direct = true, doc = "blockSize():number -- Returns the block size in bytes.")
        fun blockSize(ctx: Context, args: Arguments): Array<Any> {
            return arrayOf(driver.getBlkSize().toInt())
        }

        @Callback(direct = true, doc = "hasBlockErase():boolean -- Returns if the PROM has block erase or not.")
        fun hasBlockErase(ctx: Context, args: Arguments): Array<Any> {
            return arrayOf(nbt.getBoolean("erase"))
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
        fun setLabel(ctx: Context, args: Arguments): Array<Any> {
            nbt.setString("label", args.checkString(0))
            stk.setStackDisplayName(args.checkString(0))
            return arrayOf(stk.displayName)
        }

        @Callback(direct = true, doc = "getLabel():string -- Gets the label of the EEPROM.")
        fun getLabel(ctx: Context, args: Arguments): Array<Any> {
            return arrayOf(stk.displayName)
        }

        override fun load(n: NBTTagCompound) {
            super.load(n)
            this.nbt = n.getCompoundTag("ossm:data")
            if (this.nbt.getByteArray("write").size < driver.getNBlks().toInt() || promdata.size < driver.getNBlks() ) {
                this.nbt.setByteArray("write", ByteArray(driver.getNBlks().toInt()))
                val wl = IntArray(driver.getNBlks().toInt())
                Arrays.fill(wl, 20000)
                this.nbt.setIntArray("wear", wl)
                this.nbt.setBoolean("erase", false)
            }
            if (uuid == null || uuid == "") {
                if (node() != null) {
                    uuid = node()!!.address()!!
                } else {
                    uuid = n.getCompoundTag("node").getString("address")
                }
            }
            if (uuid != null && uuid != "") {
                val savePath = File(DimensionManager.getCurrentSaveRootDirectory().toString() + "/ossm_drives/" + uuid + ".bin")
                try {
                    val bis = ByteArrayInputStream(Files.readAllBytes(savePath.toPath()))
                    val blocks = driver.getNBlks().toInt()
                    if (promdata == null) {
                        promdata = Array(blocks) { ByteArray(driver.getBlkSize().toInt()) }
                    }
                    for (i in 0 until blocks) {
                        bis.read(promdata[i])
                    }
                } catch (e: Exception) {
                    // Again, should probably do something...
                    System.err.println(e)
                }

            }
        }

        override fun save(n: NBTTagCompound) {
            super.save(n)
            if (uuid == null || uuid == "") {
                uuid = node?.address()!!
            }
            val savePath = File(DimensionManager.getCurrentSaveRootDirectory().toString() + "/ossm_drives/" + uuid + ".bin")
            if (promdata == null) {
                val blocks = driver.getNBlks().toInt()
                promdata = Array(blocks) { ByteArray(driver.getBlkSize().toInt()) }
            }
            try {
                val os = ByteArrayOutputStream()
                for (i in promdata.indices) {
                    os.write(promdata[i])
                }
                savePath.getParentFile().mkdirs()
                Files.write(savePath.toPath(), os.toByteArray(), StandardOpenOption.WRITE, StandardOpenOption.CREATE)
            } catch (e: Exception) {
                // We should probably do something
                System.err.println(e)
            }

            n.setTag("ossm:data", nbt)
        }
    }
}