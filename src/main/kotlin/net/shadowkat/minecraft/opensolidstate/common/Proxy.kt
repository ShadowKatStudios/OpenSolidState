package net.shadowkat.minecraft.opensolidstate.common

import net.minecraft.item.Item
import net.minecraftforge.client.event.ModelRegistryEvent
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.shadowkat.minecraft.opensolidstate.common.registries.Drivers
import net.shadowkat.minecraft.opensolidstate.common.registries.Items
import net.shadowkat.minecraft.opensolidstate.common.registries.Models

open class Proxy {
    val CLIENT : Boolean = false
    val SERVER : Boolean = false

    fun preInit(event : FMLPreInitializationEvent) {

    }

    fun init(event : FMLInitializationEvent) {
        Items.init()
        Models.init()
    }

    fun postInit(event : FMLPostInitializationEvent) {
        Drivers.init()
    }

    fun onRegisterModels(event : ModelRegistryEvent) {

    }

    fun onRegisterItems(event : RegistryEvent.Register<Item>) {

    }
}