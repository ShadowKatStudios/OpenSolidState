package net.shadowkat.minecraft.opensolidstate.common

import li.cil.oc.api.Driver
import li.cil.oc.api.IMC
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack
import net.minecraft.util.text.TextComponentTranslation
import net.shadowkat.minecraft.opensolidstate.common.assemblerTemplates.ITemplate
import net.shadowkat.minecraft.opensolidstate.common.utils.classloader.OSSMClassLoader
import net.shadowkat.minecraft.opensolidstate.common.utils.classloader.getFunction2
import net.shadowkat.minecraft.opensolidstate.common.utils.classloader.hasFunction
import org.apache.commons.lang3.tuple.Pair
import kotlin.reflect.full.createInstance

object TheGodTemplate {
    // because if it breaks it'd be just as bad as the god nut breaking in a helicopter

    val templates : MutableList<ITemplate> = mutableListOf()

    val loader = OSSMClassLoader<ITemplate>(TheGodTemplate::class, "net.shadowkat.minecraft.opensolidstate.common.assemblerTemplates", arrayOf("ITemplate")) { cls, path ->
        println("Registering "+path)
        val inst = cls.createInstance()
        if (cls.hasFunction("specialRegister")) {
            cls.getFunction2("specialRegister").call(inst, templates)
        } else if (inst.tiers > 1) {
            for (con in cls.constructors)
                if (con.parameters.size == 1)
                    for (i in inst.tier until inst.tier+inst.tiers)
                        templates.add(con.call(i))
        } else {
            templates.add(inst)
        }
    }

    fun init() {
        loader.load()
        var i = 0;
        for (t in templates) {
            println("register: ${t.name}_${t.tier}, host: ${t.host}, c_tiers: ${t.componentTiers}, u_tiers: ${t.upgradeTiers}, com_tiers: ${t.componentTiers}")
            IMC.registerAssemblerTemplate("${t.name}_${t.tier}",
                                          "net.shadowkat.minecraft.opensolidstate.common.TheGodTemplate.select${i}",
                                          "net.shadowkat.minecraft.opensolidstate.common.TheGodTemplate.validate",
                                          "net.shadowkat.minecraft.opensolidstate.common.TheGodTemplate.assemble",
                                          t.host, t.containerTiers, t.upgradeTiers, List<Pair<String, Int>>(t.componentTiers.size) {Pair.of(t.componentTiers[it].first, t.componentTiers[it].second)})
            i++
        }
    }

    fun getComplexity(inv : IInventory) : Int {
        var comp = 0
        for (i in 1 until inv.sizeInventory) {
            val stk = inv.getStackInSlot(i)
            if (!stk.isEmpty) {
                comp += Driver.driverFor(stk).tier(stk)+1
            }
        }
        return comp
    }

    fun getTemplate(stk : ItemStack) : ITemplate? {
        for (t in templates)
            if (t.select(stk))
                return t
        return null
    }

    // wish i could do this better
    @JvmStatic
    fun select0(stk: ItemStack): Boolean { return templates[0].select(stk) }
    @JvmStatic
    fun select1(stk: ItemStack): Boolean { return templates[1].select(stk) }
    @JvmStatic
    fun select2(stk: ItemStack): Boolean { return templates[2].select(stk) }
    @JvmStatic
    fun select3(stk: ItemStack): Boolean { return templates[3].select(stk) }
    @JvmStatic
    fun select4(stk: ItemStack): Boolean { return templates[4].select(stk) }
    @JvmStatic
    fun select5(stk: ItemStack): Boolean { return templates[5].select(stk) }
    @JvmStatic
    fun select6(stk: ItemStack): Boolean { return templates[6].select(stk) }

    @JvmStatic
    fun validate(inv: IInventory): Array<Any> {
        val drv = getTemplate(inv.getStackInSlot(0))
        try {
            val rtn = drv?.validate(inv) ?: throw InternalError("got to validate without valid item!")
            val comp = getComplexity(inv)
            if (comp > drv.complexity)
                return arrayOf(false, TextComponentTranslation("gui.ossm_too_many_embeded_device"))
            if (rtn.size == 1) {
                return arrayOf(rtn[0], TextComponentTranslation("gui.ossm_complexity", comp, drv.complexity))
            }
            return rtn
        } catch (e : Exception) {
            System.err.println(e.localizedMessage)
            e.stackTrace.forEach {System.err.println(it)}
            return arrayOf(false)
        }
    }

    @JvmStatic
    fun assemble(inv: IInventory): Array<Any> {
        try {
            val drv = getTemplate(inv.getStackInSlot(0))
            return drv?.assemble(inv) ?: throw InternalError("got to ASSEMBLE without valid item! (how)")
        } catch (e : Exception) {
            System.err.println(e.localizedMessage)
            e.stackTrace.forEach {System.err.println(it)}
            return arrayOf(false)
        }
    }
}