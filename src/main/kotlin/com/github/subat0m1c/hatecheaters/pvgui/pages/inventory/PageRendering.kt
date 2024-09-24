package com.github.subat0m1c.hatecheaters.pvgui.pages.inventory

import com.github.subat0m1c.hatecheaters.modules.ProfileViewer.maxRows
import com.github.subat0m1c.hatecheaters.pvgui.PVGui.accent
import com.github.subat0m1c.hatecheaters.pvgui.PVGui.button
import com.github.subat0m1c.hatecheaters.pvgui.PVGui.font
import com.github.subat0m1c.hatecheaters.pvgui.PVGui.line
import com.github.subat0m1c.hatecheaters.pvgui.PVGui.main
import com.github.subat0m1c.hatecheaters.pvgui.PVGui.screen
import com.github.subat0m1c.hatecheaters.pvgui.PVGui.selected
import com.github.subat0m1c.hatecheaters.pvgui.pages.inventory.InventoryPage.BoxPosition
import com.github.subat0m1c.hatecheaters.pvgui.pages.inventory.InventoryPage.getBoxHeight
import com.github.subat0m1c.hatecheaters.pvgui.pages.inventory.InventoryRender.fixFirstNine
import com.github.subat0m1c.hatecheaters.pvgui.pages.inventory.InventoryRender.getSubset
import com.github.subat0m1c.hatecheaters.pvgui.pages.inventory.InventoryRender.renderItemStackGrid
import com.github.subat0m1c.hatecheaters.pvgui.pvutils.RenderUtils.isObjectHovered
import com.github.subat0m1c.hatecheaters.pvgui.ScreenObjects
import com.github.subat0m1c.hatecheaters.pvgui.taliData
import com.github.subat0m1c.hatecheaters.utils.ApiUtils.itemStacks
import com.github.subat0m1c.hatecheaters.utils.ApiUtils.magicalPower
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.capitalizeWords
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.colorStat
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.colorize
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.mcWidth
import com.github.subat0m1c.hatecheaters.utils.ItemUtils.getMagicalPower
import com.github.subat0m1c.hatecheaters.utils.jsonobjects.HypixelProfileData
import com.github.subat0m1c.hatecheaters.utils.jsonobjects.HypixelProfileData.MemberData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.odinmain.utils.render.*
import me.odinmain.utils.skyblock.PlayerUtils.playLoudSound
import me.odinmain.utils.skyblock.lore
import me.odinmain.utils.skyblock.modMessage
import net.minecraft.item.ItemStack
import kotlin.math.ceil
import kotlin.math.floor

object PageRendering {

    val backpackToRender = mutableListOf<() -> Unit>()
    var currentBackpack = 0
    val backpackBoxes = mutableListOf<BoxPosition>()

    fun backpackDraw(screen: ScreenObjects, player: MemberData, startY: Double) {
        val lineX = screen.mainCenterX - (screen.outlineThickness / 2)

        val width = screen.mainWidth
        val height = getBoxHeight((18), screen, width.toInt())
        val ot = screen.outlineThickness
        val lineY = screen.mainX

        val invY = startY + height + screen.lineY
        //roundedRectangle(screen.mainX, invY, screen.mainWidth, screen.mainHeight - invY + screen.lineY, line)

        val centerY = invY + (screen.mainHeight - invY + screen.lineY)/2

        player.inventory.backpackIcons.entries.forEachIndexed { i, entry ->
            val y = startY
            val x = lineY + ((height + screen.lineY) * (i))
            roundedRectangle(x - ot, y - ot, height + ot * 2, floor(height + ot * 2), accent, radius = 10f, edgeSoftness = 1f)
            if (currentBackpack == i) roundedRectangle(x, y, height, height, selected, radius = 10f, edgeSoftness = 1f)
            else roundedRectangle(x, y, height, floor(height), button, radius = 10f, edgeSoftness = 1f)

            if (backpackBoxes.size <= i) backpackBoxes.add(BoxPosition(i.toString(), x, y, height, floor(height)))
            else backpackBoxes[i] = BoxPosition(i.toString(), x, y, height, floor(height))
            val centerX = x + (height/2) - (("${i+1}".mcWidth*3)/2)
            val centerY = y + (height/2) - ((getMCTextHeight()*3)/2)
            mcText((i+1).toString(), centerX, centerY, 3f, font, center = false)
        }

        val invWidth = screen.mainWidth * 0.8
        val inventory = player.inventory.backpackContents[currentBackpack.toString()]?.itemStacks ?: emptyList()
        renderItemStackGrid(inventory, (screen.mainCenterX - invWidth/2).toInt(), centerY.toInt(), invWidth.toInt(), (inventory.size + 8)/9, 9, screen.lineY, screen) { (listOf(it.displayName) + it.lore) }
        return
    }

