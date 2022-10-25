package net.shadowkat.minecraft.opensolidstate.common.assemblerTemplates

import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack
import net.minecraft.util.text.TextComponentTranslation

class OssmPromWipe : ITemplate {
	/*val name = "prom_wipe"
		val host : Class<Any>? = null
		val containerTiers = IntArray(0)
		val upgradeTiers = IntArray(0)
		val componentSlots : List<Tuple<String, Int>> = emptyList()
		@JvmStatic
		fun register(cpath : String) {
			IMC.registerAssemblerTemplate("prom_wipe", "net.shadowkat.minecraft.opensolidstate.common.assemblerTemplates.OssmPromWipe.select", "net.shadowkat.minecraft.opensolidstate.common.assemblerTemplates.OssmPromWipe.validate", "net.shadowkat.minecraft.opensolidstate.common.assemblerTemplates.OssmPromWipe.assemble", null, IntArray(0), IntArray(0), emptyList());
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
		}*/
	override val name: String = "prom_wipe"
	override val tier: Int = 0
	override val tiers: Int = 1
	override val containerTiers: IntArray = intArrayOf()
	override val upgradeTiers: IntArray = intArrayOf()
	override val componentTiers: List<Pair<String, Int>> = listOf()
	override val complexity: Int = 0
	override val host: Class<*>? = null

	override fun select(stk: ItemStack): Boolean {
		return (stk.unlocalizedName == "item.ossm_prom_0" || stk.unlocalizedName == "item.ossm_prom_0_card")
	}

	override fun validate(inv: IInventory): Array<Any> {
		return arrayOf(true, TextComponentTranslation("gui.ossm_depreciated"))
	}

	override fun assemble(inv: IInventory): Array<Any> {
		val itm = inv.getStackInSlot(0)
		val ossm_dat = itm.getSubCompound("oc:data")?.getCompoundTag("ossm:data")
		ossm_dat?.setBoolean("wipe", true)
		return arrayOf(itm)
	}
}