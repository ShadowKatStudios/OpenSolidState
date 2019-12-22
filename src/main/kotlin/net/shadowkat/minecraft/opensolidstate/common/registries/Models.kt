package net.shadowkat.minecraft.opensolidstate.common.registries

import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraftforge.client.model.ModelLoader
import net.shadowkat.minecraft.opensolidstate.OssmEvents

object Models {
    fun init() {
        for (itm in Items.items) {
            ModelLoader.setCustomModelResourceLocation(itm, 0, ModelResourceLocation(itm.registryName!!, "inventory"))
        }
    }
}