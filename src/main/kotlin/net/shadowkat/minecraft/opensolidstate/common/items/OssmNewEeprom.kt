package net.shadowkat.minecraft.opensolidstate.common.items

import li.cil.oc.api.CreativeTab
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.resources.I18n
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList
import net.minecraft.world.World
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.event.RegistryEvent
import net.shadowkat.minecraft.opensolidstate.OssmEepromDriver
import net.shadowkat.minecraft.opensolidstate.OssmEvents
import net.shadowkat.minecraft.opensolidstate.common.Constants
import net.shadowkat.minecraft.opensolidstate.common.Settings
import net.shadowkat.minecraft.opensolidstate.common.utils.Utils
import net.shadowkat.minecraft.opensolidstate.server.drivers.EepromDriver

class OssmNewEeprom() : OssmBaseItem() {
    init {
        setHasSubtypes(true)
        setRegistryName("ossm:prom")
        creativeTab = CreativeTab.instance
    }

    override fun getSubItems(tab: CreativeTabs, items: NonNullList<ItemStack>) {
        if (!this.isInCreativeTab(tab)) return;
        for (i in 0..8) {
            items.add(ItemStack(this, 1, i))
        }
    }

    override fun addAdditionalInformation(stack: ItemStack, worldIn: World?, tooltip: MutableList<String>, flagIn: ITooltipFlag) {
        val tier = getTier(stack)
        if (tier > 0) {
            tooltip.add("§f§i"+ I18n.format("gui.ossm_itemprop_electronic_erase")+"§r§8")
        }
    }

    override fun register(event: RegistryEvent.Register<Item>) {
        event.registry.register(this)
        OssmEvents.model_register.add {
            li.cil.oc.api.Driver.add(EepromDriver())
            for (i in 0..8) {
                //li.cil.oc.api.Driver.add(OssmEepromDriver(ItemStack(this, 1, i))) // i know, weird place to do this
                ModelLoader.setCustomModelResourceLocation(
                    this,
                    i,
                    ModelResourceLocation(
                        "ossm:prom_${i / 3}${if (i % 3 > 0) "_" + Constants.upgradeTypes[i % 3] else ""}",
                        "inventory"
                    )
                )
            }
        }
    }

    override fun getData(stack: ItemStack): List<Pair<String, String>> {
        val tier = getTier(stack)
        var blocks = Settings.storage.eepromSizes[tier]
        var blksize = Settings.storage.eepromBlksize
        if (stack.getSubCompound("oc:data")?.hasKey("blocks") == true)
            blocks = stack.getSubCompound("oc:data")!!.getInteger("blocks")
        if (stack.getSubCompound("oc:data")?.hasKey("blockSize") == true)
            blksize = stack.getSubCompound("oc:data")!!.getInteger("blockSize")
        return listOf(
            Constants.ObjectProperties.Storage to Utils.toBytes((blocks*blksize).toDouble(), 0),
            Constants.ObjectProperties.SectorSize to Utils.toBytes(blksize.toDouble(), 0))
    }

    override fun getTier(stack: ItemStack): Int {
        return stack.metadata/3
    }

    override fun getUnlocalizedName(stack: ItemStack): String {
        val i = stack.metadata
        return "item.ossm_prom_${i/3}${if (i%3 > 0) "_"+Constants.upgradeTypes[i%3] else ""}"
    }

}