package com.github.subat0m1c.hatecheaters.pvgui.v2.pages

import com.github.subat0m1c.hatecheaters.modules.ProfileViewer.maxRows
import com.github.subat0m1c.hatecheaters.modules.ProfileViewer.rarityBackgrounds
import com.github.subat0m1c.hatecheaters.pvgui.v2.Pages
import com.github.subat0m1c.hatecheaters.pvgui.v2.Pages.centeredText
import com.github.subat0m1c.hatecheaters.pvgui.v2.Pages.playClickSound
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.*
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.Utils.fixFirstNine
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.Utils.getSubset
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.Utils.insertItemsAtIndexes
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.capitalizeWords
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.colorStat
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.colorize
import com.github.subat0m1c.hatecheaters.utils.ItemUtils.getMagicalPower
import com.github.subat0m1c.hatecheaters.utils.apiutils.ApiUtils.itemStacks
import com.github.subat0m1c.hatecheaters.utils.apiutils.ApiUtils.magicalPower
import me.odinmain.utils.render.*
import me.odinmain.utils.skyblock.getRarity
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

    private val inventoryAPI: Boolean by profileLazy { profile.inventory.eChestContents.itemStacks.isNotEmpty() }

    private val buttons: ButtonDSL<String> by profileLazy {
        val inventoryPageHeight = ((totalHeight-lineY * (2 + 6-1))*0.9)/ 6

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

    abstract class InventoryPage : Pages.PVPage("InventoryPage") {}

    object Basic: InventoryPage() {
        private val itemGrid: ItemGridDSL by profileLazy {
            val inventory = invArmor.reversed() + listOf(null) + profile.inventory.equipment.itemStacks + fixFirstNine(profile.inventory.invContents.itemStacks)

            itemGrid(listOf(GridItems(inventory, mainX, (startY + (mainHeight - ((separatorLineY)))/2).toInt(), mainWidth + lineY / 2, 9)), ct.roundness, 1f, lineY.toFloat()) {
                colorHandler { i, item ->
                    when {
                        i == 4 -> Color.TRANSPARENT // this should be replaced by separate inventory grids at some point
                        rarityBackgrounds -> item?.lore?.let { getRarity(it) }?.color ?: ct.items
                        else -> ct.items
                    }
                }
            }
        }

        override fun draw() = itemGrid.draw(mouseX.toInt(), mouseY.toInt())
    }

    object Wardrobe: InventoryPage() {
        private val wardrobe: List<ItemStack?> by profileLazy { profile.inventory.wardrobeContents.itemStacks }

        private val selectedButtonIndex: Int get() = buttons.getSelected - 1
        private inline val gridItems get() = listOf(GridItems(getSubset(inventoryWithArmor, selectedButtonIndex), mainX, centerY.toInt(), mainWidth + lineY / 2, 9))

        private val equipped: Int by profileLazy {
            val equippedWardrobe = profile.inventory.wardrobeEquipped?.let { it - 1 } ?: -1
            val rangeStart = (selectedButtonIndex * 9)
            val rangeEnd = rangeStart + 9

            if (equippedWardrobe in rangeStart until rangeEnd) equippedWardrobe - rangeStart else -1
        }

        private val inventoryWithArmor: List<ItemStack?> by profileLazy {
            if (equipped != -1 && invArmor.isNotEmpty()) { // this is awful i think maybe
                invArmor.mapIndexed { index, itemStack ->
                    (equipped + (9 * (invArmor.size - 1 - index))) to itemStack
                }.let { insertItemsAtIndexes(wardrobe, it) }
            } else wardrobe
        }

        private val itemGrid: ItemGridDSL by profileLazy {
            itemGrid(gridItems, ct.roundness, 1f, lineY.toFloat()) {
                val armor = invArmor.toSet()
                colorHandler { _, item -> if (item in armor) Color.BLUE else ct.items }
            }
        }

        private val buttons: ButtonDSL<Int> by profileLazy {
            buttons(
                Box(mainX, startY, mainWidth, buttonHeight), lineY, ot, default = 1,
                (1..ceil(wardrobe.size.toDouble() / 36).toInt()).toList(), 2,
                ct.button, ct.selected, ct.roundness, 1f,
            ) {
                onSelect {
                    itemGrid.updateItems(getSubset(inventoryWithArmor, selectedButtonIndex))
                    playClickSound()
                }
            }
        }

        override fun draw() {
            buttons.draw()
            itemGrid.draw(mouseX.toInt(), mouseY.toInt())
        }

        override fun mouseClick(x: Int, y: Int, button: Int) = buttons.click(mouseX.toInt(), mouseY.toInt(), button)
    }

    object Talismans: InventoryPage() {
        private val talis: List<ItemStack> by profileLazy { profile.inventory.bagContents["talisman_bag"]?.itemStacks?.filterNotNull()?.sortedByDescending { it.getMagicalPower } ?: emptyList() }
        private val magicPower: Int by profileLazy { profile.magicalPower }
        private val mp: String by profileLazy { "§aSelected Power: §6${profile.accessoryBagStorage.selectedPower?.capitalizeWords() ?: "§cNone!"}" }
        private val tunings: List<String> by profileLazy { profile.accessoryBagStorage.tuning.currentTunings.map { "${it.key.replace("_", " ").capitalizeWords().colorStat}§7: ${it.value.colorize(ceil(magicPower/10.0))}" } }
        private val abiPhone: String by profileLazy { "§5Abicase: ${floor(profile.crimsonIsle.abiphone.activeContacts.size/2.0).toInt()}" }
        private val riftPrism: String by profileLazy { "§dRift Prism: ${ if (profile.rift.access.consumedPrism) "§aObtained" else "§cMissing"}" }

        private val textList: List<String> by profileLazy { listOf("Magical Power: ${magicPower.colorize(1697)}", mp, abiPhone, riftPrism) + tunings }
        private val pages: Int by profileLazy { ceil(talis.size.toDouble()/(maxRows*9)).toInt() }

        private val separatorX = floor(mainX + mainWidth * 0.38).toInt()
        private val width = mainWidth - ((separatorX - mainX)) - lineY

        private inline val gridItems get() = listOf(GridItems(getSubset(talis, buttons.getSelected-1, maxRows*9), separatorX + lineY + ot, centerY.toInt(), width, 9,))

        private val itemGrid: ItemGridDSL by profileLazy {
            itemGrid(gridItems, ct.roundness, 1f, lineY.toFloat(),) {
                colorHandler { _, item ->
                    if (rarityBackgrounds) item?.lore?.let { getRarity(it) }?.color ?: ct.items else ct.items
                }

                tooltipHandler { listOf("${it.displayName} §7(${it.getMagicalPower.colorize(22)}§7)") + it.lore }
            }
        }

        private val buttons: ButtonDSL<Int> by profileLazy {
            buttons(
                Box(separatorX + lineY, startY, width, buttonHeight), lineY, ot, default = 1,
                (1..pages).toList(), 2, ct.button,
                ct.selected, ct.roundness, 1f,
            ) {
                onSelect {
                    itemGrid.updateItems(getSubset(talis, buttons.getSelected-1, maxRows*9))
                    playClickSound()
                }
            }
        }

        private val entryHeight: Double by profileLazy { (mainHeight - startY + lineY)/textList.size }

        override fun draw() {
            roundedRectangle(separatorX, startY, ot, mainHeight - startY + lineY, ct.line)

            buttons.draw()

            textList.forEachIndexed { i, text ->
                mcText(text, mainX, (startY + (entryHeight * i) + entryHeight/2) - ((getMCTextHeight() * 2)/2), 2, ct.font, shadow = true, center = false)
            }

            itemGrid.draw(mouseX.toInt(), mouseY.toInt())
        }

        override fun mouseClick(x: Int, y: Int, button: Int) {
            buttons.click(mouseX.toInt(), mouseY.toInt(), button)
        }
    }

    object Backpacks: InventoryPage() {
        private inline val inventory: List<ItemStack?> get() = profile.inventory.backpackContents["${buttons.getSelected-1}"]?.itemStacks ?: emptyList()
        private inline val gridItems get() = listOf(GridItems(inventory, (mainCenterX - (mainWidth * 0.8)/2).toInt(), centerY.toInt(), (mainWidth * 0.8).toInt(), 9))

        private val buttons: ButtonDSL<Int> by profileLazy {
            buttons(
                Box(mainX, startY, mainWidth, buttonHeight), lineY, ot, default = 1,
                profile.inventory.backpackContents.keys.mapNotNull { it.toIntOrNull()?.plus(1) }.sorted(), 2, // adding and subtracting so the display matches the game menu.
                ct.button, ct.selected, ct.roundness, 1f,
            ) {
                onSelect {
                    itemGrid.updateItems(inventory)
                    playClickSound()
                }
            }
        }

        private val itemGrid: ItemGridDSL by profileLazy {
            itemGrid(gridItems, ct.roundness, 1f, lineY.toFloat(),) {
                colorHandler { _, item -> if (rarityBackgrounds) item?.lore?.let { getRarity(it) }?.color ?: ct.items else ct.items }
            }
        }

        override fun draw() {
            buttons.draw()
            itemGrid.draw(mouseX.toInt(), mouseY.toInt())
        }

        override fun mouseClick(x: Int, y: Int, button: Int) {
            buttons.click(mouseX.toInt(), mouseY.toInt(), button)
        }
    }

    object EnderChest: InventoryPage() {
        private val items: List<ItemStack?> by profileLazy { profile.inventory.eChestContents.itemStacks }
        private val pages: Int by profileLazy { ceil(items.size/45.0).toInt() }
        private inline val gridItems get() = listOf(GridItems(getSubset(items, buttons.getSelected-1, 45), (mainCenterX - (mainWidth * 0.8)/2).toInt(), centerY.toInt(), (mainWidth * 0.8).toInt(), 9))

        private val buttons: ButtonDSL<Int> by profileLazy {
            buttons(
                Box(mainX, startY, mainWidth, buttonHeight), lineY, ot, default = 1,
                (1..pages).toList(), 2, ct.button,
                ct.selected, ct.roundness, 1f,
            ) {
                onSelect {
                    itemGrid.updateItems(getSubset(items, buttons.getSelected-1, 45))
                    playClickSound()
                }
            }
        }

        private val itemGrid: ItemGridDSL by profileLazy {
            itemGrid(gridItems, ct.roundness, 1f, lineY.toFloat()) {
                colorHandler { _, item -> if (rarityBackgrounds) item?.lore?.let { getRarity(it) }?.color ?: ct.items else ct.items }
            }
        }

        override fun draw() {
            buttons.draw()
            itemGrid.draw(mouseX.toInt(), mouseY.toInt())
        }

        override fun mouseClick(x: Int, y: Int, button: Int) {
            buttons.click(mouseX.toInt(), mouseY.toInt(), button)
        }
    }
}