package com.github.subat0m1c.hatecheaters.pvgui.v2.utils

import com.github.subat0m1c.hatecheaters.pvgui.v2.PVGui
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.Utils.getMouseX
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.Utils.getMouseY
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.Utils.isObjectHovered
import me.odinmain.OdinMain.mc
import me.odinmain.utils.render.*
import me.odinmain.utils.render.RenderUtils.bind
import me.odinmain.utils.skyblock.lore
import me.odinmain.utils.ui.Colors
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.GlStateManager.translate
import net.minecraft.client.renderer.GlStateManager.scale
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.item.ItemStack
import net.minecraftforge.client.event.GuiScreenEvent
import net.minecraftforge.fml.client.config.GuiUtils
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
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
    private var colorHandler: (index: Int, ItemStack?) -> Color = { _, _ -> Colors.WHITE }

    fun draw(mouseX: Int, mouseY: Int) {
        GlStateManager.pushMatrix()

        Colors.WHITE.bind()
        GlStateManager.enableRescaleNormal()
        RenderHelper.enableGUIStandardItemLighting()

        items.forEach { gridItems ->
            val itemWidth = (gridItems.width - (gridItems.columns - 1) * padding) / gridItems.columns.coerceAtLeast(1)

            gridItems.items.forEachIndexed { index, itemStack ->
                val x = gridItems.x + (index % gridItems.columns) * (itemWidth + padding)
                val height = ceil(gridItems.items.size.toDouble() / gridItems.columns) * (itemWidth + padding)
                val y = gridItems.centerY - (height / 2) + (index / gridItems.columns) * (itemWidth + padding)

                roundedRectangle(x, y, itemWidth, itemWidth, colorHandler(index, itemStack), radius, edgeSoftness)

                if (itemStack != null) {
                    GlStateManager.pushMatrix()
                    translate(x, y.toFloat(), 0f)
                    scale(itemWidth / 16f, itemWidth / 16f, 1f)
                    mc.renderItem.renderItemIntoGUI(itemStack, 0, 0)
                    mc.renderItem.renderItemOverlayIntoGUI(fontRenderer, itemStack, 0, 0, null)
                    GlStateManager.popMatrix()
                }

                if (isObjectHovered(Box(x, y, itemWidth, itemWidth), mouseX, mouseY)) hoveredItem = itemStack?.let { HoverItem(it, tooltipHandler) }
            }
        }

        RenderHelper.disableStandardItemLighting()
        GlStateManager.disableRescaleNormal()
        GlStateManager.popMatrix()
    }
    fun tooltipHandler(init: (ItemStack) -> List<String>) { tooltipHandler = init }

    fun colorHandler(init: (index: Int, ItemStack?) -> Color) { colorHandler = init }

    fun updateItems(newItems: List<ItemStack?>, index: Int = 0) { items[index].items = newItems }

    companion object {
        private val fontRenderer: FontRenderer = mc.fontRendererObj
        private var hoveredItem: HoverItem? = null

        @SubscribeEvent
        fun onGuiRender(event: GuiScreenEvent.DrawScreenEvent.Post) {
            if (event.gui != PVGui) return
            hoveredItem?.let { (item, tooltipHandler) ->
                // rendered outside inventory grid stack since the tooltip code has its own translation and scaling i dont wanna mess up
                GuiUtils.drawHoveringText(tooltipHandler(item), getMouseX.toInt(), getMouseY.toInt(), event.gui.width, event.gui.height, -1, fontRenderer)
            }
            hoveredItem = null
        }
    }
}

data class HoverItem(val item: ItemStack, val tooltipHandler: (ItemStack) -> List<String>)
data class GridItems(var items: List<ItemStack?>, val x: Int, val centerY: Int, val width: Int, val columns: Int)