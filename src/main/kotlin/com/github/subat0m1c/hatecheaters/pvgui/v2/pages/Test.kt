package com.github.subat0m1c.hatecheaters.pvgui.v2.pages

import com.github.subat0m1c.hatecheaters.modules.ProfileViewer
import com.github.subat0m1c.hatecheaters.pvgui.v2.Pages
import com.github.subat0m1c.hatecheaters.pvgui.v2.pages.Inventory.separatorLineY
import com.github.subat0m1c.hatecheaters.pvgui.v2.pages.Inventory.startY
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.GridItems
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.itemGrid
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.profileLazy
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.capitalizeWords
import com.github.subat0m1c.hatecheaters.utils.ItemUtils.createCustomSkull
import me.odinmain.utils.render.Color

object Test : Pages.PVPage("Test") {
    private val centerY by lazy { startY + (mainHeight - ((separatorLineY)))/2 }
    private val inventory by profileLazy {
        GridItems(
            listOf(createCustomSkull("§aSelected Power: §6${profile.accessoryBagStorage.selectedPower?.capitalizeWords() ?: "§cNone!"}", profile.tunings, TALISMAN_BAG_TEXTURE, profile.magicalPower)),
            mainX, centerY.toInt(), mainWidth, 1
        )
    }
    private val test by profileLazy {
        itemGrid(
            listOf(inventory),
            ProfileViewer.currentTheme.roundness,
            1f,
            lineY.toFloat(),
        ) {
            colorHandler { i, _ ->
                Color.TRANSPARENT
            }
        }
    }

    const val TALISMAN_BAG_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGIyNDllODhhZmEzMGZjODM3YjgyMTczYTMwNDgzNDU4ZDRlOWEzM2M3ZWMyNWU1NTEzODdlOGU1NGEwMThhZSJ9fX0="

    override fun draw() = test.draw(mouseX.toInt(), mouseY.toInt())
}