package net.shadowkat.minecraft.opensolidstate.common.items

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.resources.I18n
import net.minecraft.client.settings.GameSettings
import net.minecraft.client.settings.KeyBinding
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.item.EnumRarity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.shadowkat.minecraft.opensolidstate.common.utils.Utils

abstract class OssmBaseItem : Item() {
    @SideOnly(Side.CLIENT)
    abstract fun getData(stack : ItemStack) : List<Pair<String, String>>
    abstract fun getTier(stack : ItemStack) : Int
    var outdated = false

    abstract val lore : String

    override fun getRarity(stack: ItemStack): EnumRarity {
        val tier = getTier(stack)
        return Utils.getRarity(tier)
    }

    @SideOnly(Side.CLIENT)
    open fun addAdditionalInformation(stack: ItemStack, worldIn: World?, tooltip: MutableList<String>, flagIn: ITooltipFlag) {

    }

    @SideOnly(Side.CLIENT)
    override fun addInformation(stack: ItemStack, worldIn: World?, tooltip: MutableList<String>, flagIn: ITooltipFlag) {
        if (outdated) {
            var outdated_text = I18n.format("gui.ossm_tooltip_outdated")
            tooltip.add("§4§l$outdated_text§r")
        }
        val ossm_data = stack.getSubCompound("oc:data")
        if ((ossm_data != null) && ossm_data.hasKey("oc:label")) {
            tooltip.add(ossm_data.getString("oc:label"))
        }
        val sprintkey = Minecraft.getMinecraft().gameSettings.keyBindSneak
        if (GuiScreen.isShiftKeyDown()) {
            tooltip.add(lore)
            val data = getData(stack)
            for (pair in data) {
                tooltip.add("§7"+I18n.format("gui.ossm_itemprop_"+pair.first)+": §f"+pair.second+"§8")
            }
            addAdditionalInformation(stack, worldIn, tooltip, flagIn)
        } else {
            tooltip.add(I18n.format("gui.ossm_tooltip_expand", "LSHIFT"))
        }
        if (flagIn.isAdvanced) {
            if ((ossm_data != null) && ossm_data.hasKey("node")) {
                tooltip.add("§8${ossm_data.getCompoundTag("node").getString("address").substring(0, 13)}")
            }
        }
    }

    abstract fun register(event: RegistryEvent.Register<Item>)
}