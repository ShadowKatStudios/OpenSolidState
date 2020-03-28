package net.shadowkat.minecraft.opensolidstate.common.baseclasses

import li.cil.oc.api.IMC
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack
import org.apache.commons.lang3.tuple.Pair
import kotlin.reflect.KClass

abstract class OssmTemplate {
    abstract val host : Class<*>?
    abstract val containerTiers : IntArray?
    abstract val upgradeTiers : IntArray?
    abstract val componentSlots : ArrayList<Pair<String, Int>>?

    fun register() {
        IMC.registerAssemblerTemplate("temp_ossm"+this::class.qualifiedName, this::class.qualifiedName+".select", this::class.qualifiedName+".validate", this::class.qualifiedName+".assemble", host, containerTiers, upgradeTiers, componentSlots);
    }

    abstract fun select(stk: ItemStack): Boolean
    abstract fun validate(inv: IInventory): Array<Any>
    abstract fun assemble(inv: IInventory): Array<Any>
}