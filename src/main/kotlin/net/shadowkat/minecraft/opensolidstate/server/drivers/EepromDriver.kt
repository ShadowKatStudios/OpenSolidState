package net.shadowkat.minecraft.opensolidstate.server.drivers

import li.cil.oc.api.driver.DriverItem
import li.cil.oc.api.driver.item.Slot
import li.cil.oc.api.network.EnvironmentHost
import li.cil.oc.api.network.ManagedEnvironment
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.shadowkat.minecraft.opensolidstate.common.Items
import net.shadowkat.minecraft.opensolidstate.server.components.EEPROM

class EepromDriver : DriverItem {
    override fun worksWith(stk: ItemStack?): Boolean {
        return stk?.item?.registryName == Items.EEPROM.registryName
    }

    override fun createEnvironment(stk: ItemStack?, host: EnvironmentHost?): ManagedEnvironment {
        return EEPROM(tier(stk), (stk?.metadata ?: 0) % 3, host!!, this, stk!!)
    }

    val slots = arrayOf(Slot.HDD, Slot.Card, Slot.Upgrade)

    override fun slot(stk: ItemStack?): String {
        return slots[(stk?.metadata ?: 0) % 3]
    }

    override fun tier(stk: ItemStack?): Int {
        return (stk?.metadata ?: 0) / 3
    }

    override fun dataTag(stk: ItemStack?): NBTTagCompound {
        if (!stk!!.hasTagCompound()) {
            stk.setTagCompound(NBTTagCompound())
        }
        val nbt: NBTTagCompound = stk.getTagCompound()!!
        // This is the suggested key under which to store item component data.
        // You are free to change this as you please.
        // This is the suggested key under which to store item component data.
        // You are free to change this as you please.
        if (!nbt.hasKey("oc:data")) {
            nbt.setTag("oc:data", NBTTagCompound())
        }
        return nbt.getCompoundTag("oc:data")
    }
}