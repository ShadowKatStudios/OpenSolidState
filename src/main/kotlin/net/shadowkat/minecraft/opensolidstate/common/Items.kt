package net.shadowkat.minecraft.opensolidstate.common

import net.minecraft.item.Item
import net.minecraftforge.event.RegistryEvent
import net.shadowkat.minecraft.opensolidstate.common.items.*
import net.shadowkat.minecraft.opensolidstate.common.utils.classloader.OSSMClassLoader
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.primaryConstructor

object Items {
    val List = mutableListOf<Item>()
    lateinit var EEPROM : OssmNewEeprom
    lateinit var Flash : OssmFlash
    lateinit var SocTemplate : OssmSocTemplate
    lateinit var SoC : OssmSoc

    fun load(evt :  RegistryEvent.Register<Item>) {
        val ldr = OSSMClassLoader<OssmBaseItem>(OssmBaseItem::class, "net.shadowkat.minecraft.opensolidstate.common.items", arrayOf("OssmBaseItem")) { cls, path ->
            //cls.primaryConstructor!!.call().register(evt)
            println("Registering $path")
            cls.createInstance().register(evt)
        }
        ldr.load()
    }
}