package com.github.subat0m1c.hatecheaters.pvgui.v2

import com.github.subat0m1c.hatecheaters.HateCheatersObject.scope
import com.github.subat0m1c.hatecheaters.modules.ProfileViewer.scale
import com.github.subat0m1c.hatecheaters.pvgui.v2.Pages.currentPage
import com.github.subat0m1c.hatecheaters.pvgui.v2.pages.setPlayer
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.ProfileLazy
import com.github.subat0m1c.hatecheaters.utils.ApiUtils.profileOrSelected
import com.github.subat0m1c.hatecheaters.utils.JsonParseUtils.getSkyblockProfile
import com.github.subat0m1c.hatecheaters.utils.jsonobjects.HypixelProfileData.PlayerInfo
import kotlinx.coroutines.launch
import me.odinmain.utils.render.scale
import me.odinmain.utils.render.translate
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.util.MovementInputFromOptions

object PVGui : GuiScreen() {

    var playerData: PlayerInfo? = null
    var profileName: String? = null
        set(value) {
            field = value
            ProfileLazy.resetAll()
        }

    var loadText = "Loading..."

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        val sr = ScaledResolution(mc)
        scale(1f / sr.scaleFactor, 1f / sr.scaleFactor)
        translate(mc.displayWidth / 2, mc.displayHeight / 2)
        scale(scale, scale)
        currentPage.page.preDraw()
        scale(1f / scale, 1f / scale)
        translate(-mc.displayWidth / 2, -mc.displayHeight / 2)
        scale(sr.scaleFactor, sr.scaleFactor)
        super.drawScreen(mouseX, mouseY, partialTicks)
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        currentPage.page.handleClick(mouseX, mouseY, mouseButton)
        super.mouseClicked(mouseX, mouseY, mouseButton)
    }

    override fun initGui() {
        currentPage = Pages.PageEntries.Overview

        super.initGui()
    }

    override fun onGuiClosed() {
        mc.thePlayer.movementInput = MovementInputFromOptions(mc.gameSettings)
        loadText = "Loading..."
        playerData = null
        profileName = null
    }

    fun updateProfile(profile: String?) {
        playerData?.let {
            val profileData = it.profileOrSelected(profile)
            profileName = profileData?.cuteName
        }
    }

    fun loadPlayer(name: String?, profileName: String? = null) {
        scope.launch {
            getSkyblockProfile(name ?: mc.thePlayer.name, false)?.let { data ->
                playerData = data
                setPlayer(data)
                updateProfile(profileName)
            } ?: run { loadText = "Failed to grab profile data." }
        }
    }

    override fun doesGuiPauseGame(): Boolean = false
}