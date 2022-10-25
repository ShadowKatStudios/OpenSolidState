package net.shadowkat.minecraft.opensolidstate.common.assemblerTemplates

import li.cil.oc.api.Machine
import li.cil.oc.api.driver.item.Slot
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.text.TextComponentTranslation
import net.minecraftforge.items.ItemStackHandler
import net.shadowkat.minecraft.opensolidstate.common.Items
import net.shadowkat.minecraft.opensolidstate.common.Settings
import net.shadowkat.minecraft.opensolidstate.common.items.OssmSoc
import net.shadowkat.minecraft.opensolidstate.common.utils.Utils.ktPairList

class OssmSocCreate : ITemplate {
    /*val name = "ossm_soc_create1"
    val host : Class<Any>? = null
    val containerTiers = IntArray(0)
    val upgradeTiers = IntArray(1)
    val componentSlots = ktPairList(
        Slot.Card to 0,
        Slot.Memory to 0
    )

    @JvmStatic
    fun validate(inv: IInventory): Array<Any> {
        var count = 0
        for (i in 0 until inv.sizeInventory) {
            if (!inv.getStackInSlot(i).isEmpty) {
                println(inv.getStackInSlot(i).item.registryName)
                count++
            }
        }
        if (count > 1)
            return arrayOf(false, TextComponentTranslation("gui.ossm_too_many_embeded_devices", count, 1))
        return arrayOf(true)
    }

    @JvmStatic
    fun select(stk: ItemStack): Boolean {
        return try {
            //println(stk.unlocalizedName)
            stk.item.registryName == Items.SocTemplate.registryName && stk.metadata == 0
        } catch (e : Exception) {
            System.err.println(e.localizedMessage)
            false
        }
    }

    @JvmStatic
    fun assemble(inv: IInventory): Array<Any> {
        val itm = inv.getStackInSlot(0)
        val ossm_dat = itm.getSubCompound("oc:data")?.getCompoundTag("ossm:data")
        ossm_dat?.setBoolean("wipe", true)
        return arrayOf(itm)
    }*/
    private val u_tiers = arrayOf(
        intArrayOf(0),
        intArrayOf(1, 0),
        intArrayOf(2, 1),
        intArrayOf(2, 2, 2, 2, 2, 2, 2, 2, 2)
    )

    private val c_tiers = arrayOf(
        listOf(
            Slot.Card to 0
        ),
        listOf(
            Slot.Card to 1
        ),
        listOf(
            Slot.Card to 2,
            Slot.Card to 0
        ),
        listOf(
            Slot.Card to 2,
            Slot.Card to 2,
            Slot.Card to 2
        )
    )

    override val name: String = "ossm_soc_create"
    override val tier: Int
    override val tiers: Int = 4
    override val containerTiers: IntArray = intArrayOf()
    override val upgradeTiers: IntArray
        get() = u_tiers[tier]
    override val componentTiers: List<Pair<String, Int>>
        get() = c_tiers[tier]
    override val complexity: Int
        get() = Settings.items.socComplexity[tier]
    override val host: Class<*>? = null

    override fun select(stk: ItemStack): Boolean {
        return stk.item.registryName == Items.SocTemplate.registryName && stk.metadata == tier
    }

    override fun validate(inv: IInventory): Array<Any> {
        for (i in 1 until inv.sizeInventory) {
            if (inv.getStackInSlot(i).isEmpty) {
                return arrayOf(true)
            }
        }
        return arrayOf(true, TextComponentTranslation("gui.ossm_maybe_add_items"))
    }

    override fun assemble(inv: IInventory): Array<Any> {
        val hand = ItemStackHandler(upgradeTiers.size+componentTiers.size+1)
        val stk = ItemStack(Items.SoC, 1, tier)
        var pos = 0
        for (i in 1 until inv.sizeInventory) {
            if (!inv.getStackInSlot(i).isEmpty) {
                hand.setStackInSlot(pos, inv.getStackInSlot(i))
                pos++
            }
        }
        val comp = NBTTagCompound()
        comp.setTag("inventory", hand.serializeNBT())
        comp.setString("oc:archClass", Machine.LuaArchitecture::class.qualifiedName!!)
        comp.setTag("oc:data", NBTTagCompound())
        stk.tagCompound = comp
        println(stk)
        return arrayOf(stk)
    }

    constructor() {
        tier = 0
    }

    constructor(t : Int) {
        tier = t
    }
}