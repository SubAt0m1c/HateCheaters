package com.github.subat0m1c.hatecheaters.pvgui.v2.utils

import com.github.subat0m1c.hatecheaters.pvgui.v2.PVGui
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.Utils.getMouseX
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.Utils.getMouseY
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.Utils.isObjectHovered
import com.github.subat0m1c.hatecheaters.utils.odinwrappers.Box
import com.github.subat0m1c.hatecheaters.utils.odinwrappers.Color
import com.github.subat0m1c.hatecheaters.utils.odinwrappers.Colors
import com.github.subat0m1c.hatecheaters.utils.odinwrappers.Shaders
import me.odinmain.OdinMain.mc
import me.odinmain.utils.skyblock.lore
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.item.ItemStack
import net.minecraftforge.client.event.GuiScreenEvent
import net.minecraftforge.fml.client.config.GuiUtils
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import kotlin.math.ceil

//todo: add scale here instead of using the one from PVGui
fun itemGrid(
    items: List<GridItems>,
    radius: Float = 0f,
    padding: Float = 0f,
    itemGrid: ItemGridDSL.() -> Unit
) = ItemGridDSL(items, radius, padding).apply(itemGrid)

class ItemGridDSL(
    private val items: List<GridItems>,
    private val radius: Float,
    private val padding: Float,
) {
    private var tooltipHandler: (ItemStack) -> List<String> = { listOf(it.displayName) + it.lore }
    private var colorHandler: (index: Int, ItemStack?) -> Color = { _, _ -> Colors.WHITE }

    // this gets translated with sx and sy before called since it doesnt have access to those values and i dont want to refactor it to add them. This kinda sucks for readability but its ok i think
    fun draw(mouseX: Int, mouseY: Int) {
        GlStateManager.pushMatrix()

        val toDraw = mutableListOf<DrawItem>()
        items.forEach { gridItems ->
            val itemWidth = (gridItems.width - (gridItems.columns - 1) * padding) / gridItems.columns.coerceAtLeast(1)

            gridItems.items.forEachIndexed { index, itemStack ->
                val x = gridItems.x + (index % gridItems.columns) * (itemWidth + padding)
                val height = ceil(gridItems.items.size.toDouble() / gridItems.columns) * (itemWidth + padding)
                val y = gridItems.centerY - (height / 2) + (index / gridItems.columns) * (itemWidth + padding)

                Shaders.rect(x, y, itemWidth, itemWidth, radius, colorHandler(index, itemStack))

                if (itemStack != null) toDraw.add(DrawItem(itemStack, x, y.toFloat(), itemWidth))

                if (isObjectHovered(Box(x, y, itemWidth, itemWidth), mouseX, mouseY)) hoveredItem = itemStack?.let { HoverItem(it, tooltipHandler) }
            }
        }

        Shaders.stopDraw()

        Colors.WHITE.bind()
        RenderHelper.enableGUIStandardItemLighting()
        for (item in toDraw) {
            GlStateManager.enableRescaleNormal()
            item.draw(fontRenderer)
        }
        RenderHelper.disableStandardItemLighting()

        GlStateManager.popMatrix()

        Shaders.startDraw()
    }
    fun tooltipHandler(init: (ItemStack) -> List<String>) { tooltipHandler = init }

    fun colorHandler(init: (index: Int, ItemStack?) -> Color) { colorHandler = init }

    fun updateItems(newItems: List<ItemStack?>, index: Int = 0) { items[index].items = newItems }

    companion object {
        private val fontRenderer: FontRenderer = mc.fontRendererObj
        private val sr get() = ScaledResolution(mc)
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

data class DrawItem(val item: ItemStack, val x: Float, val y: Float, val size: Float) {
    fun draw(font: FontRenderer) {
        GlStateManager.pushMatrix()
        GlStateManager.translate(x, y, 0f)
        GlStateManager.scale(size / 16f, size / 16f, 1f)
        mc.renderItem.renderItemAndEffectIntoGUI(item, 0, 0)
        mc.renderItem.renderItemOverlayIntoGUI(font, item, 0, 0, null)
        GlStateManager.popMatrix()
    }
}
data class HoverItem(val item: ItemStack, val tooltipHandler: (ItemStack) -> List<String>)
data class GridItems(var items: List<ItemStack?>, val x: Int, val centerY: Int, val width: Int, val columns: Int)