package net.shadowkat.minecraft.opensolidstate.common

import li.cil.oc.api.IMC
import net.minecraft.item.EnumDyeColor
import net.minecraft.item.crafting.RecipesBanners.RecipeAddPattern
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.shadowkat.minecraft.opensolidstate.*
import net.shadowkat.minecraft.opensolidstate.common.assemblerTemplates.OssmPromWipe
import net.shadowkat.minecraft.opensolidstate.common.utils.classloader.OSSMClassLoader
import net.shadowkat.minecraft.opensolidstate.common.utils.classloader.getValue2
import java.io.File
import java.net.URI
import java.util.zip.ZipFile
import java.util.zip.ZipInputStream

abstract class Proxy {
    fun preInit(e: FMLPreInitializationEvent) {
        OssmEvents.init()
    }

    /*val assemblerLoader = OSSMClassLoader<Any>(this::class,"net.shadowkat.minecraft.opensolidstate.common.assemblerTemplates", null) { cls, path ->
        println("Loading $path")
        IMC.registerAssemblerTemplate(cls.getValue2("name"), "$path.select", "$path.validate", "$path.assemble",
            cls.getValue2("host"), cls.getValue2("containerTiers"), cls.getValue2("upgradeTiers"), cls.getValue2("componentSlots"))
    }*/

    fun init(e: FMLInitializationEvent) {
        /*for (i in OssmEvents.shit.indices)
            li.cil.oc.api.Driver.add(OssmEepromDriver(OssmEvents.shit[i]))*/

        /*val url = this::class.java.getResource("/mcmod.info")
        println(url)
        val dir = File(url.file)
        println(dir)
        println(dir.exists())
        dir.listFiles()?.forEach { println(it) }
        dir.walk().forEach() {
            println(it.absolutePath)
        }*/

        OssmEvents.postRegistration()

        //OssmPromWipe.register()
        //assemblerLoader.load()
        TheGodTemplate.init()
        li.cil.oc.api.Items.registerFloppy("promutils", EnumDyeColor.PURPLE, LootFloppy("promutils"), true);
        IMC.registerProgramDiskLabel("promutils", "PROM Utilities", "Lua 5.2", "Lua 5.3", "LuaJ")
        //val data = this::class.java.getResourceAsStream("assets/ossm/lua/romfs_exec.lua").readBytes()

        //val data = Minecraft.getMinecraft().resourceManager.getResource(ResourceLocation("ossm", "lua/romfs_exec.lua")).inputStream.readBytes()
        val data = OpenSolidState.javaClass.getResourceAsStream("/assets/ossm/lua/romfs_exec.lua")?.readBytes()

        li.cil.oc.api.Items.registerEEPROM("ROMFS Bootloader", data, null, false)

    }

    fun postInit(e: FMLPostInitializationEvent) {

    }

    abstract fun localize(key : String, vararg v : Any)
}