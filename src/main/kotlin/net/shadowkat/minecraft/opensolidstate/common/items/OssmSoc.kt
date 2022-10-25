package net.shadowkat.minecraft.opensolidstate.common.items

import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.items.ItemStackHandler
import net.shadowkat.minecraft.opensolidstate.OssmEvents
import net.shadowkat.minecraft.opensolidstate.common.Items
import net.shadowkat.minecraft.opensolidstate.server.drivers.FlashDriver
import net.shadowkat.minecraft.opensolidstate.server.drivers.SocDriver

class OssmSoc : OssmBaseItem() {

    override val lore: String = "A System on Chip (SoC) is a processor with embedded devices. Great for saving that one slot!"

    init {
        setRegistryName("ossm:soc")
        setHasSubtypes(true)
    }
    override fun addAdditionalInformation(
        stack: ItemStack,
        worldIn: World?,
        tooltip: MutableList<String>,
        flagIn: ITooltipFlag
    ) {
        val inv = ItemStackHandler()
        inv.deserializeNBT(stack.getSubCompound("inventory"))
        for (slot in 0 until inv.slots) {
            if (inv.getStackInSlot(slot).isEmpty) continue
            tooltip.add("- ${inv.getStackInSlot(slot).displayName}")
        }
    }
    override fun getData(stack: ItemStack): List<Pair<String, String>> {
        return listOf()
    }

    override fun getTier(stack: ItemStack): Int {
        return stack.metadata
    }

    override fun getUnlocalizedName(stack: ItemStack): String {
        val i = stack.metadata
        return "item.ossm_soc_${i}"
    }

    override fun register(event: RegistryEvent.Register<Item>) {
        event.registry.register(this)
        Items.SoC = this
        OssmEvents.model_register.add {
            li.cil.oc.api.Driver.add(SocDriver())
            for (i in 0..3) {
                //li.cil.oc.api.Driver.add(OssmEepromDriver(ItemStack(this, 1, i))) // i know, weird place to do this
                ModelLoader.setCustomModelResourceLocation(
                    this,
                    i,
                    ModelResourceLocation(
                        "ossm:soc_${i}",
                        "inventory"
                    )
                )
            }
        }
    }
}