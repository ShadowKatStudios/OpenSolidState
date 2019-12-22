package net.shadowkat.minecraft.opensolidstate.common.staticitems

import li.cil.oc.api.CreativeTab
import net.minecraft.item.Item

class OssmEeprom : Item {
	private val tier : Int
	private val card : Boolean

	constructor(tier : Int, card : Boolean) {
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
}