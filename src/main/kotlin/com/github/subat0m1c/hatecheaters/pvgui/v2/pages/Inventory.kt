package com.github.subat0m1c.hatecheaters.pvgui.v2.pages

import com.github.subat0m1c.hatecheaters.modules.render.ProfileViewer.maxRows
import com.github.subat0m1c.hatecheaters.modules.render.ProfileViewer.rarityBackgrounds
import com.github.subat0m1c.hatecheaters.pvgui.v2.PVPage
import com.github.subat0m1c.hatecheaters.pvgui.v2.PageHandler.playClickSound
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.*
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.Utils.fixFirstNine
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.Utils.getSubset
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.Utils.insertItemsAtIndexes
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.capitalizeWords
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.colorize
import com.github.subat0m1c.hatecheaters.utils.ItemUtils.getMagicalPower
import com.github.subat0m1c.hatecheaters.utils.ItemUtils.maxMagicalPower
import com.github.subat0m1c.hatecheaters.utils.odinwrappers.*
import me.odinmain.utils.skyblock.getRarity
import me.odinmain.utils.skyblock.lore
import net.minecraft.item.ItemStack
import kotlin.math.ceil
import kotlin.math.floor

object Inventory : PVPage("Inventory") {
    enum class InventoryPages(val page: InventoryPage) {
        Basic(Inventory.Basic),
        Wardrobe(Inventory.Wardrobe),
        Talismans(Inventory.Talismans),
        Backpacks(Inventory.Backpacks),
        EnderChest(Inventory.EnderChest),
    }

    private inline val currentInventory
        get() = InventoryPages.entries.find { it.page.displayName == buttons.selected }?.page
            ?: InventoryPages.Basic.page

    val separatorLineY: Double by lazy { spacer + ((totalHeight - spacer * (2 + 6 - 1)) * 0.9) / 6 }

    private val inventoryAPI: Boolean by resettableLazy { profile.inventoryApi }

    private val buttons: ButtonDSL<String> by resettableLazy {
        val inventoryPageHeight = ((totalHeight - spacer * (2 + 6 - 1)) * 0.9) / 6

        buttons(
            Box(mainX, spacer, mainWidth, inventoryPageHeight), spacer, default = "Basic",
            InventoryPages.entries.map { it.page.displayName }, 2, ct.button.hc(),
            ct.selected.hc(), ct.roundness,
        ) { onSelect { playClickSound() } }
    }

    override fun draw(mouseX: Int, mouseY: Int) {
        if (!inventoryAPI) return Text.text(
            "Inventory API disabled!",
            mainCenterX,
            mainHeight / 2,
            4f,
            Colors.RED,
            alignment = Text.Alignment.MIDDLE
        )

        buttons.draw(mouseX, mouseY)
        currentInventory.draw(mouseX, mouseY)
    }

    override fun mouseClick(x: Int, y: Int, button: Int) {
        buttons.click(x, y, button)
        currentInventory.mouseClick(x, y, button)
        super.mouseClick(x, y, button)
    }

    val invArmor by resettableLazy { profile.inventory.invArmor.itemStacks }
    val startY by lazy { separatorLineY + spacer + 1 }
    private val buttonHeight = (mainWidth - (spacer * 16)) / 18
    private val centerY by lazy { (startY + buttonHeight + spacer) + (mainHeight - (startY + buttonHeight)) / 2 }

    abstract class InventoryPage(val displayName: String) : PVPage("InventoryPage")

    object Basic : InventoryPage("Basic") {
        private val itemGrid: ItemGridDSL by resettableLazy {
            val inventory = invArmor.reversed() + listOf(null) + profile.inventory.equipment.itemStacks + fixFirstNine(profile.inventory.invContents.itemStacks)

            itemGrid(
                listOf(
                    GridItems(
                        inventory,
                        mainX,
                        (startY + (mainHeight - ((separatorLineY))) / 2).toInt(),
                        mainWidth,
                        9
                    )
                ), ct.roundness, spacer.toFloat()
            ) {
                colorHandler { i, item ->
                    when {
                        i == 4 -> Colors.TRANSPARENT // this should be replaced by separate inventory grids at some point
                        rarityBackgrounds -> item?.lore?.let { getRarity(it) }?.color?.hc() ?: ct.items.hc()
                        else -> ct.items.hc()
                    }
                }
            }
        }

