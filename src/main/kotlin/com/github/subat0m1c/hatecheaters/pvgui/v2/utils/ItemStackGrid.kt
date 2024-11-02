package com.github.subat0m1c.hatecheaters.pvgui.v2.utils

import com.github.subat0m1c.hatecheaters.modules.ProfileViewer
import com.github.subat0m1c.hatecheaters.modules.ProfileViewer.rarityBackgrounds
import com.github.subat0m1c.hatecheaters.modules.ProfileViewer.scale
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.Utils.getProperHeight
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.Utils.insertItemsAtIndexes
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.Utils.isObjectHovered
import me.odinmain.OdinMain.mc
import me.odinmain.utils.render.*
import me.odinmain.utils.skyblock.getRarity
import me.odinmain.utils.skyblock.lore
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.client.config.GuiUtils
import org.lwjgl.opengl.GL11

object ItemStackGrid {
    data class ItemPosition(val itemStack: ItemStack, val x: Int, val y: Int, val width: Int, val height: Int)

    private val fontRenderer: FontRenderer = mc.fontRendererObj

    fun renderItemStackGrid(
        itemStacks: List<ItemStack?>,
        x: Int,
        yVal: Int,
        width: Int,
        rows: Int,
        columns: Int,
        padding: Int,
        mouseX: Int,
        mouseY: Int,
        ot: Int,
        renderFifth: Boolean = true,
        wardrobe: Int = -1,
        invArmor: List<ItemStack?> = emptyList(),
        tooltipHandler: (ItemStack) -> List<String>,
    ) {
        val inventoryWithArmor = if (wardrobe != -1 && invArmor.isNotEmpty()) {
            val itemsMap = invArmor.reversed().mapIndexed { index, itemStack ->
                wardrobe + (9 * index) to itemStack
            }

            insertItemsAtIndexes(itemStacks, itemsMap)
        } else itemStacks

        val height = getProperHeight(width, rows, columns, padding)

        val y = yVal-height/2

        val itemWidth = (width - (columns - 1) * padding) / columns.coerceAtLeast(1)
        val itemHeight = (height - (rows - 1) * padding) / rows.coerceAtLeast(1)

        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        RenderHelper.enableGUIStandardItemLighting()

        val itemPositions = mutableListOf<ItemPosition>()

        var index = 0
        for (rowIndex in 0 until rows) {
            for (columnIndex in 0 until columns) {
                val equipped = columnIndex == wardrobe
                with(this) {
                    val itemStack = inventoryWithArmor.getOrNull(index++)
                    if (!renderFifth && index == 5) return@with
                    val itemX = x + columnIndex * (itemWidth + padding)
                    val itemY = y + rowIndex * (itemHeight + padding)

                    val color = if (rarityBackgrounds) itemStack?.lore?.let { getRarity(it) }?.color ?: ProfileViewer.currentTheme.items else ProfileViewer.currentTheme.items

                    //roundedRectangle(itemX-ot, itemY-screen.outlineThickness, itemWidth+screen.outlineThickness*2, itemHeight+screen.outlineThickness*2, accent, radius = buttonRound, edgeSoftness = 1f)
                    if (!equipped) roundedRectangle(itemX, itemY, itemWidth, itemHeight, color, radius = ProfileViewer.currentTheme.roundness, edgeSoftness = 1f)
                    else roundedRectangle(itemX, itemY, itemWidth, itemHeight, Color.BLUE, radius = ProfileViewer.currentTheme.roundness, edgeSoftness = 1f)

                    itemStack?.let {
                        GlStateManager.pushMatrix()
                        GlStateManager.translate(itemX.toFloat(), itemY.toFloat(), 0f)
                        GlStateManager.scale(itemWidth.toFloat() / 16f, itemHeight.toFloat() / 16f, 1f)
                        mc.renderItem.renderItemIntoGUI(it, 0, 0)
                        mc.renderItem.renderItemOverlayIntoGUI(fontRenderer, it, 0, 0, null)
                        GlStateManager.popMatrix()

                        itemPositions.add(ItemPosition(it, itemX, itemY, itemWidth, itemHeight))
                        //GuiScreen.drawRect(itemX, itemY, itemX + itemWidth, itemY + itemHeight, Color(0, 0, 0, 100).rgb)
                    }
                }
            }
        }

        itemPositions.find { isObjectHovered(Box(it.x, it.y, it.width, it.height), mouseX, mouseY) }?.let {
            val tooltip = tooltipHandler(it.itemStack)

            GlStateManager.pushMatrix()


            translate(mouseX + (padding*3), mouseY)
            scale(2f, 2f)
            scale(scale, scale)
            GuiUtils.drawHoveringText(tooltip, 0, 0, mc.displayWidth, mc.displayHeight, -1, fontRenderer)
            GlStateManager.popMatrix()
        }

        RenderHelper.disableStandardItemLighting()
        GlStateManager.disableRescaleNormal()
        GlStateManager.disableLighting()
        GL11.glDisable(GL11.GL_DEPTH_TEST)
    }
}