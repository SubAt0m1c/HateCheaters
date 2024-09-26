package com.github.subat0m1c.hatecheaters.pvgui.pvutils

import com.github.subat0m1c.hatecheaters.pvgui.*
import com.github.subat0m1c.hatecheaters.modules.ProfileViewer.scale
import com.github.subat0m1c.hatecheaters.pvgui.PVGui.accent
import com.github.subat0m1c.hatecheaters.pvgui.PVGui.button
import com.github.subat0m1c.hatecheaters.pvgui.PVGui.currentPage
import com.github.subat0m1c.hatecheaters.pvgui.PVGui.drawLoading
import com.github.subat0m1c.hatecheaters.pvgui.PVGui.font
import com.github.subat0m1c.hatecheaters.pvgui.PVGui.line
import com.github.subat0m1c.hatecheaters.pvgui.PVGui.main
import com.github.subat0m1c.hatecheaters.pvgui.PVGui.pages
import com.github.subat0m1c.hatecheaters.pvgui.PVGui.player
import com.github.subat0m1c.hatecheaters.pvgui.PVGui.screen
import com.github.subat0m1c.hatecheaters.pvgui.PVGui.selected
import me.odinmain.OdinMain
import me.odinmain.OdinMain.mc
import me.odinmain.utils.floor
import me.odinmain.utils.render.*
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import org.lwjgl.opengl.Display
import kotlin.math.floor
import kotlin.math.max

object BackgroundDraw {

    fun draw() {
        GlStateManager.pushMatrix()
        val screen = screen ?: getScreenObjects()
        val profile = player

        scale(scaledFactor, scaledFactor, 1f)

        //scale(1f * animation.getPercent(), 1f * animation.getPercent())

        val centerW: Double = screen.totalCenterX

        val centerH =  screen.totalCenterY

        val width =  screen.totalWidth
        val height =  screen.totalHeight

        val ot =  floor(screen.outlineThickness)

        // main page
        translate(centerW - width/2, centerH -  height/2)
        roundedRectangle(0-ot, 0-ot, width+ot*2, height+ot*2, accent, radius = 10f, edgeSoftness = 1f)
        roundedRectangle(0, 0, width, height, main, radius = 10f, edgeSoftness = 1f)

        val lineX = screen.lineX

        val lineY = screen.lineY

        val mainX = lineX+ot + lineY
        val mainWidth = width - (lineX + lineY*2)

        // page list stuff rendering

        roundedRectangle(lineX, lineY, ot, height-lineY*2, line)

        val pageHeight = screen.pageHeight.floor().toDouble()
        val pageWidth = screen.pageWidth.floor().toDouble()

        val centerX = lineY + (pageWidth/2)

        pages.forEachIndexed { i, page ->
            val pageY = page.getY(screen, i)
            roundedRectangle(lineY-ot, pageY-ot, pageWidth+ot*2, floor(pageHeight+ot*2), accent, radius = 10f, edgeSoftness = 1f)
            if (currentPage == page) roundedRectangle(lineY, pageY, pageWidth, pageHeight, selected, radius = 10f, edgeSoftness = 1f)
            else roundedRectangle(lineY, pageY, pageWidth, floor(pageHeight), button, radius = 10f, edgeSoftness = 1f)

            val textWidth = getMCTextWidth(page.name)
            val textScale = 3f * screen.scale
            val centerY = lineY + (pageHeight/2 + ((pageHeight + lineY) * i))
            mcText(page.name, centerX-(textWidth/2)*textScale, centerY-(getMCTextHeight() *textScale)/2, textScale, font, shadow = true, center = false)
        }

        val lastPageY = screen.lineY + ((screen.pageHeight + screen.lineY) * pages.size+1)
        val lastPageHeight = screen.totalHeight - lastPageY - lineY
        roundedRectangle(lineY-ot, lastPageY-ot, pageWidth+ot*2, lastPageHeight+ot*2, accent, radius = 10f, edgeSoftness = 1f)
        roundedRectangle(lineY, lastPageY, pageWidth, lastPageHeight, main, radius = 10f, edgeSoftness = 1f)
        val betaText = if (currentPage != PVEntries.Overview.page && profile != null) profile.name else "HCPV Beta 0"
        val betaTextScale = (if (betaText.length >= 12) 2f else 3f) * screen.scale
        val pvTextWidth = getMCTextWidth(betaText)
        val lastPageCenterY = lastPageY + (lastPageHeight/2)
        mcText(betaText, centerX - (pvTextWidth/2)*betaTextScale, lastPageCenterY-((getMCTextHeight() *betaTextScale)/2), betaTextScale, font, true, false)
        player?.let { currentPage.page.draw(screen, it) } ?: drawLoading( mainWidth/2, (height-lineY*2)/2, mainX, lineY)
        GlStateManager.popMatrix()
    }

    fun getScreenObjects(): ScreenObjects {

        val disHeight = mc.displayHeight.toDouble()  * scale
        val width = floor(disHeight*1.245)
        val height = floor(disHeight*0.7)
        val ot = floor(disHeight*0.001).coerceAtLeast(1.0)
        val lineX = floor(disHeight*0.2) - 2
        val lineY =  floor(disHeight*0.01) - 2

        val mainWidth = (width - (lineX + lineY*2))-ot
        val mainHeight = height-lineY*2

        val pageHeight = ((height-lineY * (2 + pages.size-1))*0.9)/pages.size
        val pageWidth = lineX - lineY*2
        val mainX = (lineX+ot) + (lineY)


        val objects = ScreenObjects(
            mc.displayWidth.toDouble()/2,
            mc.displayHeight.toDouble()/2,
            width,
            height,
            lineX,
            lineY,
            mainX,
            lineY,
            mainX + (mainWidth/2),
            lineY + (mainHeight/2),
            mainHeight,
            mainWidth,
            pageHeight,
            pageWidth,
            ot,
            scale,
        )

        return objects.also { screen = it }
    }
}