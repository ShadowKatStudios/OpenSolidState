package net.shadowkat.minecraft.opensolidstate.common

import li.cil.oc.api.IMC
import net.minecraft.item.EnumDyeColor
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.shadowkat.minecraft.opensolidstate.*

open class Proxy {
    fun preInit(e: FMLPreInitializationEvent) {
        OssmEvents.init()
    }

    fun init(e: FMLInitializationEvent) {
        /*for (i in OssmEvents.shit.indices)
            li.cil.oc.api.Driver.add(OssmEepromDriver(OssmEvents.shit[i]))*/
        OssmEvents.postRegistration()
        OssmPromWipe.register()
        li.cil.oc.api.Items.registerFloppy("promutils", EnumDyeColor.PURPLE, LootFloppy("promutils"), true);
        IMC.registerProgramDiskLabel("promutils", "PROM Utilities", "Lua 5.2", "Lua 5.3", "LuaJ")
        //val data = this::class.java.getResourceAsStream("assets/ossm/lua/romfs_exec.lua").readBytes()

        //val data = Minecraft.getMinecraft().resourceManager.getResource(ResourceLocation("ossm", "lua/romfs_exec.lua")).inputStream.readBytes()
        val data = OpenSolidState.javaClass.getResourceAsStream("assets/ossm/lua/romfs_exec.lua")?.readBytes()

        li.cil.oc.api.Items.registerEEPROM("ROMFS Bootloader", data, null, false)
    }

    fun postInit(e: FMLPostInitializationEvent) {

    }
}