package net.shadowkat.minecraft.opensolidstate.server.utils

import net.minecraft.item.ItemStack
import kotlin.experimental.inv
import kotlin.experimental.or

class ZeroOnlyBlkdev(stk: ItemStack, n_uuid: String?, blockSize: Int, blocks: Int) : StorageDeviceManager(stk, n_uuid, blockSize, blocks) {
    override fun writeBlk(pos: Int, bytes: ByteArray) {
        val dat = readBlk(pos)
        val write = ByteArray(dat.size) {
            if (it >= bytes.size)
                dat[it]
            else
                (dat[it].inv() or bytes[it].inv()).inv()
        }
        super.writeBlk(pos, write)
    }
}