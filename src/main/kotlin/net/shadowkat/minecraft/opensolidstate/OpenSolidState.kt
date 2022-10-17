package net.shadowkat.minecraft.opensolidstate

import li.cil.oc.api.IMC
import net.minecraft.client.Minecraft
import net.minecraft.item.EnumDyeColor
import net.minecraft.item.Item
import net.minecraft.util.ResourceLocation
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.registry.GameRegistry

@Mod(modid = "ossm", name = "OpenSolidState", version = "1.0.0", dependencies = "required-after:opencomputers@[1.7.0,)", modLanguageAdapter = "net.shadowfacts.forgelin.KotlinAdapter")
object OpenSolidState {
	@Mod.Instance
	var instance: OpenSolidState? = null

	@Mod.EventHandler
	fun preInit(e: FMLPreInitializationEvent) {
		println("opensolidstate" + ":preInit")
		OssmEvents.init()
	}

	@Mod.EventHandler
	fun init(e: FMLInitializationEvent) {
		println("opensolidstate" + ":init")
		for (i in OssmEvents.shit.indices)
			li.cil.oc.api.Driver.add(OssmEepromDriver(OssmEvents.shit[i])) // eat shit and die
		OssmPromWipe.register()
		li.cil.oc.api.Items.registerFloppy("promutils", EnumDyeColor.PURPLE, LootFloppy("promutils"), true);
		IMC.registerProgramDiskLabel("promutils", "PROM Utilities", "Lua 5.2", "Lua 5.3", "LuaJ")
		//val data = this::class.java.getResourceAsStream("assets/ossm/lua/romfs_exec.lua").readBytes()

		val data = Minecraft.getMinecraft().resourceManager.getResource(ResourceLocation("ossm", "lua/romfs_exec.lua")).inputStream.readBytes()

		li.cil.oc.api.Items.registerEEPROM("ROMFS Bootloader", data, null, false)
		//GameRegistry.addShapedRecipe();
	}

	@Mod.EventHandler
	fun postInit(event: FMLPostInitializationEvent) {
		println("opensolidstate" + ":postInit")
	}
}
