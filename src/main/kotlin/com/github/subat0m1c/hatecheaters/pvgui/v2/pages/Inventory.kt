package com.github.subat0m1c.hatecheaters.pvgui.v2.pages

import com.github.subat0m1c.hatecheaters.modules.ProfileViewer.maxRows
import com.github.subat0m1c.hatecheaters.pvgui.v2.Pages
import com.github.subat0m1c.hatecheaters.pvgui.v2.Pages.centeredText
import com.github.subat0m1c.hatecheaters.pvgui.v2.Pages.playClickSound
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.ButtonDSL
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.ItemStackGrid.renderItemStackGrid
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.Utils.fixFirstNine
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.Utils.getSubset
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.buttons
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.profileLazy
import com.github.subat0m1c.hatecheaters.utils.ApiUtils.itemStacks
import com.github.subat0m1c.hatecheaters.utils.ApiUtils.magicalPower
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.capitalizeWords
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.colorStat
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.colorize
import com.github.subat0m1c.hatecheaters.utils.ItemUtils.getMagicalPower
import me.odinmain.utils.floor
import me.odinmain.utils.render.*
import me.odinmain.utils.skyblock.lore
import net.minecraft.item.ItemStack
import kotlin.math.ceil
import kotlin.math.floor

object Inventory: Pages.PVPage("Inventory") {
    enum class InventoryPages(val page: InventoryPage) {
        Basic(Inventory.Basic),
        Wardrobe(Inventory.Wardrobe),
        Talismans(Inventory.Talismans),
        Backpacks(Inventory.Backpacks),
        EnderChest(Inventory.EnderChest),
    }

    private inline val currentInventory get() = InventoryPages.entries.find { it.name == buttons.getSelected }?.page ?: InventoryPages.Basic.page

    val separatorLineY: Double by lazy { lineY + pageHeight }

    val inventoryPageHeight = ((totalHeight-lineY * (2 + 6-1))*0.9)/ 6

    val inventoryAPI: Boolean by profileLazy { profile.inventory.eChestContents.itemStacks.isNotEmpty() }

    private val buttons: ButtonDSL<String> by profileLazy {
        buttons(
            Box(mainX, lineY, mainWidth, inventoryPageHeight), lineY, ot, default = "Basic",
            InventoryPages.entries.map { it.name }, 2, ct.button,
            ct.selected, ct.roundness, 1f,
        ) { onSelect { playClickSound() } }
    }

    override fun draw() {
        if (!inventoryAPI) return centeredText("Inventory API disabled!", mainCenterX, mainHeight/2, 4f, Color.RED, true)
        roundedRectangle(mainX, separatorLineY, mainWidth, ot, ct.line)

        buttons.draw()

        currentInventory.draw()
    }

    override fun mouseClick(x: Int, y: Int, button: Int) {
        buttons.click(mouseX.toInt(), mouseY.toInt(), button)
        currentInventory.mouseClick(x, y, button)
        super.mouseClick(x, y, button)
    }

    override fun init() {
        currentInventory.init()
        super.init()
    }

    val invArmor: List<ItemStack?> by profileLazy { profile.inventory.invArmor.itemStacks }
    val startY: Double by lazy { separatorLineY + lineY + ot }
    private val buttonHeight = (mainWidth - (lineY * 16))/18
    private val centerY: Double by lazy { (startY + buttonHeight + lineY) + (mainHeight - (startY + buttonHeight))/2 }

    abstract class InventoryPage: Pages.PVPage("InventoryPage") {}

    object Basic: InventoryPage() {
        private val inventory: List<ItemStack?> by profileLazy { invArmor.reversed() + listOf(null) + profile.inventory.equipment.itemStacks + fixFirstNine(profile.inventory.invContents.itemStacks) }
        private val centerY by lazy { startY + (mainHeight - ((separatorLineY)))/2 }

        override fun draw() =
            renderItemStackGrid(inventory, mainX, centerY.toInt(), mainWidth + lineY/2, 5, 9, lineY, mouseX.toInt(), mouseY.toInt(), ot, false) { (listOf(it.displayName) + it.lore) }
    }

    object Wardrobe: InventoryPage() {
        private val wardrobe: List<ItemStack?> by profileLazy { profile.inventory.wardrobeContents.itemStacks }
        private val equippedWardrobe: Int by profileLazy { profile.inventory.wardrobeEquipped?.let { it-1 } ?: -1 }

        private val buttons: ButtonDSL<Int> by profileLazy {
            buttons(
                Box(mainX, startY, mainWidth, buttonHeight), lineY, ot, default = 1,
                (1..ceil(wardrobe.size.toDouble()/36).toInt()).toList(), 2,
                ct.button, ct.selected, ct.roundness, 1f,
            ) { onSelect { playClickSound() } }
        }

        override fun draw() {
            buttons.draw()

            val onCurrentPage = equippedWardrobe in ((buttons.getSelected-1) *9)..<((buttons.getSelected)*9)
            val equipped = if (onCurrentPage) equippedWardrobe - (9 * (buttons.getSelected-1)) else -1
            renderItemStackGrid(getSubset(wardrobe, buttons.getSelected-1), mainX, centerY.toInt(), mainWidth+lineY/2, 4, 9, lineY, mouseX.toInt(), mouseY.toInt(), ot, wardrobe = equipped, invArmor = invArmor) { (listOf(it.displayName) + it.lore) }
        }

