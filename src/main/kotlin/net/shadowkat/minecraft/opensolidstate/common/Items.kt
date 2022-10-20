package net.shadowkat.minecraft.opensolidstate.common

import net.minecraft.item.Item
import net.shadowkat.minecraft.opensolidstate.common.items.OssmFlash
import net.shadowkat.minecraft.opensolidstate.common.items.OssmNewEeprom

object Items {
    val List = mutableListOf<Item>()
    lateinit var EEPROM : OssmNewEeprom
    lateinit var Flash : OssmFlash
}