        override fun draw(mouseX: Int, mouseY: Int) = itemGrid.draw(mouseX, mouseY)
    }

    object Wardrobe : InventoryPage("Wardrobe") {
        private val wardrobe: List<ItemStack?> by resettableLazy { profile.inventory.wardrobeContents.itemStacks }

        private inline val selectedButtonIndex: Int get() = buttons.selected - 1
        private inline val gridItems get() = listOf(GridItems(getSubset(inventoryWithArmor, selectedButtonIndex), mainX, centerY.toInt(), mainWidth, 9))

        private val equipped by resettableLazy {
            val equippedWardrobe = profile.inventory.wardrobeEquipped?.let { it - 1 } ?: -1
            val rangeStart = selectedButtonIndex * 9
            val rangeEnd = rangeStart + 9

            if (equippedWardrobe in rangeStart until rangeEnd) equippedWardrobe - rangeStart else -1
        }

        private val inventoryWithArmor by resettableLazy {
            if (equipped != -1 && invArmor.isNotEmpty()) { // this is awful i think maybe
                invArmor.mapIndexed { index, itemStack ->
                    (equipped + (9 * (invArmor.size - 1 - index))) to itemStack
                }.let { insertItemsAtIndexes(wardrobe, it) }
            } else wardrobe
        }

        private val itemGrid by resettableLazy {
            itemGrid(gridItems, ct.roundness, spacer.toFloat()) {
                colorHandler { _, item -> if (item in invArmor.toSet()) Colors.BLUE else ct.items.hc() }
            }
        }

        private val buttons by resettableLazy {
            buttons(
                Box(mainX, startY, mainWidth, buttonHeight), spacer, default = 1,
                (1..ceil(wardrobe.size.toDouble() / 36).toInt()).toList(), 2,
                ct.button.hc(), ct.selected.hc(), ct.roundness
            ) {
                onSelect {
                    itemGrid.updateItems(getSubset(inventoryWithArmor, selectedButtonIndex))
                    playClickSound()
                }
            }
        }

        override fun draw(mouseX: Int, mouseY: Int) {
            buttons.draw(mouseX, mouseY)
            itemGrid.draw(mouseX, mouseY)
        }

        override fun mouseClick(x: Int, y: Int, button: Int) = buttons.click(x, y, button)
    }

    object Talismans : InventoryPage("Talismans") {
        private val talis by resettableLazy {
            profile.inventory.bagContents["talisman_bag"]?.itemStacks?.filterNotNull()
                ?.sortedByDescending { it.getMagicalPower } ?: emptyList()
        }
        private val magicPower by resettableLazy { profile.magicalPower }
        private val mp by resettableLazy { "§aSelected Power: §6${profile.accessoryBagStorage.selectedPower?.capitalizeWords() ?: "§cNone!"}" }
        private val tunings by resettableLazy { profile.tunings }
        private val abiPhone by resettableLazy { "§5Abicase: ${floor(profile.crimsonIsle.abiphone.activeContacts.size / 2.0).toInt()}" }
        private val riftPrism by resettableLazy { "§dRift Prism: ${if (profile.rift.access.consumedPrism) "§aObtained" else "§cMissing"}" }

        private val textList by resettableLazy { listOf(mp, abiPhone, riftPrism) + tunings }
        private val pages by resettableLazy { ceil(talis.size.toDouble() / (maxRows * 9)).toInt() }

        private val textBoxWidth = mainWidth * 0.38 // magic number 0.38 my beloved.

        private val textBox by resettableLazy {
            TextBox(
                Box(mainX + spacer, startY + spacer, textBoxWidth - 2 * spacer, mainHeight - startY - spacer),
                "Magical Power: ${magicPower.colorize(maxMagicalPower)}",
                2.5f,
                textList,
                2.2f,
                spacer.toFloat(),
                ct.font.hc()
            )
        }

        private val separatorX = floor(mainX + textBoxWidth).toInt()
        private val width = mainWidth - ((separatorX - mainX)) - spacer

        private inline val gridItems
            get() = listOf(
                GridItems(
                    getSubset(talis, buttons.selected - 1, maxRows * 9),
                    separatorX + spacer + 1,
                    centerY.toInt(),
                    width,
                    9
                )
            )

