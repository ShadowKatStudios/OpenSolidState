package net.shadowkat.minecraft.opensolidstate.server.utils

import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.common.DimensionManager
import java.io.File
import java.io.RandomAccessFile
import kotlin.math.max
import kotlin.math.min

open class StorageDeviceManager(val stk : ItemStack, n_uuid : String?, blockSize : Int, blocks : Int) {
    var uuid : String = ""
    lateinit var file : File
    val blkSize : Int
    val blks : Int
    val cache : HashMap<Int, ByteArray> = hashMapOf()

    lateinit var filehand : RandomAccessFile
    init {
        val comp = stk.getSubCompound("oc:data")

        if (comp?.hasKey("node") == true && comp.getCompoundTag("node").hasKey("address")) // why is null not a falsey value
            uuid = comp.getCompoundTag("node").getString("address")
        else if (n_uuid != null)
            uuid = n_uuid

        if (comp?.hasKey("blockSize") == true)
            blkSize = comp.getInteger("blockSize")
        else
            blkSize = blockSize
        if (comp?.hasKey("blocks") == true)
            blks = comp.getInteger("blocks")
        else
            blks = blocks
        if (isReady()) {
            init()
        }
    }

    fun init() {
        val comp = stk.getSubCompound("oc:data")

        file = File(DimensionManager.getCurrentSaveRootDirectory().toString() + "/ossm_drives/" + uuid + ".bin")
        file.parentFile.mkdirs()

        if (!file.exists()) {
            file.writeBytes(ByteArray(blkSize*blks) {0xFF.toByte()})
        }

        comp?.setInteger("blockSize", blkSize)
        comp?.setInteger("blocks", blks)
        stk.getTagCompound()?.setTag("oc:data", comp)
        val dif = (blkSize*blks)-file.length()
        if (dif > 0) {
            file.appendBytes(ByteArray(dif.toInt()) {0xFF.toByte()})
        }
        filehand = RandomAccessFile(file, "rw")
    }

    fun isReady() : Boolean {
        return this::filehand.isInitialized
    }

    fun rawWriteBlk(pos : Int, bytes : ByteArray) {
        if (!isReady() || cache.containsKey(pos)) {
            cache[pos] = bytes
            return
        }
        filehand.seek((pos*blkSize).toLong())
        filehand.write(bytes, 0, min(blkSize, bytes.size))
    }

    open fun writeBlk(pos : Int, bytes : ByteArray) {
        rawWriteBlk(pos, bytes)
    }

    fun readBlk(pos : Int) : ByteArray {
        if (cache.containsKey(pos)) {
            return cache[pos]!!
        }
        if (!isReady()) {
            return ByteArray(blkSize)
        }
        filehand.seek((pos*blkSize).toLong())
        val ba = ByteArray(blkSize)
        filehand.read(ba)
        return ba
    }

    fun erase(fillByte : Byte) {
        if (!isReady()) {
            for (i in 0 until blks) {
                rawWriteBlk(i, ByteArray(blkSize) {fillByte})
            }
            return
        }
        filehand.close()
        file.writeBytes(ByteArray(blkSize*blks) {fillByte})
        filehand = RandomAccessFile(file, "rw")
    }

    fun flush() {
        if (uuid != "")
            init()
        if (isReady()) {
            for (pairs in cache) {
                rawWriteBlk(pairs.key, pairs.value)
            }
            cache.clear()
            filehand.close()
            filehand = RandomAccessFile(file, "rw")
        } else {
            println("node still has no uuid?")
        }
    }
}