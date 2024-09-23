package com.github.subat0m1c.hatecheaters.pvgui.pages.inventory

import com.github.subat0m1c.hatecheaters.pvgui.*
import com.github.subat0m1c.hatecheaters.pvgui.PVGui.accent
import com.github.subat0m1c.hatecheaters.pvgui.PVGui.button
import com.github.subat0m1c.hatecheaters.pvgui.PVGui.font
import com.github.subat0m1c.hatecheaters.pvgui.PVGui.line
import com.github.subat0m1c.hatecheaters.pvgui.PVGui.main
import com.github.subat0m1c.hatecheaters.pvgui.PVGui.screen
import com.github.subat0m1c.hatecheaters.pvgui.PVGui.selected
import com.github.subat0m1c.hatecheaters.pvgui.pages.inventory.PageRendering.backPackClick
import com.github.subat0m1c.hatecheaters.pvgui.pages.inventory.PageRendering.backpackDraw
import com.github.subat0m1c.hatecheaters.pvgui.pages.inventory.PageRendering.inventoryDraw
import com.github.subat0m1c.hatecheaters.pvgui.pages.inventory.PageRendering.talismanClick
import com.github.subat0m1c.hatecheaters.pvgui.pages.inventory.PageRendering.talismanDraw
import com.github.subat0m1c.hatecheaters.pvgui.pages.inventory.PageRendering.wardrobeClick
import com.github.subat0m1c.hatecheaters.pvgui.pages.inventory.PageRendering.wardrobeDraw
import com.github.subat0m1c.hatecheaters.pvgui.pvutils.RenderUtils.isObjectHovered
import com.github.subat0m1c.hatecheaters.pvgui.pvutils.RenderUtils.somethingWentWrong
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.mcWidth
import com.github.subat0m1c.hatecheaters.utils.jsonobjects.HypixelProfileData.PlayerInfo
import me.odinmain.utils.render.*
import me.odinmain.utils.skyblock.modMessage
import kotlin.math.floor

object InventoryPage: PVGuiPage() {

    var currentInventory: String = "Talismans"

    data class BoxPosition(val name: String, val x: Double, val y: Double, val width: Double, val height: Double)
    val boxPositions = mutableListOf<BoxPosition>()

    override fun draw(screen: ScreenObjects, player: PlayerInfo) {

        player.profileData.profiles.find { it.selected }?.members?.get(player.uuid)?.let {
            val width = screen.mainWidth

            val toRender = mutableListOf<() -> Unit>()
            val pageHeight = ((screen.totalHeight-screen.lineY * (2 + 6-1))*0.9)/ 6 // same height as the page height of 6 pages

            val separatorLineY = screen.lineY*2 + pageHeight
            roundedRectangle(screen.mainX, separatorLineY, screen.mainWidth, screen.outlineThickness, line)

            val pageWidth = getBoxHeight(inventoryList.size, screen, width.toInt())
            val ot = screen.outlineThickness
            val lineY = screen.lineX + ot + screen.lineY
            inventoryList.forEachIndexed { i, entry ->
                val y = screen.lineY
                val x = lineY + ((pageWidth + screen.lineY) * i)
                roundedRectangle(x - ot, y - ot, pageWidth + ot * 2, floor(pageHeight + ot * 2), accent)
                if (currentInventory == entry) roundedRectangle(x, y, pageWidth, pageHeight, selected)
                else roundedRectangle(x, y, pageWidth, floor(pageHeight), button)

                val textWidth = getMCTextWidth(entry)
                val textScale = 3f
                val centerY = screen.lineY + pageHeight / 2
                val centerX = x + pageWidth / 2
                if (boxPositions.size <= i) boxPositions.add(BoxPosition(entry, x, y, pageWidth, floor(pageHeight)))
                else boxPositions[i] = BoxPosition(entry, x, y, pageWidth, floor(pageHeight))

                toRender.add {
                    //todo either fix being unable to render 2 inv in a single draw or ignore this todo
                    mcText(entry, centerX - (textWidth / 2) * textScale, centerY - (getMCTextHeight() * textScale) / 2, textScale, font, shadow = true, center = false)
                }
            }

            toRender.forEach {
                it.invoke()
            }

            when (currentInventory) {
                "Inventory" -> inventoryDraw(screen, it, (separatorLineY + screen.lineY + ot))
                "Wardrobe" -> wardrobeDraw(screen, it, (separatorLineY + screen.lineY + ot))
                "Talismans" -> talismanDraw(screen, it, (separatorLineY + screen.lineY + ot))
                "Backpacks" -> backpackDraw(screen, it, (separatorLineY + screen.lineY + ot))
            }
            return
        }

        somethingWentWrong(screen)
    }

    override fun mouseClick(x: Int, y: Int, button: Int) {
        boxPositions.find { isObjectHovered(it.x, it.y, it.width, it.height, screen ?: return) }?.let {
            modMessage(it.name)
            currentInventory = it.name
        }

        when (currentInventory) {
            "Wardrobe" -> wardrobeClick(button)
            "Talismans" -> talismanClick(button)
            "Backpacks" -> backPackClick(button)
        }
    }


    fun getBoxHeight(size: Int, screen: ScreenObjects, length: Int): Double {
        return (length - (size - 1) * screen.lineY) / size
    }

    val inventoryList = listOf(
        "Inventory",
        "Wardrobe",
        "Talismans",
        "Backpacks",
        "Ender Chest",
    )
}