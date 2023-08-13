package net.shadowkat.minecraft.opensolidstate.common

import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

class CreativeTab : CreativeTabs("ossm") {
    @SideOnly(Side.CLIENT)
    override fun getTabIconItem(): ItemStack {
        return ItemStack(Items.EEPROM, 1, 2)
    }
}