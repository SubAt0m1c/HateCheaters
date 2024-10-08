package com.github.subat0m1c.hatecheaters.pvgui

import com.github.subat0m1c.hatecheaters.HateCheatersObject.scope
import com.github.subat0m1c.hatecheaters.modules.ProfileViewer.currentTheme
import com.github.subat0m1c.hatecheaters.modules.ProfileViewer.scale
import com.github.subat0m1c.hatecheaters.pvgui.pages.inventory.PageRendering.getTaliData
import com.github.subat0m1c.hatecheaters.pvgui.pages.inventory.PageRendering.taliItems
import com.github.subat0m1c.hatecheaters.pvgui.pages.overview.OverviewPage.getPlayer
import com.github.subat0m1c.hatecheaters.pvgui.pvutils.BackgroundDraw
import com.github.subat0m1c.hatecheaters.pvgui.pvutils.BackgroundDraw.getScreenObjects
import com.github.subat0m1c.hatecheaters.pvgui.pvutils.RenderUtils.isObjectHovered
import com.github.subat0m1c.hatecheaters.utils.JsonParseUtils.getSkyblockProfile
import com.github.subat0m1c.hatecheaters.utils.LogHandler.logger
import com.github.subat0m1c.hatecheaters.utils.jsonobjects.HypixelProfileData
import kotlinx.coroutines.launch
import me.odinmain.ui.Screen
import me.odinmain.ui.clickgui.animations.impl.EaseInOut
import me.odinmain.utils.render.*
import me.odinmain.utils.skyblock.PlayerUtils.playLoudSound
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.renderer.GlStateManager

object PVGui : Screen() {
    val pages: ArrayList<PVPage> = ArrayList()

    var currentPage: PVPage = PVEntries.Overview.page

    var animation = EaseInOut(300)
    var open = false

    inline val main: Color get() = currentTheme.main
    inline val accent: Color get() = currentTheme.accent
    inline val line: Color get() = currentTheme.line
    inline val font: Color get() = currentTheme.font
    inline val items: Color get() = currentTheme.items
    inline val c: String get() = currentTheme.fontCode
    inline val selected: Color get() = currentTheme.selected
    inline val button: Color get() = currentTheme.button
    inline val roundness: Float get() = currentTheme.roundness * scale.toFloat()
    inline val buttonRound: Float get() = currentTheme.buttons * scale.toFloat()

    var player: HypixelProfileData.PlayerInfo? = null
    var failed: String? = null

    var screen: ScreenObjects? = null

    fun init() {
        screen?.scale?.let { if (it != scale) getScreenObjects() }
        pages.clear()
        PVEntries.entries.forEach {
            pages.add(it.page)
            it.page.page.init()
        }
    }

    //@Suppress("UNREACHABLE_CODE")
    override fun draw() {
        GlStateManager.pushMatrix()
        //translate(mc.displayWidth/4, mc.displayHeight/4)
        //scale(animation.get(0f, 1f), animation.get(0f, 1f))
        //translate(-mc.displayWidth/4, -mc.displayHeight/4)
        BackgroundDraw.draw()
        GlStateManager.popMatrix()
    }
    override fun initGui() {
        init()
        logger.info("drawing pv gui")
        open = true
        animation.start(true)
    }

    override fun onGuiClosed() {
        failed = null
        player = null
        open = false
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        val screen = screen ?: return
        pages.filterIndexed { index, pvPage ->
            isObjectHovered(screen.lineY, pvPage.getY(screen, index), screen.pageWidth, screen.pageHeight, mouseX, mouseY, screen)
        }.firstOrNull()?.let {
            if (currentPage == it) return@let
            currentPage = it
            playLoudSound("gui.button.press", 0.5f, 1.1f)
        } ?: currentPage.page.mouseClick(mouseX, mouseY, mouseButton)
        super.mouseClicked(mouseX, mouseY, mouseButton)
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {
        if (keyCode == 27 && !animation.isAnimating()) {
            mc.displayGuiScreen(null as GuiScreen?)
            if (mc.currentScreen == null) {
                mc.setIngameFocus()
            }
        }
        super.keyTyped(typedChar, keyCode)
    }

    fun drawLoading(centerX: Double, centerY: Double, x: Double, y: Double) {
        val scale = 7f * scale
        var text = "Loading..."
        failed?.let { text = it }
        val width = getMCTextWidth(text)*scale
        mcText(text, x + (centerX - width/2), y+(centerY - (getMCTextHeight()*scale)/2), scale, font, center = false)
    }


    fun loadPlayer(name: String?) {
        scope.launch {
            getSkyblockProfile(name ?: mc.thePlayer.name, false)?.let { data ->
                player = data
                taliItems = null
                getTaliData(data)
                getPlayer(data)
            } ?: run { failed = "Failed to grab profile data." }
        }
    }
}