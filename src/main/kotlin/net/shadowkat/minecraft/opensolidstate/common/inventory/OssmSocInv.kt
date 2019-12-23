package net.shadowkat.minecraft.opensolidstate.common.inventory

import li.cil.oc.api.driver.DeviceInfo
import li.cil.oc.api.driver.item.Slot
import net.shadowkat.minecraft.opensolidstate.common.baseclasses.OssmDeviceInv

val comp_tier : IntArray = intArrayOf(5, 7, 9, 20)
val comp_extra : IntArray = intArrayOf(1, 2, 3, 3)
val card_tier : Array<IntArray> = arrayOf(
        intArrayOf(0),
        intArrayOf(1, 0),
        intArrayOf(2, 1, 0),
        intArrayOf(3, 3, 3)
)
class OssmSocInv(tier : Int) : OssmDeviceInv(comp_tier[tier], "SoC") {
    val tier = tier
    override fun getInvSlotTypes(): HashMap<Int, String> {
        return hashMapOf(
                0 to Slot.CPU,
                1 to Slot.Memory,
                2 to Slot.ComponentBus,
                3 to Slot.Card,
                4 to Slot.Upgrade,
                5 to Slot.Card,
                6 to Slot.Upgrade,
                7 to Slot.Card,
                8 to Slot.Upgrade
        )
    }

    override fun getSlots(): HashMap<Int, Int> {
        val tiers = hashMapOf(
                0 to tier,
                1 to tier,
                2 to tier,
                3 to card_tier[tier][0],
                4 to card_tier[tier][0],
                5 to card_tier[tier][0],
                6 to card_tier[tier][0],
                7 to card_tier[tier][0],
                8 to card_tier[tier][0]
        )
        if (comp_tier[tier] > 9)
        for (i in (comp_extra[tier]*2)+3 until comp_tier[tier]-1) {
            tiers[i] = card_tier[tier][0]
        }
    }

    override fun getUpgrades(): ArrayList<Int> {
        
    }

    override fun getContainers(): ArrayList<Int> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}