    fun backPackClick(type: Int) {
        backpackBoxes.find { isObjectHovered(it.x, it.y, it.height, it.height, screen ?: return) }?.let {
            if (currentBackpack == it.name.toInt()) return
            playLoudSound("gui.button.press", 0.5f, 1.1f)
            currentBackpack = it.name.toInt()
        }
    }

    fun inventoryDraw(screen: ScreenObjects, player: MemberData, startY: Double) {
        //val width = getProperHeight(pageHeight.toInt(), 2, 4, screen.lineY.toInt())
        val centerY = startY + (screen.mainHeight - startY + screen.lineY)/2
        val inventory = player.inventory.invArmor.itemStacks.reversed() + listOf(null) + player.inventory.equipment.itemStacks + fixFirstNine(player.inventory.invContents.itemStacks)
        renderItemStackGrid(inventory, screen.mainX.toInt(), centerY.toInt(), screen.mainWidth.toInt(), 5, 9, screen.lineY, screen, false) { (listOf(it.displayName) + it.lore) }
    }

    val wardrobeButtons = mutableListOf<BoxPosition>()
    var currentWardrobe: Int = 0

    fun wardrobeDraw(screen: ScreenObjects, player: MemberData, startY: Double) {
        val inventory = player.inventory.wardrobeContents.itemStacks

        val buttonHeight = getBoxHeight((18), screen, screen.mainWidth.toInt()) //same height as backpack height
        val buttonWidth = (screen.mainWidth-screen.lineY)/ceil(inventory.size.toDouble()/36)

        val ot = screen.outlineThickness
        (0..<ceil(inventory.size.toDouble()/36).toInt()).forEach {
            val x = screen.mainX + ((buttonWidth + screen.lineY) * (it))
            roundedRectangle(x - ot, startY - ot, buttonWidth + ot * 2, floor(buttonHeight + ot * 2), accent, radius = 10f, edgeSoftness = 1f)
            if (currentWardrobe == it) roundedRectangle(x, startY, buttonWidth, floor(buttonHeight), selected, radius = 10f, edgeSoftness = 1f)
            else roundedRectangle(x, startY, buttonWidth, floor(buttonHeight), button, radius = 10f, edgeSoftness = 1f)

            if (wardrobeButtons.size <= it) wardrobeButtons.add(BoxPosition(it.toString(), x, startY, buttonWidth, floor(buttonHeight)))
            else wardrobeButtons[it] = BoxPosition(it.toString(), x, startY, buttonWidth, floor(buttonHeight))
        }

        val centerY = (startY + buttonHeight + screen.lineY) + (screen.mainHeight - (startY + buttonHeight))/2
        val equippedWardrobe = player.inventory.wardrobeEquipped?.let { it-1 } ?: -1
        val onCurrentPage = equippedWardrobe in (currentWardrobe*9)..<((currentWardrobe+1)*9)
        val equipped = if (onCurrentPage) equippedWardrobe - (9 * currentWardrobe) else -1
        renderItemStackGrid(getSubset(inventory, currentWardrobe), screen.mainX.toInt(), centerY.toInt(), screen.mainWidth.toInt(), 4, 9, screen.lineY, screen, wardrobe = equipped, player = player) { (listOf(it.displayName) + it.lore) }
    }

    fun wardrobeClick(button: Int) {
        wardrobeButtons.find { isObjectHovered(it.x, it.y, it.width, it.height, screen ?: return) }?.let {
            if (currentWardrobe == it.name.toInt()) return
            currentWardrobe = it.name.toInt()
            playLoudSound("gui.button.press", 0.5f, 1.1f)
        }
    }

    val talismanButtons = mutableListOf<BoxPosition>()
    var currentTaliPage = 0

    var taliItems: taliData? = null

