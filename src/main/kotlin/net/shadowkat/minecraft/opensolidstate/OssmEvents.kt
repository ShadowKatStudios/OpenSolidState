package net.shadowkat.minecraft.opensolidstate

import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.item.Item
import net.minecraft.item.crafting.IRecipe
import net.minecraftforge.client.event.ModelRegistryEvent
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.shadowkat.minecraft.opensolidstate.common.Items
import net.shadowkat.minecraft.opensolidstate.common.items.OssmNewEeprom
import net.shadowkat.minecraft.opensolidstate.common.recipes.EEPROMConvert
import kotlin.reflect.KClass
import kotlin.reflect.KFunction

@Mod.EventBusSubscriber(modid = "ossm")
object OssmEvents {
	//lateinit var shit: Array<Item>

	var items : HashMap<String, MutableList<Item>> = hashMapOf()
	var postreg : HashMap<String, (Item) -> Int> = hashMapOf()

	var model_register : MutableList<(ModelRegistryEvent) -> Unit> = mutableListOf()
	private fun <T : Item> addItem(cls: KClass<T>, name: String, tiers: Int, firstTier: Int, secondArgOptions: Array<Any>?, prfunc: (Item) -> Int) {
		val maxArgs = if (secondArgOptions == null) 0 else 1
		lateinit var rcon : KFunction<T>
		for (con in cls.constructors) {
			println("params: "+con.parameters.size)
			println("wanted: "+(maxArgs+1))
			if (con.parameters.size == maxArgs+1) {
				rcon = con
				break
			}
		}
		val lst = mutableListOf<Item>()
		items[name] = lst
		for (tier in firstTier..tiers) {
			if (maxArgs == 0) {
				lst[tier] = rcon.call(listOf<Any>(tier))
			} else
				for (i in secondArgOptions!!) {
					lst.add(rcon.call(tier, i))
				}
		}
		postreg.set(name, prfunc)
	}

	fun postRegistration() {
		for (key in items.keys) {
			for (item in items[key]!!) {
				postreg[key]!!.invoke(item)
			}
		}
	}

	fun init() {
		//shit = arrayOf(OssmEeprom(0, false), OssmEeprom(0, true), OssmEeprom(1, false), OssmEeprom(1, true), OssmEeprom(2, false), OssmEeprom(2, true), OssmEeprom(3, false), OssmEeprom(3, true))
		addItem<OssmEeprom>(OssmEeprom::class, "eeprom", 2, 0, arrayOf(true, false)) {
			li.cil.oc.api.Driver.add(OssmEepromDriver(it as OssmEeprom))
			0
		} // kotlin does not like this line

	}

	@SubscribeEvent
	fun registerItems(event: RegistryEvent.Register<Item>) {
		/*for (i in shit.indices)
			event.registry.register(shit[i])*/
		for (list in items.values) {
			for (item in list) {
				event.registry.register(item)
			}
		}
		val eeprom = OssmNewEeprom()
		eeprom.register(event)
		Items.EEPROM = eeprom
	}

	@SubscribeEvent
	fun registerRecipes(event: RegistryEvent.Register<IRecipe>) {
		event.registry.register(EEPROMConvert())
	}



	@SubscribeEvent
	fun registerRenders(event: ModelRegistryEvent) {
		for (list in items.values) {
			for (item in list) {
				ModelLoader.setCustomModelResourceLocation(item, 0, ModelResourceLocation(item.registryName!!, "inventory"))
			}
		}
		for (reg in model_register)
			reg(event)
		/*for (i in shit.indices)
			ModelLoader.setCustomModelResourceLocation(shit[i], 0, ModelResourceLocation(shit[i].registryName!!, "inventory"))*/
		//ModelLoader.set
	}
}
