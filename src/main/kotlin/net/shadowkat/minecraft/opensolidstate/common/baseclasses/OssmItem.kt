package net.shadowkat.minecraft.opensolidstate.common.baseclasses

import net.minecraft.item.Item

abstract class OssmItem : Item {
    abstract val unlocalizedBaseName : String
    val tier : Int
    constructor() {
        this.tier = 0
    }
    constructor(tier : Int) {
        this.tier = tier
    }
}