    fun talismanDraw(screen: ScreenObjects, player: MemberData, startY: Double) {
        taliItems?.let {
            val talis = it.talis
            val magicalPowerText = it.mp
            val selectedPowerText = it.selected
            val tuningText = it.tuningList
            val abiphoneText = it.abiphone

            val textList = listOf(magicalPowerText, selectedPowerText, abiphoneText) + tuningText

            val entryHeight = (screen.mainHeight - startY + screen.lineY)/textList.size

            val itemMax = maxRows*9

            val pages = ceil(talis.size.toDouble()/itemMax).toInt()

            val lineX = floor(screen.mainX + (screen.mainWidth * 0.38))
            val startX = lineX+screen.lineY
            val width = screen.mainWidth - (lineX - screen.mainX + screen.lineY)//+screen.outlineThickness + screen.lineY)

            val buttonHeight = getBoxHeight((18), screen, screen.mainWidth.toInt()) //same height as backpack height
            val buttonWidth = getBoxHeight(pages, screen, width.toInt())

            val ot = screen.outlineThickness

            roundedRectangle(lineX, startY, screen.outlineThickness, (screen.mainHeight - startY + screen.lineY), line)

            (0..<pages).forEach {
                val x = startX + ((buttonWidth + screen.lineY) * (it))
                roundedRectangle(x - ot, startY - ot, buttonWidth + ot * 2, floor(buttonHeight + ot * 2), accent, radius = 10f, edgeSoftness = 1f)
                if (currentTaliPage == it) roundedRectangle(x, startY, buttonWidth, floor(buttonHeight), selected, radius = 10f, edgeSoftness = 1f)
                else roundedRectangle(x, startY, buttonWidth, floor(buttonHeight), button, radius = 10f, edgeSoftness = 1f)

                if (talismanButtons.size <= it) talismanButtons.add(BoxPosition(it.toString(), x, startY, buttonWidth, floor(buttonHeight)))
                else talismanButtons[it] = BoxPosition(it.toString(), x, startY, buttonWidth, floor(buttonHeight))
            }

            textList.forEachIndexed { i, text ->
                mcText(text, screen.mainX, (startY + (entryHeight * i) + entryHeight/2) - ((getMCTextHeight()*3f)/2), 3f, font, center = false)
            }

            roundedRectangle(lineX, startY, screen.outlineThickness, (screen.mainHeight - startY + screen.lineY), line)
            val centerY = (startY + buttonHeight + screen.lineY) + (screen.mainHeight - (startY + buttonHeight))/2
            renderItemStackGrid(getSubset(talis.sortedByDescending { it.getMagicalPower }, currentTaliPage, itemMax), startX.toInt(), centerY.toInt(), (width).toInt(), maxRows, 9, screen.lineY, screen) { (listOf("${it.displayName} §7(${it.getMagicalPower.colorize(22)}§7)") + it.lore) }
            return
        }
        val text = "Loading talisman data..."
        val text2 = "Please report this if you've waited more than 5 seconds."
        val centerY = screen.mainCenterY
        val fontScale = 3f
        mcText(text, screen.mainCenterX - ((text.mcWidth * fontScale) / 2), centerY - ((getMCTextHeight() * fontScale)), fontScale, font, center = false)
        mcText(text2, screen.mainCenterX - ((text2.mcWidth * fontScale) / 2), centerY + ((getMCTextHeight() * fontScale)), fontScale, font, center = false)
    }

    fun talismanClick(button: Int) {
        talismanButtons.find { isObjectHovered(it.x, it.y, it.width, it.height, screen ?: return) }?.let {
            if (currentTaliPage == it.name.toInt()) return
            currentTaliPage = it.name.toInt()
            playLoudSound("gui.button.press", 0.5f, 1.1f)
        }
    }

    suspend fun getTaliData(data: HypixelProfileData.PlayerInfo): Unit = withContext(Dispatchers.Default) {
        data.profileData.profiles.find { it.selected }?.members?.get(data.uuid)?.let { player ->
            val talis = player.inventory.bagContents["talisman_bag"]?.itemStacks?.filterNotNull() ?: emptyList()
            val magicalPowerText = "Magical Power: ${player.magicalPower.colorize(1697)}"
            val selectedPowerText = "§aSelected Power: §6${player.accessoryBagStorage.selectedPower?.capitalizeWords() ?: "§cNone!"}"
            val tuningText = player.accessoryBagStorage.tuning.currentTunings.map { "${it.key.replace("_", " ").capitalizeWords().colorStat}§7: ${it.value.colorize(ceil(player.magicalPower/10.0))}" }
            val abiphoneText = "§5Abicase: ${floor(player.crimsonIsle.abiphone.activeContacts.size/2.0).toInt()}"
            val riftPrism = player.rift.access.consumedPrism
            taliItems = taliData(
                magicalPowerText,
                tuningText,
                selectedPowerText,
                talis,
                abiphoneText,
                riftPrism
            )

        }
    }
}