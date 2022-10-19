package net.shadowkat.minecraft.opensolidstate.server.drivers

import li.cil.oc.api.driver.item.Slot
import li.cil.oc.api.network.EnvironmentHost
import li.cil.oc.api.network.ManagedEnvironment
import li.cil.oc.api.prefab.DriverItem
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
        return env
    }

    override fun slot(stk: ItemStack?): String {
        return Slot.CPU
    }
}