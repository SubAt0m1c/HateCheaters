package com.github.subat0m1c.hatecheaters.pvgui.v2.pages

import com.github.subat0m1c.hatecheaters.pvgui.v2.PVPage
import com.github.subat0m1c.hatecheaters.pvgui.v2.PageHandler.playClickSound
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.Utils.getSubset
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.buttons
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.resettableLazy
import com.github.subat0m1c.hatecheaters.utils.ItemUtils.colorName
import com.github.subat0m1c.hatecheaters.utils.ItemUtils.petItem
import com.github.subat0m1c.hatecheaters.utils.odinwrappers.*
import kotlin.math.ceil
import kotlin.math.floor

object Pets : PVPage("Pets") {
    private val activePet by resettableLazy {
        "§7Active Pet§8: §${ct.fontCode}${profile.pets.activePet?.let { "${it.colorName}${it.petItem?.let { " §7(§${ct.fontCode}${it}§7)" } ?: ""}" } ?: "None!"}"
    }
    private val buttonHeight = (mainWidth - (spacer * 16)) / 18

    private val activePetBox = Box(mainX, spacer, mainWidth, mainHeight * 0.1)

    private val buttons by resettableLazy {
        buttons(
            Box(mainX, activePetBox.y + activePetBox.h + spacer, mainWidth, buttonHeight), spacer, 1,
            List(ceil(profile.pets.pets.size / 20.0).toInt()) { it + 1 }, 3f,
            ct.button.hc(), ct.selected.hc(), ct.roundness, false
        ) { onSelect { playClickSound() } }
    }

    private val pets by resettableLazy {
        profile.pets.pets.sortedByDescending { it.exp }
            .map { pet -> "${pet.colorName}${pet.petItem?.let { " §7(§${ct.fontCode}${it}§7)" } ?: ""}" }
    }

    private val entryHeight by resettableLazy {
        (mainHeight - (activePetBox.y + activePetBox.h + buttonHeight + spacer * 2)) / ceil(
            pets.size.coerceIn(
                1,
                20
            ) / 2.0
        )
    }

    private val petsStart = activePetBox.y + activePetBox.h + buttonHeight + 2 * spacer

    override fun draw(mouseX: Int, mouseY: Int) {
        Shaders.rect(activePetBox, ct.roundness, ct.items.hc())
        Text.fillText(
            activePet,
            mainCenterX,
            activePetBox.y + activePetBox.h / 2,
            activePetBox.w - 2 * spacer,
            activePetBox.h - 2 * spacer,
            ct.font.hc()
        )

        buttons.draw(mouseX, mouseY)

        val currentPets = getSubset(pets, buttons.selected - 1, 20)

        Shaders.rect(Box(mainX, petsStart, mainWidth, mainHeight + spacer - petsStart), ct.roundness, ct.items.hc())

        currentPets.forEachIndexed { i, pet ->
            val x = when (i % 2) {
                0 -> mainWidth * 0.28
                1 -> mainWidth * 0.72
                else -> mainCenterX.toDouble()
            }
            val y = petsStart + (entryHeight * floor(i / 2.0)) + entryHeight / 2
            Text.text(pet, mainX + x, y, 2f, Colors.WHITE, alignment = Text.Alignment.MIDDLE)
        }
    }

    override fun mouseClick(x: Int, y: Int, button: Int) = buttons.click(x, y, button)
}