package net.shadowkat.minecraft.opensolidstate.server.components

import li.cil.oc.api.Driver
import li.cil.oc.api.Network
import li.cil.oc.api.driver.DeviceInfo
import li.cil.oc.api.driver.DeviceInfo.DeviceAttribute
import li.cil.oc.api.network.Node
import li.cil.oc.api.network.Visibility
import li.cil.oc.api.prefab.AbstractManagedEnvironment
import li.cil.oc.api.prefab.DriverItem
import net.minecraft.item.ItemStack
import net.shadowkat.minecraft.opensolidstate.common.Constants

class Soc(val tier : Int, val stk : ItemStack) : AbstractManagedEnvironment(), DeviceInfo {

    val devinfo : MutableMap<String, String> = mutableMapOf(
        DeviceAttribute.Class to DeviceInfo.DeviceClass.Processor,
        DeviceAttribute.Description to "SoC",
        DeviceAttribute.Vendor to Constants.defaultVendor,
        DeviceAttribute.Product to "SubspaceChip S{((tier-1)*2)+1} SoC",
        DeviceAttribute.Clock to "0"
    )

    init {
        val node = Network.newNode(this, Visibility.Neighbors)!!
        val comp = node.withConnector()
    }

    override fun getDeviceInfo(): MutableMap<String, String> {
        return devinfo
    }
}