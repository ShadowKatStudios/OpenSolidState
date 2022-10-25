package net.shadowkat.minecraft.opensolidstate

import li.cil.oc.api.CreativeTab
import net.minecraft.client.resources.I18n
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import net.minecraftforge.event.RegistryEvent
import net.shadowkat.minecraft.opensolidstate.common.items.OssmBaseItem
import net.shadowkat.minecraft.opensolidstate.common.Constants.ObjectProperties
import net.shadowkat.minecraft.opensolidstate.common.Settings
import net.shadowkat.minecraft.opensolidstate.common.utils.Utils

class OssmEeprom : OssmBaseItem {
	private val tier : Int
	private val card : Boolean

	override val lore: String = "An item from a different year."

	constructor(tier : Int, card : Boolean) {
		this.outdated = true
		this.tier = tier
		this.card = card
		creativeTab = CreativeTab.instance;
		unlocalizedName = "ossm_prom_${tier}${cardText()}"
		setRegistryName("ossm:prom_${tier}${cardText()}")
	}

	private fun cardText() : String {
		if (card)
			return "_card"
		else
			return ""
	}

	fun getTier() : Int {
		return tier
	}

	fun getCard() : Boolean {
		return card
	}

	override fun addAdditionalInformation(stack: ItemStack, worldIn: World?, tooltip: MutableList<String>, flagIn: ITooltipFlag) {
		if (tier > 0) {
			tooltip.add("§f"+I18n.format("gui.ossm_itemprop_electronic_erase")+"§8")
		}
	}

	override fun register(event: RegistryEvent.Register<Item>) {

	}

	override fun getData(stack: ItemStack): List<Pair<String, String>> {
		return listOf(ObjectProperties.Storage to Utils.toBytes((Settings.storage.eepromSizes[tier]*Settings.storage.eepromBlksize).toDouble(), 0),
		              ObjectProperties.SectorSize to Utils.toBytes(Settings.storage.eepromBlksize.toDouble(), 0))
	}

	override fun getTier(stack: ItemStack): Int {
		return tier
	}


}