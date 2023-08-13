package net.shadowkat.minecraft.opensolidstate.server.components

import li.cil.oc.api.driver.DeviceInfo
import li.cil.oc.api.machine.Arguments
import li.cil.oc.api.machine.Callback
import li.cil.oc.api.machine.Context
import li.cil.oc.api.prefab.AbstractManagedEnvironment

class APCCard : AbstractManagedEnvironment(), DeviceInfo {
    override fun getDeviceInfo(): MutableMap<String, String> {
        TODO("Not yet implemented")
    }

    @Callback(direct = true)
    fun sendCommand(ctx: Context, args: Arguments) : Array<Any?> {
        // TODO("Not yet implemented")
        return arrayOf()
    }
}