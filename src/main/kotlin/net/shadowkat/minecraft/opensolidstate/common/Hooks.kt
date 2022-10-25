package net.shadowkat.minecraft.opensolidstate.common

import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.shadowkat.minecraft.opensolidstate.common.utils.hooks.Hook
import kotlin.reflect.KFunction
import kotlin.reflect.jvm.reflect

open class Hooks {
    val _preInit = Hook<Unit>()
    val _init = Hook<Unit>()
    val _postInit = Hook<Unit>()
    val _registerItems = Hook<Unit>()
    val _registerRecipes = Hook<Unit>()
    val _registerRenders = Hook<Unit>()
}