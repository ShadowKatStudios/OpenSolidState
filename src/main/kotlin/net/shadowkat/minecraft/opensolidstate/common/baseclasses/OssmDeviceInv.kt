package net.shadowkat.minecraft.opensolidstate.common.baseclasses

import net.minecraft.item.ItemStack

abstract class OssmDeviceInv(slots : Int, name : String) : OssmInventory(slots, name) {
    abstract fun getInvSlotTypes() : HashMap<Int, String>
    abstract fun getSlots() : HashMap<Int, Int>
    abstract fun getUpgrades() : ArrayList<Int>
    abstract fun getContainers() : ArrayList<Int>
}