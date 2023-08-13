package net.shadowkat.minecraft.opensolidstate.server.drivers

import li.cil.oc.Settings
import li.cil.oc.api.Driver
import li.cil.oc.api.Machine
import li.cil.oc.api.driver.DriverItem
import li.cil.oc.api.driver.item.CallBudget
import li.cil.oc.api.driver.item.MutableProcessor
import li.cil.oc.api.driver.item.Slot
import li.cil.oc.api.machine.Architecture
import li.cil.oc.api.network.Component
import li.cil.oc.api.network.EnvironmentHost
import li.cil.oc.api.network.ManagedEnvironment
import li.cil.oc.api.network.Visibility
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.items.ItemStackHandler
import net.shadowkat.minecraft.opensolidstate.common.Items
import net.shadowkat.minecraft.opensolidstate.server.components.Soc
import net.shadowkat.minecraft.opensolidstate.server.utils.InvEnvManager
import kotlin.math.max
import kotlin.math.min

class SocDriver : DriverItem, MutableProcessor, CallBudget {
    init {

    }

    override fun worksWith(stk: ItemStack?): Boolean {
        return stk!!.item.registryName == Items.SoC.registryName
    }

    override fun createEnvironment(stk: ItemStack?, host: EnvironmentHost?): ManagedEnvironment {
        val ienv = InvEnvManager(stk!!, host)
        val env =  Soc(stk!!.metadata, stk!!, ienv);
        /*val inv = ItemStackHandler()
        inv.deserializeNBT(stk.getSubCompound("inventory"))
        val envs : MutableList<Triple<Pair<ItemStack, Int>, ManagedEnvironment, DriverItem>> = mutableListOf()
        for (slot in 0 until inv.slots) {
            val item = inv.getStackInSlot(slot)
            if (item.isEmpty) continue
            val drv = Driver.driverFor(item)
            val cenv = drv.createEnvironment(item, host)
            println(cenv)
            try {
                (cenv.node() as Component).setVisibility(Visibility.Network)

            } catch (e : Exception) {
                // lol
            }
            envs.add(Triple(item to slot, cenv, drv))
        }*/

        //env.envs = envs
        //env.inv = inv
        env.load(dataTag(stk))
        return env
    }

    override fun slot(stk: ItemStack?): String {
        return Slot.CPU
    }

    override fun tier(stk: ItemStack?): Int {
        return min(stk?.metadata ?: 0, 2)
    }

    override fun dataTag(stk: ItemStack?): NBTTagCompound {
        return stk?.getSubCompound("oc:data") ?: NBTTagCompound()
    }

    override fun supportedComponents(stk: ItemStack?): Int {
        return Settings.get().cpuComponentSupport()[stk!!.metadata]
    }

    override fun architecture(stk: ItemStack?): Class<out Architecture> {
        return try {
            val clz = stk!!.tagCompound!!.getString("oc:archClass")
            val clazz = Class.forName(clz).asSubclass(Architecture::class.java)
            clazz
        } catch (e : Exception) {
            stk!!.tagCompound!!.setString("oc:archClass", Machine.LuaArchitecture::class.qualifiedName!!)
            Machine.LuaArchitecture
        }
    }

    override fun allArchitectures(): MutableCollection<Class<out Architecture>> {
        return Machine.architectures()
    }

    override fun setArchitecture(stk: ItemStack?, clz: Class<out Architecture>?) {
        stk!!.tagCompound!!.setString("oc:archClass", clz!!.name)
    }

    override fun getCallBudget(stk: ItemStack?): Double {
        return Settings.get().callBudgets()[min(stk!!.metadata, 2)]
    }
}