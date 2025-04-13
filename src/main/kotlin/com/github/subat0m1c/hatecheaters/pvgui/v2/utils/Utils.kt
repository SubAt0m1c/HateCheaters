package com.github.subat0m1c.hatecheaters.pvgui.v2.utils

import com.github.subat0m1c.hatecheaters.utils.ChatUtils.capitalizeWords
import me.odinmain.OdinMain.mc
import me.odinmain.utils.render.Box
import me.odinmain.utils.render.RenderUtils.bind
import me.odinmain.utils.ui.Colors
import net.minecraft.client.gui.inventory.GuiInventory.drawEntityOnScreen
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.GlStateManager.translate
import net.minecraft.entity.EntityLivingBase
import org.lwjgl.input.Mouse

object Utils {

    inline val String.formatted get() =
        replace("_", " ").lowercase().capitalizeWords()

    /**
     * Takes the first 9 items in a list and moves them to the end.
     *
     * used because hypixel api returns hotbar in inventory list first and that looks awful
     */
    fun <T> fixFirstNine(list: List<T>): List<T> =
        if (list.size >= 9) list.subList(9, list.size) + list.subList(0, 9) else list


    fun <T> getSubset(lst: List<T>, index: Int, subsetSize: Int = 36): List<T> {
        val startIndex = index * subsetSize
        return lst.subList(startIndex.coerceAtMost(lst.size), (startIndex + subsetSize).coerceAtMost(lst.size))
    }

    fun <T> insertItemsAtIndexes(targetList: List<T>, items: List<Pair<Int, T>>): List<T> {
        val list = targetList.toMutableList()
        for ((index, item) in items) {
            list[index] = item
        }
        return list.toList()
    }

    //faster than filter i think probably maybe idk if it matters though

    fun <T> List<T>.without(vararg items: T): List<T> = with(this.toMutableList()) {
        items.forEach { this.remove(it) }
        return@with this
    }

    fun <K, V> Map<K, V>.without(vararg items: K): Map<K, V> = with(this.toMutableMap()) {
        items.forEach { this.remove(it) }
        return@with this
    }

    fun isObjectHovered(box: Box, mouseX: Number, mouseY: Number): Boolean =
        (mouseX.toDouble() in box.x.toDouble()..box.x.toDouble() + box.w.toDouble() && mouseY.toDouble() in box.y.toDouble()..box.y.toDouble()+box.h.toDouble())

    inline val getMouseX: Double get() = Mouse.getX() * mc.currentScreen.width.toDouble() / mc.displayWidth

    inline val getMouseY: Double get() = mc.currentScreen.height - Mouse.getY() * mc.currentScreen.height.toDouble() / mc.displayHeight - 1

    /**
     * Gets the height of a square array given width, rows, columns, and padding.
     *
     * use height and swap rows and columns to get the width given height.
     */
    fun getProperHeight(width: Int, rows: Int, columns: Int, padding: Int): Int =
        rows * ((width - (columns - 1) * padding) / columns) + (rows - 1) * padding

    fun getBoxHeight(size: Int, lineY: Number, length: Int) = (length - (size - 1) * lineY.toInt()) / size

    fun drawPlayerOnScreen(x: Double, y: Double, scale: Int, mouseX: Int, mouseY: Int, renderPlayer: EntityLivingBase) {
        GlStateManager.pushMatrix()
        Colors.WHITE.bind()
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
        translate(x, y, 200.0)
        drawEntityOnScreen(0, 0, scale, ((x*2)-mouseX).toFloat(), (mouseY-y).toFloat(), renderPlayer)
        GlStateManager.popMatrix()
    }
}