        private val itemGrid by resettableLazy {
            itemGrid(gridItems, ct.roundness, spacer.toFloat()) {
                colorHandler { _, item ->
                    if (rarityBackgrounds) item?.lore?.let { getRarity(it) }?.color?.hc() ?: ct.items.hc()
                    else ct.items.hc()
                }

                tooltipHandler { listOf("${it.displayName} §7(${it.getMagicalPower.colorize(22)}§7)") + it.lore }
            }
        }

        private val buttons: ButtonDSL<Int> by resettableLazy {
            buttons(
                Box(separatorX + spacer, startY, width, buttonHeight), spacer, default = 1,
                (1..pages).toList(), 2, ct.button.hc(), ct.selected.hc(), ct.roundness,
            ) {
                onSelect {
                    itemGrid.updateItems(getSubset(talis, buttons.selected - 1, maxRows * 9))
                    playClickSound()
                }
            }
        }

        override fun draw(mouseX: Int, mouseY: Int) {
            Shaders.rect(mainX, startY, textBoxWidth, mainHeight - startY + spacer, ct.roundness, ct.items.hc())

            buttons.draw(mouseX, mouseY)
            textBox.draw()
            itemGrid.draw(mouseX, mouseY)
        }

        override fun mouseClick(x: Int, y: Int, button: Int) {
            buttons.click(x, y, button)
        }
    }

    object Backpacks : InventoryPage("Backpacks") {
        private inline val inventory
            get() = profile.inventory.backpackContents["${buttons.selected - 1}"]?.itemStacks ?: emptyList()
        private inline val gridItems get() = listOf(GridItems(inventory, (mainCenterX - (mainWidth * 0.8) / 2).toInt(), centerY.toInt(), (mainWidth * 0.8).toInt(), 9))

        private val buttons: ButtonDSL<Int> by resettableLazy {
            buttons(
                Box(mainX, startY, mainWidth, buttonHeight), spacer, default = 1,
                profile.inventory.backpackContents.keys.mapNotNull { it.toIntOrNull()?.plus(1) }.sorted(), 2, // adding and subtracting so the display matches the game menu instead of index.
                ct.button.hc(), ct.selected.hc(), ct.roundness,
            ) {
                onSelect {
                    itemGrid.updateItems(inventory)
                    playClickSound()
                }
            }
        }

        private val itemGrid by resettableLazy {
            itemGrid(gridItems, ct.roundness, spacer.toFloat()) {
                colorHandler { _, item ->
                    if (rarityBackgrounds) item?.lore?.let { getRarity(it) }?.color?.hc()
                        ?: ct.items.hc() else ct.items.hc()
                }
            }
        }

        override fun draw(mouseX: Int, mouseY: Int) {
            buttons.draw(mouseX, mouseY)
            itemGrid.draw(mouseX, mouseY)
        }

        override fun mouseClick(x: Int, y: Int, button: Int) {
            buttons.click(x, y, button)
        }
    }

    object EnderChest : InventoryPage("Ender Chest") {
        private val items by resettableLazy { profile.inventory.eChestContents.itemStacks }
        private val pages by resettableLazy { ceil(items.size / 45.0).toInt() }
        private inline val gridItems
            get() = listOf(
                GridItems(
                    getSubset(items, buttons.selected - 1, 45),
                    (mainCenterX - (mainWidth * 0.8) / 2).toInt(),
                    centerY.toInt(),
                    (mainWidth * 0.8).toInt(),
                    9
                )
            )

        private val buttons: ButtonDSL<Int> by resettableLazy {
            buttons(
                Box(mainX, startY, mainWidth, buttonHeight), spacer, default = 1,
                (1..pages).toList(), 2, ct.button.hc(),
                ct.selected.hc(), ct.roundness,
            ) {
                onSelect {
                    itemGrid.updateItems(getSubset(items, buttons.selected - 1, 45))
                    playClickSound()
                }
            }
        }

        private val itemGrid: ItemGridDSL by resettableLazy {
            itemGrid(gridItems, ct.roundness, spacer.toFloat()) {
                colorHandler { _, item ->
                    if (rarityBackgrounds) item?.lore?.let { getRarity(it) }?.color?.hc()
                        ?: ct.items.hc() else ct.items.hc()
                }
            }
        }

        override fun draw(mouseX: Int, mouseY: Int) {
            buttons.draw(mouseX, mouseY)
            itemGrid.draw(mouseX, mouseY)
        }

        override fun mouseClick(x: Int, y: Int, button: Int) {
            buttons.click(x, y, button)
        }
    }
}