package com.github.subat0m1c.hatecheaters.pvgui.v2.pages

import com.github.subat0m1c.hatecheaters.pvgui.v2.Pages
import com.github.subat0m1c.hatecheaters.pvgui.v2.Pages.centeredText
import com.github.subat0m1c.hatecheaters.pvgui.v2.Pages.playClickSound
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.Utils.getSubset
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.buttons
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.profileLazy
import com.github.subat0m1c.hatecheaters.utils.ItemUtils.colorName
import com.github.subat0m1c.hatecheaters.utils.ItemUtils.petItem
import com.github.subat0m1c.hatecheaters.utils.odinwrappers.Box
import com.github.subat0m1c.hatecheaters.utils.odinwrappers.Shaders
import com.github.subat0m1c.hatecheaters.utils.odinwrappers.hc
import kotlin.math.ceil
import kotlin.math.floor

object Pets: Pages.PVPage("Pets") {
    private val activePet by profileLazy {
        "§6Active Pet§7: §${ct.fontCode}${profile.pets.activePet?.let { "${it.colorName}${it.petItem?.let { " §7(§${ct.fontCode}${it}§7)" } ?: ""}" } ?: "None!"}"
    }
    private val buttonHeight = (mainWidth - (lineY * 16)) / 18
    private val mainLineY = mainHeight * 0.1

    private val buttons by profileLazy {
        buttons(
            Box(mainX, mainLineY+lineY, mainWidth, buttonHeight), lineY, ot, 1,
            (1..<ceil(profile.pets.pets.size / 20.0).toInt()).toList(), 3f,
            ct.button.hc(), ct.selected.hc(), ct.roundness, 1f, false
        ) { onSelect { playClickSound() } }
    }

    private val pets by profileLazy {
        profile.pets.pets.map { pet -> "${pet.colorName}${pet.petItem?.let { " §7(§${ct.fontCode}${it}§7)" } ?: ""}" }
    }

    private val entryHeight by profileLazy {
        (mainHeight - (mainLineY + buttonHeight + lineY * 2)) / ceil(pets.size.coerceIn(1, 20) / 2.0)
    }

    override fun draw() {
        Shaders.rect(mainX, mainLineY, mainWidth, ot, color = ct.line.hc())
        centeredText(activePet, mainCenterX, lineY + mainLineY / 2, 3f, ct.font.hc())

        buttons.draw()

        val currentPets = getSubset(pets, buttons.getSelected - 1, 20)

        currentPets.forEachIndexed { i, pet ->
            val x = when (i % 2) {
                0 -> mainWidth * 0.28
                1 -> mainWidth * 0.72
                else -> mainCenterX.toDouble()
            }
            val y = (mainLineY + buttonHeight + lineY * 2) + (entryHeight * floor(i / 2.0)) + entryHeight / 2
            centeredText(pet, mainX + x, y, 2f)
        }
    }

    override fun mouseClick(x: Int, y: Int, button: Int) = buttons.click(mouseX.toInt(), mouseY.toInt(), button)
}