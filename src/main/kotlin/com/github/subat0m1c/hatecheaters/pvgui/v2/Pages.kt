package com.github.subat0m1c.hatecheaters.pvgui.v2

import com.github.subat0m1c.hatecheaters.modules.render.ProfileViewer
import com.github.subat0m1c.hatecheaters.modules.render.ProfileViewer.scale
import com.github.subat0m1c.hatecheaters.pvgui.v2.PVGui.loadText
import com.github.subat0m1c.hatecheaters.pvgui.v2.PVGui.playerData
import com.github.subat0m1c.hatecheaters.pvgui.v2.PVGui.profileName
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.Utils.getMouseX
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.Utils.getMouseY
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.Utils.isObjectHovered
import com.github.subat0m1c.hatecheaters.utils.apiutils.HypixelData.MemberData
import com.github.subat0m1c.hatecheaters.utils.apiutils.HypixelData.PlayerInfo
import com.github.subat0m1c.hatecheaters.utils.apiutils.HypixelData.PlayerInfo.Companion.dummyPlayer
import com.github.subat0m1c.hatecheaters.utils.odinwrappers.*
import me.odinmain.OdinMain.mc
import me.odinmain.utils.minus
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

    // TODO: Make this look good
    abstract class PVPage(val name: String) {
        protected inline val player: PlayerInfo
            get() = playerData
                ?: dummyPlayer // these should never be rendered when null, however making them not nullable without asserting is good.
        protected inline val profile: MemberData
            get() = player.profileOrSelected(profileName)?.members?.get(player.uuid) ?: MemberData(playerId = "")
        protected inline val ct get() = ProfileViewer.currentTheme

        private val totalWidth = 1000
        private val lineX = 216
        protected val totalHeight = 560
        protected val ot = 1

        /** Used as a spacer as well */
        protected val lineY = 10

        protected val sx = -totalWidth / 2
        protected val sy = -totalHeight / 2

        protected val mainWidth = (totalWidth - (lineX + lineY * 2))
        protected val mainHeight = totalHeight - lineY * 2

        protected val pageHeight by lazy { (mainHeight * 0.9 - (lineY * (2 + PageEntries.entries.size - 1))) / PageEntries.entries.size }

        private val lastY by lazy { lineY + ((pageHeight + lineY) * PageEntries.entries.size) }
        private val lastHeight by lazy { totalHeight - lastY - lineY }

        private val pageWidth = lineX - lineY * 2
        private val pageCenter = lineY+pageWidth / 2
        protected val mainX = (lineX + ot) + (lineY)

        protected val mainCenterX = lineX + ot + lineY + mainWidth / 2

        // maybe overcomplicated but it works :)
        protected inline val mouseX get() = ((getMouseX * ScaledResolution(mc).scaleFactor - mc.displayWidth / 2) * 1f / scale) - sx
        protected inline val mouseY get() = ((getMouseY * ScaledResolution(mc).scaleFactor - mc.displayHeight / 2) * 1f / scale) - sy

//        protected inline val mouseX: Double get() = getMouseX
//        protected inline val mouseY: Double get() = getMouseY

        // todo make vertical button dsl for this
        fun preDraw() {
            Shaders.pushMatrix()
            Shaders.translate(sx.toFloat(), sy.toFloat())
            GlStateManager.translate(sx.toDouble(), sy.toDouble(), 0.0)
            Shaders.rect(0, 0, totalWidth, totalHeight, ct.roundness, ct.main.hc())

            Shaders.rect(lineX, lineY, ot, totalHeight - lineY * 2, 0f, ct.line.hc())

            PageEntries.entries.forEachIndexed { i, page ->
                val pageY = lineY + ((pageHeight + lineY) * i)
                if (currentPage == page) Shaders.rect(
                    lineY,
                    pageY,
                    pageWidth,
                    pageHeight,
                    ct.roundness,
                    ct.selected.hc()
                )
                else Shaders.rect(lineY, pageY, pageWidth, pageHeight, ct.roundness, ct.button.hc())
                val centerY = lineY + (pageHeight / 2 + ((pageHeight + lineY) * i))
                centeredText(page.name, pageCenter, centerY, color = ct.font.hc(), scale = 3.5f)
            }

            Shaders.rect(lineY, lastY, pageWidth, lastHeight, ct.roundness, ct.button.hc())
            val betaText = if (currentPage != PageEntries.Overview) player.name else "HCPV 0.0.2"
            centeredText(
                betaText,
                pageCenter,
                lastY + lastHeight / 2,
                color = ct.font.hc(),
                scale = if (betaText.length >= 8) 3f else 3.5f
            )
            if (playerData != null) draw() else centeredText(loadText, mainCenterX, totalHeight / 2, 5f)
            Shaders.popMatrix()
        }

        fun handleClick(x: Int, y: Int, button: Int) {
            PageEntries.entries.filterIndexed { i, _ ->
                isObjectHovered(Box(lineY, lineY + ((pageHeight + lineY) * i), pageWidth, pageHeight), mouseX, mouseY)
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
    fun centeredText(text: String, x: Number, y: Number, scale: Float = 1f, color: Color = Colors.WHITE) =
        Text.text(text, x, y - Text.textHeight(scale) / 2, scale, color)

    fun playClickSound() = playLoudSound("gui.button.press", 0.5f, 1.1f)
}