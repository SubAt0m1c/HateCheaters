package com.github.subat0m1c.hatecheaters.pvgui.v2

import com.github.subat0m1c.hatecheaters.modules.ProfileViewer
import com.github.subat0m1c.hatecheaters.modules.ProfileViewer.scale
import com.github.subat0m1c.hatecheaters.pvgui.v2.PVGui.loadText
import com.github.subat0m1c.hatecheaters.pvgui.v2.PVGui.playerData
import com.github.subat0m1c.hatecheaters.pvgui.v2.PVGui.profileName
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.Utils.getMouseX
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.Utils.getMouseY
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.Utils.isObjectHovered
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.mcWidth
import com.github.subat0m1c.hatecheaters.utils.apiutils.ApiUtils.profileOrSelected
import com.github.subat0m1c.hatecheaters.utils.apiutils.HypixelData.MemberData
import com.github.subat0m1c.hatecheaters.utils.apiutils.HypixelData.PlayerInfo
import com.github.subat0m1c.hatecheaters.utils.apiutils.SkyCryptData.dummyPlayer
import me.odinmain.OdinMain.mc
import me.odinmain.utils.minus
import me.odinmain.utils.render.*
import me.odinmain.utils.skyblock.PlayerUtils.playLoudSound
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager

object Pages {

    var currentPage = PageEntries.Overview

    enum class PageEntries(val page: PVPage) {
        Overview(page = com.github.subat0m1c.hatecheaters.pvgui.v2.pages.Overview),
        Profile(page = com.github.subat0m1c.hatecheaters.pvgui.v2.pages.Profile),
        Dungeons(page = com.github.subat0m1c.hatecheaters.pvgui.v2.pages.Dungeons),
        Inventory(page = com.github.subat0m1c.hatecheaters.pvgui.v2.pages.Inventory),
        Pets(page = com.github.subat0m1c.hatecheaters.pvgui.v2.pages.Pets),
        //Test(page = com.github.subat0m1c.hatecheaters.pvgui.v2.pages.Test)
    }


    abstract class PVPage(val name: String) {
        inline val player: PlayerInfo get() = playerData ?: dummyPlayer // these should never be rendered when null, however making them not nullable without asserting is good.
        inline val profile: MemberData get() = player.profileOrSelected(profileName)?.members?.get(player.uuid) ?: MemberData(playerId = "")
        inline val ct get() = ProfileViewer.currentTheme

        val totalWidth = 1000
        val totalHeight = 560
        val ot = 1
        val lineX = 216

        /** Used as a spacer as well */
        val lineY = 10

        private val sx = -totalWidth/2
        private val sy = -totalHeight/2

        val mainWidth = (totalWidth - (lineX + lineY*2))
        val mainHeight = totalHeight-lineY*2

        val pageHeight: Double by lazy { (mainHeight*0.9 - (lineY * (2 + PageEntries.entries.size-1)))/PageEntries.entries.size }

        private val lastY: Double by lazy { lineY + ((pageHeight + lineY) * PageEntries.entries.size) }
        private val lastHeight: Double by lazy { totalHeight - lastY - lineY }

        private val pageWidth = lineX - lineY*2
        private val pageCenter = lineY+pageWidth/2
        val mainX = (lineX+ot) + (lineY)

        val mainCenterX = lineX + ot + lineY + mainWidth/2

        // maybe overcomplicated but it works :)
        val mouseX get() = ((getMouseX * ScaledResolution(mc).scaleFactor - mc.displayWidth/2) * 1f / scale) - sx
        val mouseY get() = ((getMouseY * ScaledResolution(mc).scaleFactor - mc.displayHeight/2) * 1f / scale) - sy

        // todo make vertical button dsl for this
        fun preDraw() {
            GlStateManager.pushMatrix()
            translate(sx, sy, 0)
            roundedRectangle(0, 0, totalWidth, totalHeight, ct.main, ct.roundness, 1f)

            roundedRectangle(lineX, lineY, ot, totalHeight-lineY*2, ct.line)

            PageEntries.entries.forEachIndexed { i, page ->
                val pageY = lineY + ((pageHeight + lineY) * i)
                if (currentPage == page) roundedRectangle(lineY, pageY, pageWidth, pageHeight, ct.selected, radius = ct.roundness, edgeSoftness = 1f)
                else roundedRectangle(lineY, pageY, pageWidth, pageHeight, ct.button, radius = ct.roundness, edgeSoftness = 1f)
                val centerY = lineY + (pageHeight/2 + ((pageHeight + lineY) * i))
                centeredText(page.name, pageCenter, centerY, color = ct.font, scale = 3.5)
            }

            roundedRectangle(lineY, lastY, pageWidth, lastHeight, ct.button, radius = ct.roundness, edgeSoftness = 1f)
            val betaText = if (currentPage != PageEntries.Overview) player.name else "HCPV Beta 2"
            centeredText(betaText, pageCenter, lastY + lastHeight/2, color = ct.font, scale = if (betaText.length >= 8) 3 else 3.5)
            if (playerData != null) draw() else centeredText(loadText, mainCenterX, totalHeight/2, 7f)
            GlStateManager.popMatrix()
        }

        fun handleClick(x: Int, y: Int, button: Int) {
            PageEntries.entries.filterIndexed { i, _ ->
                val pageY = lineY + ((pageHeight + lineY) * i)
                isObjectHovered(Box(lineY, pageY, pageWidth, pageHeight), mouseX, mouseY)
            }.firstOrNull()?.let {
                currentPage = it
                playClickSound()
            }

            mouseClick(x, y, button)
        }

        open fun draw() = centeredText("This page is not yet implemented!", mainCenterX, totalHeight/2, 3f)

        open fun mouseClick(x: Int, y: Int, button: Int) { }

        open fun init() { }
    }

    /**
     * The "Centered" parameter in [mcText()] does not center vertically, thus this should be used instead.
     */
    fun centeredText(text: String, x: Number, y: Number, scale: Number = 1f, color: Color = Color.WHITE, shadow: Boolean = true) =
        mcText(text, x - (text.mcWidth.toDouble()*scale.toDouble()/2.0), y - (getMCTextHeight().toDouble()*scale.toDouble())/2.0, scale, color, shadow, false)

    fun playClickSound() = playLoudSound("gui.button.press", 0.5f, 1.1f)
}