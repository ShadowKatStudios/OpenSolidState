package net.shadowkat.minecraft.opensolidstate.common.baseclasses

import li.cil.oc.api.network.EnvironmentHost
import li.cil.oc.api.network.ManagedEnvironment
import li.cil.oc.api.prefab.DriverItem
import net.minecraft.item.ItemStack

abstract class OssmDriver(itm : ItemStack) : DriverItem(itm) {
    abstract val DriverItemNames : Array<String>
}