package net.shadowkat.minecraft.opensolidstate.common.recipes

import net.minecraft.inventory.InventoryCrafting
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.World
import net.minecraftforge.registries.IForgeRegistryEntry
import net.shadowkat.minecraft.opensolidstate.OssmEeprom
import net.shadowkat.minecraft.opensolidstate.common.Items
import net.shadowkat.minecraft.opensolidstate.common.Settings

class EEPROMConvert : IForgeRegistryEntry.Impl<IRecipe>(), IRecipe {

    init {
        setRegistryName("ossm:eeprom_convert")
    }

    override fun matches(inv: InventoryCrafting, worldIn: World): Boolean {
        var found = false
        for (i in 0 until inv.getSizeInventory()) {
            val stk = inv.getStackInSlot(i)
            if (stk.isEmpty)
                continue
            else if (stk.item.registryName.toString().startsWith("ossm:prom_")) {
                if (found)
                    return false
                else
                    found = true
            } else {
                return false
            }
        }
        return true
    }

    override fun getCraftingResult(inv: InventoryCrafting): ItemStack {
        for (i in 0 until inv.getSizeInventory()) {
            val stk = inv.getStackInSlot(i)
            if (!stk.isEmpty) {
                val rname = stk.item.registryName!!.resourcePath
                val tier = rname[5]-'0'
                val card = if (rname.length > 6) 1 else 0
                val metadata = (3*tier)+card
                val newitem = ItemStack(Items.EEPROM, 1, metadata)
                //val compound = stk.getSubCompound("ossm:data") ?: return newitem

                val cpd = NBTTagCompound()
                val data = NBTTagCompound()
                val address = stk.getSubCompound("node")?.getString("address")
                val write = stk.getSubCompound("ossm:data")?.getByteArray("write")
                if (address != null) {
                    val com = NBTTagCompound()
                    com.setString("address", address)
                    data.setTag("node", com)
                }
                if (write != null)
                    data.setByteArray("written", write)
                data.setInteger("blksize", Settings.storage.eepromBlksize)
                data.setInteger("size", Settings.storage.eepromSizes[tier])
                cpd.setTag("oc:data", data)
                newitem.setTagCompound(cpd)
                return newitem
            }
        }
        return ItemStack.EMPTY
    }

    override fun canFit(width: Int, height: Int): Boolean {
        return width > 0 && height > 0
    }

    override fun getRecipeOutput(): ItemStack {
        return ItemStack(Items.EEPROM)
    }

}