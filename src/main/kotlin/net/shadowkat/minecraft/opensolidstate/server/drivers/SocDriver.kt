package net.shadowkat.minecraft.opensolidstate.server.drivers

import li.cil.oc.api.Driver
import li.cil.oc.api.driver.item.Slot
import li.cil.oc.api.network.EnvironmentHost
import li.cil.oc.api.network.ManagedEnvironment
import li.cil.oc.api.prefab.DriverItem
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraftforge.items.ItemStackHandler
import net.shadowkat.minecraft.opensolidstate.server.components.Soc

class SocDriver : DriverItem {
    constructor(item: ItemStack) : super(item) {

    }

    override fun createEnvironment(stk: ItemStack?, host: EnvironmentHost?): ManagedEnvironment {
        val env =  Soc(stk!!.metadata, stk!!);
        val inv = ItemStackHandler()
        inv.deserializeNBT(stk.getSubCompound("inventory"))
        val envs : MutableList<Pair<Item, ManagedEnvironment>> = mutableListOf()
        for (slot in 0 until inv.slots) {
            val item = inv.getStackInSlot(slot)
            val drv = Driver.driverFor(item)
            drv.createEnvironment(item, host)
        }
        return env
    }

    override fun slot(stk: ItemStack?): String {
        return Slot.CPU
    }
}