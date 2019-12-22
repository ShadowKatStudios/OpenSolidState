package net.shadowkat.minecraft.opensolidstate.common.registries

import net.minecraft.item.Item
import net.minecraft.util.ResourceLocation
import net.minecraftforge.registries.GameData
import net.shadowkat.minecraft.opensolidstate.common.baseclasses.OssmItem
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

object Items : BaseRegistryInit("net.shadowkat.minecraft.opensolidstate.common.items") {
    var items : ArrayList<OssmItem> = ArrayList()
    override fun load(cls: KClass<*>) {
        if (cls.isSubclassOf(Item::class)) {
            var rcls = cls as KClass<OssmItem>
            for (cons in rcls.constructors) {
                if ((cons.parameters.size == 1) and (cons.parameters[0].type.classifier == Int::class)) { // Takes a tier
                    for (i in 0 until 3) {
                        try {
                            val itm = cons.call(i)
                            itm.unlocalizedName = "item.ossm."+itm.unlocalizedBaseName+"_"+i
                            GameData.register_impl(itm.setRegistryName(ResourceLocation("ossm", itm.unlocalizedBaseName+"_"+i)))
                            items.add(itm)
                        } catch (e: Exception) {
                            // Do absolutely fuck all
                        }
                    }
                } else if (cons.parameters.isEmpty()) {
                    try {
                        val itm = cons.call()
                        itm.unlocalizedName = "item.ossm."+itm.unlocalizedBaseName
                        GameData.register_impl(itm.setRegistryName(ResourceLocation("ossm", itm.unlocalizedBaseName)))
                        items.add(itm)
                    } catch (e: Exception) {
                        // Do absolutely fuck all^2
                    }
                } else {
                    println("Unknown constructor with "+cons.parameters.size+" parameters!")
                }
            }
        }
    }

    override fun preInit() {
        
    }

    fun get(n : String) : OssmItem {
        for (itm in items) {
            if (itm.unlocalizedBaseName == n) {
                return itm
            }
        }
        throw IndexOutOfBoundsException()
    }
}