package net.shadowkat.minecraft.opensolidstate.common.enviroments

import li.cil.oc.api.Driver
import li.cil.oc.api.driver.item.Processor
import li.cil.oc.api.network.ComponentHost
import li.cil.oc.api.network.Environment
import li.cil.oc.api.network.EnvironmentHost
import li.cil.oc.api.prefab.AbstractManagedEnvironment
import net.minecraft.world.World
import net.shadowkat.minecraft.opensolidstate.common.baseclasses.OssmDeviceInv
import net.shadowkat.minecraft.opensolidstate.common.inventory.OssmSocInv

class OssmSocEnv(host : EnvironmentHost, inv : OssmSocInv) : AbstractManagedEnvironment(), ComponentHost {
    val host = host
    val inv = inv
    val envs = ArrayList<Environment>()
    init {
        for (k in 0 until inv.slots-1) {
            val stk = inv.getStackInSlot(k)
            if (!stk.isEmpty)
                envs.add(Driver.driverFor(stk).createEnvironment(stk, host))
        }
    }
    override fun xPosition(): Double {
        return host.xPosition()
    }

    override fun getComponents(): MutableIterable<Environment> {
        return envs
    }

    override fun yPosition(): Double {
        return host.yPosition()
    }

    override fun world(): World {
        return host.world()
    }

    override fun markChanged() {
        host.markChanged()
    }

    override fun zPosition(): Double {
        return host.zPosition()
    }

}