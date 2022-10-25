package net.shadowkat.minecraft.opensolidstate.common.assemblerTemplates

import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack

interface ITemplate {
    val name : String
    val tier : Int
    val tiers : Int
    val containerTiers : IntArray
    val upgradeTiers : IntArray
    val componentTiers : List<Pair<String, Int>>
    val complexity : Int
    val host : Class<*>?


    fun select(stk: ItemStack): Boolean
    fun validate(inv: IInventory): Array<Any>
    fun assemble(inv: IInventory): Array<Any>
}