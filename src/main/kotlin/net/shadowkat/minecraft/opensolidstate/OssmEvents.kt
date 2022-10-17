package net.shadowkat.minecraft.opensolidstate

import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.item.Item
import net.minecraftforge.client.event.ModelRegistryEvent
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@Mod.EventBusSubscriber(modid = "ossm")
object OssmEvents {
	lateinit var shit: Array<Item>

	fun init() {
		shit = arrayOf(OssmEeprom(0, false), OssmEeprom(0, true), OssmEeprom(1, false), OssmEeprom(1, true), OssmEeprom(2, false), OssmEeprom(2, true), OssmEeprom(3, false), OssmEeprom(3, true))
	}

	@SubscribeEvent
	fun registerItems(event: RegistryEvent.Register<Item>) {
		for (i in shit.indices)
			event.registry.register(shit[i])
	}

	@SubscribeEvent
	fun registerRenders(event: ModelRegistryEvent) {
		for (i in shit.indices)
			ModelLoader.setCustomModelResourceLocation(shit[i], 0, ModelResourceLocation(shit[i].registryName!!, "inventory"))
		//ModelLoader.set
	}
}
