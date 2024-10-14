package com.github.subat0m1c.hatecheaters.pvgui.v2.pages

import com.github.subat0m1c.hatecheaters.pvgui.v2.Pages
import com.github.subat0m1c.hatecheaters.pvgui.v2.Pages.centeredText
import com.github.subat0m1c.hatecheaters.pvgui.v2.Pages.playClickSound
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.ButtonDSL
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.Utils.getSubset
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.buttons
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.profileLazy
import com.github.subat0m1c.hatecheaters.utils.ApiUtils.colorName
import com.github.subat0m1c.hatecheaters.utils.ApiUtils.petItem
import me.odinmain.utils.render.Box
import me.odinmain.utils.render.roundedRectangle
import kotlin.math.ceil
import kotlin.math.floor

object Pets: Pages.PVPage("Pets") {
    private val mainLineY = mainHeight*0.1
    private val activePet: String by profileLazy { "§6Active Pet§7: §${ct.fontCode}${profile.pets.pets.find { it.active }?.colorName ?: "None!"} ${profile.pets.pets.find { it.active }?.petItem?.let { "§7(§${ct.fontCode}${it}§7)" } ?: ""}" }
    private val buttonHeight = (mainWidth - (lineY * 16))/18

    private val buttons: ButtonDSL<Int> by profileLazy {
        buttons(
            Box(mainX, mainLineY+lineY, mainWidth, buttonHeight), lineY, ot, 1,
            (1..<ceil(profile.pets.pets.size/20.0).toInt()).toList(), 3f,
            ct.button, ct.selected, ct.roundness, 1f, false
        ) { onSelect { playClickSound() } }
    }

    private val pets: List<String> by profileLazy {
        profile.pets.pets.map { "${it.colorName} ${it.petItem?.let { "§7(§${ct.fontCode}${it}§7)" }}" }
    }

    private val entryHeight: Double by profileLazy { (mainHeight - (mainLineY + buttonHeight + lineY*2))/(pets.size/2) }

    override fun draw() {
        roundedRectangle(mainX, mainLineY, mainWidth, ot, ct.line)
        centeredText(activePet, mainCenterX, lineY + mainLineY/2, 3, ct.font)

        buttons.draw()

        val currentPets = getSubset(pets, buttons.getSelected-1, 20)

        currentPets.forEachIndexed { i, pet ->
            val x = when (i % 2) {
                0 -> mainWidth * 1/3
                1 -> mainWidth * 2/3
                else -> mainCenterX
            }
            centeredText(pet, mainX + x, ((mainLineY + buttonHeight + lineY*2) + (entryHeight * floor(i.toDouble()/2)) + entryHeight/2), 2.5)
        }
    }

    override fun mouseClick(x: Int, y: Int, button: Int) = buttons.click(mouseX.toInt(), mouseY.toInt(), button)
}