        override fun mouseClick(x: Int, y: Int, button: Int) {
            buttons.click(mouseX.toInt(), mouseY.toInt(), button)
        }
    }

    object Talismans: InventoryPage() {
        val talis: List<ItemStack> by profileLazy { profile.inventory.bagContents["talisman_bag"]?.itemStacks?.filterNotNull()?.sortedByDescending { it.getMagicalPower } ?: emptyList() }
        val magicPower: Int by profileLazy { profile.magicalPower }
        val mp: String by profileLazy { "§aSelected Power: §6${profile.accessoryBagStorage.selectedPower?.capitalizeWords() ?: "§cNone!"}" }
        val tunings: List<String> by profileLazy { profile.accessoryBagStorage.tuning.currentTunings.map { "${it.key.replace("_", " ").capitalizeWords().colorStat}§7: ${it.value.colorize(ceil(magicPower/10.0))}" } }
        val abiPhone: String by profileLazy { "§5Abicase: ${floor(profile.crimsonIsle.abiphone.activeContacts.size/2.0).toInt()}" }
        //val riftPrism: Boolean by profileLazy { profile.rift.access.consumedPrism } //todo implement

        val textList: List<String> by profileLazy { listOf("Magical Power: ${magicPower.colorize(1697)}", mp, abiPhone) + tunings }

        val pages: Int by profileLazy { ceil(talis.size.toDouble()/(maxRows*9)).toInt() }

        val separatorX = (mainX + mainWidth * 0.38).floor().toInt()

        val width = mainWidth - ((separatorX - mainX)) -lineY

        private val buttons: ButtonDSL<Int> by profileLazy {
            buttons(
                Box(separatorX + lineY, startY, width, buttonHeight), lineY, ot, default = 1,
                (1..pages).toList(), 2, ct.button,
                ct.selected, ct.roundness, 1f,
            ) { onSelect { playClickSound() } }
        }

        private val entryHeight: Double by profileLazy { (mainHeight - startY + lineY)/textList.size }

        override fun draw() {
            roundedRectangle(separatorX, startY, ot, mainHeight - startY + lineY, ct.line)

            buttons.draw()

            textList.forEachIndexed { i, text ->
                mcText(text, mainX, (startY + (entryHeight * i) + entryHeight/2) - ((getMCTextHeight() * 2)/2), 2, ct.font, shadow = true, center = false)
            }

            renderItemStackGrid(
                getSubset(talis, buttons.getSelected-1, maxRows*9), separatorX + lineY + ot, centerY.toInt(), width, maxRows, 9, lineY, mouseX.toInt(), mouseY.toInt(), ot,
            ) { (listOf("${it.displayName} §7(${it.getMagicalPower.colorize(22)}§7)") + it.lore) }
        }

        override fun mouseClick(x: Int, y: Int, button: Int) {
            buttons.click(mouseX.toInt(), mouseY.toInt(), button)
        }
    }

    object Backpacks: InventoryPage() {
        private val buttons: ButtonDSL<Int> by profileLazy {
            buttons(
                Box(mainX, startY, mainWidth, buttonHeight), lineY, ot, default = 1,
                (1..profile.inventory.backpackIcons.size).toList(), 2,
                ct.button, ct.selected, ct.roundness, 1f,
            ) { onSelect { playClickSound() } }
        }

        override fun draw() {
            buttons.draw()
            val inventory = profile.inventory.backpackContents["${buttons.getSelected-1}"]?.itemStacks ?: emptyList()
            renderItemStackGrid(
                inventory, (mainCenterX - (mainWidth * 0.8)/2).toInt(), centerY.toInt(), (mainWidth * 0.8).toInt(), (inventory.size + 8)/9, 9, lineY, mouseX.toInt(), mouseY.toInt(), ot
            ) { listOf(it.displayName) + it.lore}
        }

        override fun mouseClick(x: Int, y: Int, button: Int) {
            buttons.click(mouseX.toInt(), mouseY.toInt(), button)
        }
    }

    object EnderChest: InventoryPage() {

        val items: List<ItemStack?> by profileLazy { profile.inventory.eChestContents.itemStacks }
        val pages: Int by profileLazy { ceil(items.size/45.0).toInt() }

        private val buttons: ButtonDSL<Int> by profileLazy {
            buttons(
                Box(mainX, startY, mainWidth, buttonHeight), lineY, ot, default = 1,
                (1..pages).toList(), 2, ct.button,
                ct.selected, ct.roundness, 1f,
            ) { onSelect { playClickSound() } }
        }

        override fun draw() {
            buttons.draw()

            renderItemStackGrid(
                getSubset(items, buttons.getSelected-1, 45), (mainCenterX - (mainWidth * 0.8)/2).toInt(), centerY.toInt(), (mainWidth * 0.8).toInt(), 5, 9, lineY, mouseX.toInt(), mouseY.toInt(), ot,
            ) { (listOf(it.displayName) + it.lore) }
        }

        override fun mouseClick(x: Int, y: Int, button: Int) {
            buttons.click(mouseX.toInt(), mouseY.toInt(), button)
        }

    }
}