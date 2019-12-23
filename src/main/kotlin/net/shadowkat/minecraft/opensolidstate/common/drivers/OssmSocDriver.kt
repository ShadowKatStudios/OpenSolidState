package net.shadowkat.minecraft.opensolidstate.common.drivers

import li.cil.oc.api.Driver
import li.cil.oc.api.Machine
import li.cil.oc.api.driver.item.Processor
import li.cil.oc.api.driver.item.Slot
import li.cil.oc.api.machine.Architecture
import li.cil.oc.api.network.EnvironmentHost
import li.cil.oc.api.network.ManagedEnvironment
import li.cil.oc.api.prefab.DriverItem
import net.minecraft.item.ItemStack
import net.shadowkat.minecraft.opensolidstate.common.baseclasses.OssmItem
import net.shadowkat.minecraft.opensolidstate.common.enviroments.OssmSocEnv
import net.shadowkat.minecraft.opensolidstate.common.inventory.OssmSocInv
import net.shadowkat.minecraft.opensolidstate.common.registries.Items

class OssmSocDriver() : DriverItem(ItemStack(Items.get("soc_0")), ItemStack(Items.get("soc_1")), ItemStack(Items.get("soc_2"))), Processor {
    override fun slot(p0: ItemStack?): String {
        return Slot.CPU
    }

    override fun createEnvironment(p0: ItemStack?, p1: EnvironmentHost?): ManagedEnvironment {
        val inv = OssmSocInv((p0!!.item as? OssmItem)!!.tier)
        inv.load_inv(p0.tagCompound!!)
        return OssmSocEnv(p1!!, inv)
    }

    override fun supportedComponents(p0: ItemStack?): Int {
        val inv = OssmSocInv((p0!!.item as? OssmItem)!!.tier)
        inv.load_inv(p0.tagCompound!!)
        val stk = inv.getStackInSlot(0)
        return (Driver.driverFor(stk) as? Processor)!!.supportedComponents(stk)
    }

    override fun architecture(p0: ItemStack?): Class<out Architecture> {
        val inv = OssmSocInv((p0!!.item as? OssmItem)!!.tier)
        inv.load_inv(p0.tagCompound!!)
        val stk = inv.getStackInSlot(0)
        return (Driver.driverFor(stk) as? Processor)!!.architecture(stk)
    }
}