package net.shadowkat.minecraft.opensolidstate.common.items

import li.cil.oc.api.CreativeTab
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.event.RegistryEvent
import net.shadowkat.minecraft.opensolidstate.OpenSolidState
import net.shadowkat.minecraft.opensolidstate.OssmEvents
import net.shadowkat.minecraft.opensolidstate.common.Constants
import net.shadowkat.minecraft.opensolidstate.common.Items
import net.shadowkat.minecraft.opensolidstate.common.Settings
import net.shadowkat.minecraft.opensolidstate.common.utils.Utils
import net.shadowkat.minecraft.opensolidstate.server.drivers.EepromDriver
import net.shadowkat.minecraft.opensolidstate.server.drivers.FlashDriver

class OssmFlash : OssmBaseItem() {

    override val lore: String = "A flash device lets any device have writable memory! Perfect for saving your state!"
    init {
        setHasSubtypes(true)
        setRegistryName("ossm:flash")
        creativeTab = OpenSolidState.prox!!.CreativeTab
    }

    override fun getData(stack: ItemStack): List<Pair<String, String>> {
        val tier = getTier(stack)
        var blocks = Settings.storage.flashSizes[tier]
        var blksize = Settings.storage.flashBlksize
        if (stack.getSubCompound("oc:data")?.hasKey("blocks") == true)
            blocks = stack.getSubCompound("oc:data")!!.getInteger("blocks")
        if (stack.getSubCompound("oc:data")?.hasKey("blockSize") == true)
            blksize = stack.getSubCompound("oc:data")!!.getInteger("blockSize")
        return listOf(
            Constants.ObjectProperties.Storage to Utils.toBytes((blocks*blksize).toDouble(), 0),
            Constants.ObjectProperties.SectorSize to Utils.toBytes(blksize.toDouble(), 0))
    }

    override fun getSubItems(tab: CreativeTabs, items: NonNullList<ItemStack>) {
        if (!this.isInCreativeTab(tab)) return;
        for (i in 0..2) {
            items.add(ItemStack(this, 1, i))
        }
    }

    override fun getUnlocalizedName(stack: ItemStack): String {
        val i = stack.metadata
        return "item.ossm_flash_${i}"
    }

    override fun getTier(stack: ItemStack): Int {
        return stack.metadata
    }

    override fun register(event: RegistryEvent.Register<Item>) {
        event.registry.register(this)
        Items.Flash = this
        OssmEvents.model_register.add {
            li.cil.oc.api.Driver.add(FlashDriver())
            for (i in 0..2) {
                //li.cil.oc.api.Driver.add(OssmEepromDriver(ItemStack(this, 1, i))) // i know, weird place to do this
                ModelLoader.setCustomModelResourceLocation(
                    this,
                    i,
                    ModelResourceLocation(
                        "ossm:flash_${i}",
                        "inventory"
                    )
                )
            }
        }
    }

}