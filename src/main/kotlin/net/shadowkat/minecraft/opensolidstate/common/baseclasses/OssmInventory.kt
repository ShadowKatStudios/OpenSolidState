package net.shadowkat.minecraft.opensolidstate.common.baseclasses

import li.cil.oc.api.driver.item.Inventory
import li.cil.oc.api.network.EnvironmentHost
import li.cil.oc.api.network.ManagedEnvironment
import net.minecraft.inventory.InventoryBasic
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.util.Constants

open class OssmInventory(slots : Int, name : String) : InventoryBasic(name, false, slots) {
    fun load_inv(nbt : NBTTagCompound) {
        val inv = nbt.getTagList("inventory", Constants.NBT.TAG_COMPOUND)
        for (k in 0 until inv.tagCount()-1) {
            val obj = inv.getCompoundTagAt(k)
            val its = ItemStack(Item.REGISTRY.getObject(ResourceLocation(obj.getString("item"))))
            its.itemDamage = obj.getShort("damage").toInt()
            val inbt = obj.getCompoundTag("data")
            its.deserializeNBT(inbt)
            its.count = obj.getInteger("count")
            this.addItem(its)
        }
    }

    fun save_inv(nbt : NBTTagCompound) {
        val inv = nbt.getTagList("inventory", Constants.NBT.TAG_COMPOUND)
        
    }
}