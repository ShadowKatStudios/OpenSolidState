package net.shadowkat.minecraft.opensolidstate.server.utils

import li.cil.oc.api.Driver
import li.cil.oc.api.driver.DriverItem
import li.cil.oc.api.network.EnvironmentHost
import li.cil.oc.api.network.ManagedEnvironment
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.items.ItemStackHandler

class InvEnvManager(stk : ItemStack, host : EnvironmentHost?) : Iterable<Triple<ItemStack, ManagedEnvironment?, DriverItem>> {
    private data class InvEntry(val slot : Int, val stk : ItemStack, val env : ManagedEnvironment?, val drv : DriverItem)

    private val items : List<InvEntry>
    private val inv = ItemStackHandler()

    init {
        inv.deserializeNBT(stk.getSubCompound("inventory"))
        val mlist : MutableList<InvEntry> = mutableListOf()
        for (slot in 0 until inv.slots) {
            val istk = inv.getStackInSlot(slot)
            if (istk.isEmpty) continue
            val drv = Driver.driverFor(istk)
            val env = drv.createEnvironment(istk, host)
            if (env == null)
                println("${istk} has no env (${drv})")
            mlist.add(InvEntry(slot, istk, env, drv))
        }
        items = mlist.toList()
    }

    override fun iterator(): Iterator<Triple<ItemStack, ManagedEnvironment?, DriverItem>> {
        return object : Iterator<Triple<ItemStack, ManagedEnvironment?, DriverItem>> {
            var idx = 0
            override fun hasNext(): Boolean {
                return idx < items.size
            }

            override fun next(): Triple<ItemStack, ManagedEnvironment?, DriverItem> {
                val rtv = Triple(items[idx].stk, items[idx].env, items[idx].drv)
                idx++
                return rtv
            }
        }
    }

    fun load() {
        for (itm in items) {
            val (slot, istk, env, drv) = itm
            var dt = drv.dataTag(istk)
            if (dt == null) {
                if (istk.getSubCompound("oc:data") == null) {
                    if (istk.tagCompound == null) {
                        istk.tagCompound = NBTTagCompound()
                    }
                    istk.tagCompound!!.setTag("oc:data", NBTTagCompound())
                }
                dt = istk.getSubCompound("oc:data")
            }
            env?.load(dt)
        }
    }

    fun save() : NBTTagCompound {
        for (itm in items) {
            val (slot, istk, env, drv) = itm
            var dt = drv.dataTag(istk)
            if (dt == null) {
                if (istk.getSubCompound("oc:data") == null) {
                    if (istk.tagCompound == null) {
                        istk.tagCompound = NBTTagCompound()
                    }
                    istk.tagCompound!!.setTag("oc:data", NBTTagCompound())
                }
                dt = istk.getSubCompound("oc:data")
            }
            env?.save(dt)
            istk.tagCompound!!.setTag("oc:data", dt)
            inv.setStackInSlot(slot, istk)
        }
        return inv.serializeNBT()
    }
}