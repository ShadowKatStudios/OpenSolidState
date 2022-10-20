package net.shadowkat.minecraft.opensolidstate.server.drivers

import li.cil.oc.api.driver.DriverItem
import li.cil.oc.api.driver.item.Slot
import li.cil.oc.api.network.EnvironmentHost
import li.cil.oc.api.network.ManagedEnvironment
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.shadowkat.minecraft.opensolidstate.common.Items
import net.shadowkat.minecraft.opensolidstate.server.components.EEPROM
import net.shadowkat.minecraft.opensolidstate.server.components.Flash

class FlashDriver : DriverItem {
    override fun worksWith(stk: ItemStack?): Boolean {
        return stk?.item?.registryName == Items.Flash.registryName
    }

    override fun createEnvironment(stk: ItemStack?, host: EnvironmentHost?): ManagedEnvironment {
        return Flash(stk!!.metadata, host!!, this, stk!!) //EEPROM(tier(stk), (stk?.metadata ?: 0) % 3, host!!, this, stk!!)
    }

    override fun slot(stk: ItemStack?): String {
        return Slot.Upgrade
    }

    override fun tier(stk: ItemStack?): Int {
        return stk?.metadata ?: 0
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