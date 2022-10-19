package net.shadowkat.minecraft.opensolidstate.server.components

import li.cil.oc.api.Network
import li.cil.oc.api.driver.DeviceInfo
import li.cil.oc.api.network.EnvironmentHost
import li.cil.oc.api.network.Visibility
import li.cil.oc.api.prefab.AbstractManagedEnvironment
import net.minecraft.item.ItemStack
import net.shadowkat.minecraft.opensolidstate.common.Settings
import net.shadowkat.minecraft.opensolidstate.server.drivers.EepromDriver
import net.shadowkat.minecraft.opensolidstate.server.utils.StorageDeviceManager

class Flash(val tier : Int, val host : EnvironmentHost, val driver : Any, val stk : ItemStack) : AbstractManagedEnvironment(), DeviceInfo {

    val node = Network.newNode(this, Visibility.Neighbors)!!.withComponent(if (Settings.storage.flashEmulateDrive) "drive_flash" else "ossm_flash", Visibility.Neighbors)!!.withConnector().create()
    var uuid : String = ""
    /*val promdata : Array<ByteArray> = Array<ByteArray>(Settings.storage.eepromSizes[tier]) {
        ByteArray(Settings.storage.eepromBlksize)
    }*/
    val sdev : StorageDeviceManager
    val capacity : Int


    init {
        setNode(node)
        uuid = stk.getSubCompound("oc:data")?.getCompoundTag("network")?.getString("address") ?: node.address()
        sdev = StorageDeviceManager(stk, uuid, Settings.storage.flashBlksize, Settings.storage.flashSizes[tier])
        capacity = sdev.blks*sdev.blkSize
    }

    override fun getDeviceInfo(): MutableMap<String, String> {
        return mutableMapOf(
            DeviceInfo.DeviceAttribute.Class to DeviceInfo.DeviceClass.Disk,
            DeviceInfo.DeviceAttribute.Description to "Flash",
            DeviceInfo.DeviceAttribute.Vendor to "Shadow Kat Semiconductor",
            DeviceInfo.DeviceAttribute.Version to "Rev ${tier+1}",
            DeviceInfo.DeviceAttribute.Capacity to "$capacity",
            DeviceInfo.DeviceAttribute.Product to "FISH-${capacity/1024}"
        );
    }


}