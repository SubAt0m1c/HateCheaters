package com.github.subat0m1c.hatecheaters.pvgui.pages.inventory

import com.github.subat0m1c.hatecheaters.pvgui.*
import com.github.subat0m1c.hatecheaters.pvgui.PVGui.accent
import com.github.subat0m1c.hatecheaters.pvgui.PVGui.items
import com.github.subat0m1c.hatecheaters.pvgui.pvutils.RenderUtils.isObjectHovered
import com.github.subat0m1c.hatecheaters.utils.ApiUtils.itemStacks
import com.github.subat0m1c.hatecheaters.utils.jsonobjects.HypixelProfileData
import me.odinmain.OdinMain.mc
import me.odinmain.utils.render.Color
import me.odinmain.utils.render.roundedRectangle
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.client.config.GuiUtils
import org.lwjgl.opengl.GL11

object InventoryRender {

    data class ItemPosition(val itemStack: ItemStack, val x: Int, val y: Int, val width: Int, val height: Int)


    fun renderItemStackGrid(
        itemStacks: List<ItemStack?>,
        x: Int,
        yVal: Int,
        width: Int,
        rows: Int,
        columns: Int,
        padding: Double,
        screen: ScreenObjects,
        renderFifth: Boolean = true,
        wardrobe: Int = -1,
        player: HypixelProfileData.MemberData? = null,
        tooltipHandler: (ItemStack) -> List<String>,
    ) {
        val inventoryWithArmor = if (wardrobe != -1 && player != null) {
            val itemsMap = player.inventory.invArmor.itemStacks.reversed().mapIndexed { index, itemStack ->
                wardrobe + (9 * index) to itemStack
            }

            insertItemsAtIndexes(itemStacks, itemsMap)//.also { modMessage(it.indexOf(itemsMap.unzip().second.first())) }
        } else itemStacks



        val fontRenderer = mc.fontRendererObj

        val height = getProperHeight(width, rows, columns, padding.toInt())

        val y = yVal-height/2

        val itemWidth = (width - (columns -1) * padding) / columns
        val itemHeight = (height - (rows - 1) * padding) / rows

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

                    roundedRectangle(itemX-screen.outlineThickness, itemY-screen.outlineThickness, itemWidth+screen.outlineThickness*2, itemHeight+screen.outlineThickness*2, accent)
                    if (!equipped) (roundedRectangle(itemX, itemY, itemWidth, itemHeight, items))
                    else roundedRectangle(itemX, itemY, itemWidth, itemHeight, Color.BLUE)

                    itemStack?.let {
                        GlStateManager.pushMatrix()
                        GlStateManager.translate(itemX.toFloat(), itemY.toFloat(), 0f)
                        GlStateManager.scale(itemWidth.toFloat() / 16f, itemHeight.toFloat() / 16f, 1f)
                        mc.renderItem.renderItemIntoGUI(it, 0, 0)
                        mc.renderItem.renderItemOverlayIntoGUI(fontRenderer, it, 0, 0, null)
                        GlStateManager.popMatrix()

                        itemPositions.add(ItemPosition(it, itemX.toInt(), itemY.toInt(), itemWidth.toInt(), itemHeight.toInt()))
                        //GuiScreen.drawRect(itemX, itemY, itemX + itemWidth, itemY + itemHeight, Color(0, 0, 0, 100).rgb)
                    }
                }
            }
        }

        itemPositions.find { isObjectHovered(it.x.toDouble(), it.y.toDouble(), it.width.toDouble(), it.height.toDouble(), screen) }?.let {
            val tooltip = tooltipHandler(it.itemStack)

            //GlStateManager.pushMatrix()

            //scale(1.2f, 1.2f, 0f)
            GuiUtils.drawHoveringText(tooltip, screen.mouseX.toInt() + (screen.lineY.toInt()*3), screen.mouseY.toInt(), mc.displayWidth, mc.displayHeight, -1, fontRenderer)
            //GlStateManager.popMatrix()
        }

        RenderHelper.disableStandardItemLighting()
        GlStateManager.disableRescaleNormal()
        GlStateManager.disableLighting()
        GL11.glDisable(GL11.GL_DEPTH_TEST)
    }

    /**
     * Takes the first 9 items in a list and moves them to the end.
     *
     * used because hypixel api returns hotbar in inventory list first and that looks awful
     */
    fun <T> fixFirstNine(list: List<T>): List<T> {
        return if (list.size >= 9) {
            list.subList(9, list.size) + list.subList(0, 9)
        } else {
            list
        }
    }


    fun <T> getSubset(lst: List<T>, index: Int, subsetSize: Int = 36): List<T> {
        val startIndex = index * subsetSize
        val endIndex = startIndex + subsetSize
        return lst.subList(startIndex, endIndex.coerceAtMost(lst.size))
    }

    fun <T> insertItemsAtIndexes(targetList: List<T>, items: List<Pair<Int, T>>): List<T> {
        val list = targetList.toMutableList()
        for ((index, item) in items) {
            list[index] = item
        }
        return list.toList()
    }

    /**
     * Gets the height of a square array given width, rows, columns, and padding.
     *
     * use height and swap rows and columns to get the width given height.
     */
    fun getProperHeight(width: Int, rows: Int, columns: Int, padding: Int): Int {
        val itemWidth = (width - (columns - 1) * padding) / columns
        return rows * itemWidth + (rows - 1) * padding
    }
}