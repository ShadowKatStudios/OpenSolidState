package net.shadowkat.minecraft.opensolidstate.client.renderer.tileentity

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.shadowkat.minecraft.opensolidstate.common.blocks.OssmRasterScreen
import net.shadowkat.minecraft.opensolidstate.common.tileentities.OssmRasterScreenEntity
import org.lwjgl.opengl.GL14
import org.lwjgl.opengl.GL11

class OssmRasterScreenRenderer : TileEntitySpecialRenderer<OssmRasterScreenEntity>() {
    private lateinit var screen : OssmRasterScreenEntity
    private fun transform() {

    }
    override fun render(te: OssmRasterScreenEntity, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int, alpha: Float) {
        super.render(te, x, y, z, partialTicks, destroyStage, alpha)

    }
}