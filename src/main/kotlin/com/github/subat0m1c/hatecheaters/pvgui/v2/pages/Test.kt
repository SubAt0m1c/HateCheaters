package com.github.subat0m1c.hatecheaters.pvgui.v2.pages

import com.github.subat0m1c.hatecheaters.modules.ProfileViewer
import com.github.subat0m1c.hatecheaters.pvgui.v2.Pages
import com.github.subat0m1c.hatecheaters.pvgui.v2.pages.Inventory.invArmor
import com.github.subat0m1c.hatecheaters.pvgui.v2.pages.Inventory.separatorLineY
import com.github.subat0m1c.hatecheaters.pvgui.v2.pages.Inventory.startY
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.GridItems
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.Utils.fixFirstNine
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.itemGrid
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.profileLazy
import com.github.subat0m1c.hatecheaters.utils.apiutils.ApiUtils.itemStacks
import me.odinmain.utils.render.Color

object Test : Pages.PVPage("Test") {
    private val centerY by lazy { startY + (mainHeight - ((separatorLineY)))/2 }
    private val inventory by profileLazy {
        GridItems(
            invArmor.reversed() + listOf(null) + profile.inventory.equipment.itemStacks + fixFirstNine(profile.inventory.invContents.itemStacks),
            mainX, centerY.toInt(), mainWidth, 9
        )
    }
    private val test by profileLazy {
        itemGrid(
            listOf(inventory),
            Color.WHITE,
            ProfileViewer.currentTheme.roundness,
            1f,
            lineY.toFloat(),
        ) {
            colorHandler { i, _ ->
                if (i == 4) Color.TRANSPARENT else ProfileViewer.currentTheme.items
            }
        }
    }

    override fun draw() {
        test.draw(mouseX.toInt(), mouseY.toInt())
    }
}