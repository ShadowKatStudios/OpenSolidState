package net.shadowkat.minecraft.opensolidstate

import li.cil.oc.api.IMC
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack
import org.apache.commons.lang3.tuple.Pair

class OssmPromWipe {
	companion object {
		fun register() {
			IMC.registerAssemblerTemplate("prom_wipe", "net.shadowkat.minecraft.opensolidstate.OssmPromWipe.select", "net.shadowkat.minecraft.opensolidstate.OssmPromWipe.validate", "net.shadowkat.minecraft.opensolidstate.OssmPromWipe.assemble", null, IntArray(0), IntArray(0), emptyList());
		}

		@JvmStatic
		fun validate(inv: IInventory): Array<Any> {
			return Array(1) { true }
		}

		@JvmStatic
		fun select(stk: ItemStack): Boolean {
			//println(stk.unlocalizedName)
			return (stk.unlocalizedName == "item.ossm_prom_0" || stk.unlocalizedName == "item.ossm_prom_0_card")
		}

		@JvmStatic
		fun assemble(inv: IInventory): Array<Any> {
			val itm = inv.getStackInSlot(0)
			val ossm_dat = itm.getSubCompound("oc:data")?.getCompoundTag("ossm:data")
			ossm_dat?.setBoolean("wipe", true)
			return arrayOf(itm)
		}
	}
}