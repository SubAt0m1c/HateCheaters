package com.github.subat0m1c.hatecheaters.pvgui.v2.utils

import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.Utils.isObjectHovered
import me.odinmain.OdinMain.mc
import me.odinmain.utils.render.*
import me.odinmain.utils.skyblock.lore
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.client.config.GuiUtils
import org.lwjgl.opengl.GL11
import kotlin.math.ceil

fun itemGrid(
    items: List<GridItems>,
    radius: Float = 0f,
    edgeSoftness: Float = 0f,
    padding: Float = 0f,
    itemGrid: ItemGridDSL.() -> Unit
) = ItemGridDSL(items, radius, edgeSoftness, padding).apply(itemGrid)

class ItemGridDSL(
    private val items: List<GridItems>,
    private val radius: Float,
    private val edgeSoftness: Float,
    private val padding: Float,
) {
    private var tooltipHandler: (ItemStack) -> List<String> = { listOf(it.displayName) + it.lore }
    private var colorHandler: (index: Int, ItemStack?) -> Color = { _, _ -> Color.WHITE }

    private val fontRenderer: FontRenderer = mc.fontRendererObj
    private var hoveredItem: ItemStack? = null

    fun draw(mouseX: Int, mouseY: Int) {
        GlStateManager.pushMatrix()

        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        RenderHelper.enableGUIStandardItemLighting()

        hoveredItem = null

        items.forEach { gridItems ->
            val itemWidth = (gridItems.width - (gridItems.columns - 1) * padding) / gridItems.columns.coerceAtLeast(1)

            gridItems.items.forEachIndexed { index, itemStack ->
                val x = gridItems.x + (index % gridItems.columns) * (itemWidth + padding)
                val height = ceil(gridItems.items.size.toDouble() / gridItems.columns) * (itemWidth + padding)
                val y = gridItems.centerY - (height / 2) + (index / gridItems.columns) * (itemWidth + padding)

                roundedRectangle(x, y, itemWidth, itemWidth, colorHandler(index, itemStack), radius, edgeSoftness)

                if (itemStack != null) {
                    GlStateManager.pushMatrix()
                    GlStateManager.translate(x, y.toFloat(), 0f)
                    GlStateManager.scale(itemWidth / 16f, itemWidth / 16f, 1f)
                    mc.renderItem.renderItemIntoGUI(itemStack, 0, 0)
                    mc.renderItem.renderItemOverlayIntoGUI(fontRenderer, itemStack, 0, 0, null)
                    GlStateManager.popMatrix()
                }

                if (isObjectHovered(Box(x, y, itemWidth, itemWidth), mouseX, mouseY)) {
                    hoveredItem = itemStack
                }
            }
        }

        hoveredItem?.let {
            GlStateManager.pushMatrix()
            translate(mouseX, mouseY, 0)
            scale(2f, 2f, 1f)
            GuiUtils.drawHoveringText(tooltipHandler(it), 0, 0, mc.displayWidth, mc.displayHeight, -1, fontRenderer)
            GlStateManager.popMatrix()
        }

        RenderHelper.disableStandardItemLighting()
        GlStateManager.disableRescaleNormal()
        GlStateManager.disableLighting()
        GL11.glDisable(GL11.GL_DEPTH_TEST)

        GlStateManager.popMatrix()
    }

    fun tooltipHandler(init: (ItemStack) -> List<String>) { tooltipHandler = init }

    fun colorHandler(init: (index: Int, ItemStack?) -> Color) { colorHandler = init }

    fun updateItems(newItems: List<ItemStack?>, index: Int = 0) { items[index].items = newItems }
}

data class GridItems(var items: List<ItemStack?>, val x: Int, val centerY: Int, val width: Int, val columns: Int)