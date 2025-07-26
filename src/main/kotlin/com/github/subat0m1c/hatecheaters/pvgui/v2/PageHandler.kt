package com.github.subat0m1c.hatecheaters.pvgui.v2

import com.github.subat0m1c.hatecheaters.modules.render.ProfileViewer.scale
import com.github.subat0m1c.hatecheaters.pvgui.v2.PVGui.loadText
import com.github.subat0m1c.hatecheaters.pvgui.v2.PVGui.playerData
import com.github.subat0m1c.hatecheaters.pvgui.v2.PVGui.profileName
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.ButtonDSL
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.ResettableLazy
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.Utils.getMouseX
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.Utils.getMouseY
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.buttons
import com.github.subat0m1c.hatecheaters.utils.apiutils.HypixelData.MemberData
import com.github.subat0m1c.hatecheaters.utils.apiutils.HypixelData.PlayerInfo
import com.github.subat0m1c.hatecheaters.utils.apiutils.HypixelData.PlayerInfo.Companion.dummyPlayer
import com.github.subat0m1c.hatecheaters.utils.odinwrappers.Box
import com.github.subat0m1c.hatecheaters.utils.odinwrappers.Shaders
import com.github.subat0m1c.hatecheaters.utils.odinwrappers.Text
import com.github.subat0m1c.hatecheaters.utils.odinwrappers.hc
import me.odinmain.OdinMain.mc
import me.odinmain.utils.skyblock.PlayerUtils.playLoudSound
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager

object PageHandler {
    private var currentPage = PageEntries.Overview

    inline val player: PlayerInfo get() = playerData ?: dummyPlayer
    inline val profile: MemberData
        get() = player.profileOrSelected(profileName)?.members?.get(player.uuid) ?: MemberData(playerId = "")

    val pageData = PageData()

    private val pageButtonHeight by lazy {
        (0.9 * pageData.mainHeight - (pageData.spacer * (PageEntries.entries.size + 1))) / PageEntries.entries.size
    }
    private val infoY by lazy {
        pageData.spacer + ((pageButtonHeight + pageData.spacer) * PageEntries.entries.size)
    }
    private val infoHeight by lazy {
        pageData.totalHeight - infoY - pageData.spacer
    }

    private val pageButtonWidth = pageData.mainStart - 2 * pageData.spacer
    private val pageButtonCenter = pageData.mainStart / 2

    private inline val mouseX get() = (((getMouseX * ScaledResolution(mc).scaleFactor - mc.displayWidth / 2) * 1f / scale) - pageData.offsetX).toInt()
    private inline val mouseY get() = (((getMouseY * ScaledResolution(mc).scaleFactor - mc.displayHeight / 2) * 1f / scale) - pageData.offsetY).toInt()

    private val pageButtonsLazy = ResettableLazy.silentCreate {
        val height = pageData.mainHeight - infoHeight - pageData.spacer

        buttons(
            Box(pageData.spacer, pageData.spacer, pageButtonWidth, height),
            pageData.spacer,
            default = PageEntries.Overview,
            PageEntries.entries,
            3.5,
            pageData.ct.button.hc(),
            pageData.ct.selected.hc(),
            pageData.ct.roundness,
            vertical = true
        ) {
            onSelect {
                currentPage = it
                playClickSound()
            }
        }
    }

    private val pageButtons: ButtonDSL<PageEntries> by pageButtonsLazy

    fun preDraw() {
        Shaders.pushMatrix()
        Shaders.translate(pageData.offsetX.toFloat(), pageData.offsetY.toFloat())
        GlStateManager.translate(pageData.offsetX.toDouble(), pageData.offsetY.toDouble(), 0.0)
        Shaders.rect(0, 0, pageData.totalWidth, pageData.totalHeight, pageData.ct.roundness, pageData.ct.main.hc())

        //Shaders.rect(pageData.mainStart, pageData.spacer, 1, pageData.totalHeight - pageData.spacer * 2, 0f, pageData.ct.line.hc())

        pageButtons.draw(mouseX, mouseY)

        Shaders.rect(
            pageData.spacer,
            infoY,
            pageButtonWidth,
            infoHeight,
            pageData.ct.roundness,
            pageData.ct.button.hc()
        )
        val betaText = if (currentPage != PageEntries.Overview) player.name else "HCPV 1.0.0"
        Text.fillText(
            betaText,
            pageButtonCenter,
            infoY + infoHeight / 2,
            pageButtonWidth - pageData.spacer * 2,
            infoHeight - 2 * pageData.spacer,
            pageData.ct.font.hc()
        )
        if (playerData != null) currentPage.page.draw(mouseX, mouseY)
        else Text.fillText(
            loadText,
            pageData.mainStart + pageData.mainWidth / 2,
            pageData.totalHeight / 2,
            pageData.mainWidth - pageData.spacer * 2,
            pageData.mainHeight * 0.2,
            pageData.ct.font.hc()
        )
        Shaders.popMatrix()
    }

    fun handleClick(button: Int) {
        pageButtons.click(mouseX, mouseY, button)
        currentPage.page.mouseClick(mouseX, mouseY, button)
    }

    fun playClickSound() = playLoudSound("gui.button.press", 0.5f, 1.1f)

    fun reset() {
        pageButtonsLazy.reset()
        currentPage = PageEntries.Overview
    }
}