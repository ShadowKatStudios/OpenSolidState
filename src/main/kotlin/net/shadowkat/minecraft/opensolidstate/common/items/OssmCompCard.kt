package net.shadowkat.minecraft.opensolidstate.common.items

import net.shadowkat.minecraft.opensolidstate.common.baseclasses.OssmItem

class OssmCompCard(tier: Int) : OssmItem(tier) {
    override val unlocalizedBaseName: String
        get() = "ossm_comp_card_${tier}"


}