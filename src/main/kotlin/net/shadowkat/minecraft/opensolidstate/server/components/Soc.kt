package net.shadowkat.minecraft.opensolidstate.server.components

import li.cil.oc.api.Driver
import li.cil.oc.api.Network
import li.cil.oc.api.driver.DeviceInfo
import li.cil.oc.api.driver.DeviceInfo.DeviceAttribute
import li.cil.oc.api.network.Component
import li.cil.oc.api.network.ManagedEnvironment
import li.cil.oc.api.network.Node
import li.cil.oc.api.network.Visibility
import li.cil.oc.api.prefab.AbstractManagedEnvironment
import li.cil.oc.api.prefab.DriverItem
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.items.ItemStackHandler
import net.shadowkat.minecraft.opensolidstate.common.Constants

class Soc(val tier : Int, val stk : ItemStack) : AbstractManagedEnvironment(), DeviceInfo {

    lateinit var inv : ItemStackHandler
    lateinit var envs : MutableList<Triple<ItemStack, ManagedEnvironment, li.cil.oc.api.driver.DriverItem>>

    val devinfo : MutableMap<String, String> = mutableMapOf(
        DeviceAttribute.Class to DeviceInfo.DeviceClass.Processor,
        DeviceAttribute.Description to "SoC",
        DeviceAttribute.Vendor to Constants.defaultVendor,
        DeviceAttribute.Product to "SubspaceChip ${((tier)*2)+1} SoC",
        DeviceAttribute.Clock to "0"
    )

    init {
        val node = Network.newNode(this, Visibility.Neighbors)!!
        val comp = node.withConnector()
        setNode(comp.create())
    }

    override fun getDeviceInfo(): MutableMap<String, String> {
        return devinfo
    }

    override fun load(nbt: NBTTagCompound?) {
        super.load(nbt)
        inv.deserializeNBT(stk.getSubCompound("inventory"))
        for (p in envs) {
            p.second.load(p.third.dataTag(p.first))
        }
    }

    override fun save(nbt: NBTTagCompound?) {
        super.save(nbt)
        for (p in envs) {
            p.second.save(p.third.dataTag(p.first))
        }
        stk.tagCompound!!.setTag("inventory", inv.serializeNBT())
    }

    override fun onConnect(node: Node?) {
        super.onConnect(node)
        for (p in envs) {
            /*println(node)
            println(node())
            println(p.second.node())
            println("==================")*/
            if (p.second.node() != null) {
                val com = p.second.node() as Component
                if (node!! != p.second.node())
                    node!!.network().connect(node, p.second.node())
                /*println(node.network())
                println(node().network())
                println(p.second.node().network())
                println("==================")*/
            }
            //p.second.node().connect(node!!)
        }
    }

    override fun onDisconnect(node: Node?) {
        super.onDisconnect(node)
        for (p in envs) {
            p.second.node().remove()
        }
    }
}