package com.github.subat0m1c.hatecheaters.pvgui.v2

import com.github.subat0m1c.hatecheaters.HateCheaters.Companion.launch
import com.github.subat0m1c.hatecheaters.modules.render.ProfileViewer.invwalk
import com.github.subat0m1c.hatecheaters.modules.render.ProfileViewer.scale
import com.github.subat0m1c.hatecheaters.pvgui.v2.Pages.currentPage
import com.github.subat0m1c.hatecheaters.pvgui.v2.pages.Overview.setPlayer
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.InvWalkInput
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.ProfileLazy
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.modMessage
import com.github.subat0m1c.hatecheaters.utils.apiutils.HypixelData.PlayerInfo
import com.github.subat0m1c.hatecheaters.utils.apiutils.HypixelApi.getProfile
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager.scale
import net.minecraft.client.renderer.GlStateManager.translate
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
        scale(1.0 / sr.scaleFactor, 1.0 / sr.scaleFactor, 1.0)
        translate(mc.displayWidth / 2.0, mc.displayHeight / 2.0, 1.0)
        scale(scale, scale, 1.0)
        currentPage.page.preDraw()
        scale(1f / scale, 1f / scale, 1.0)
        translate(-mc.displayWidth / 2.0, -mc.displayHeight / 2.0, -1.0)
        scale(sr.scaleFactor.toDouble(), sr.scaleFactor.toDouble(), 1.0)
        super.drawScreen(mouseX, mouseY, partialTicks)
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        currentPage.page.handleClick(mouseX, mouseY, mouseButton)
        super.mouseClicked(mouseX, mouseY, mouseButton)
    }

    override fun initGui() {
        currentPage = Pages.PageEntries.Overview

        if (!invwalk) return
        mc.thePlayer?.let { player ->
            if (player.movementInput !is InvWalkInput) {
                player.movementInput = InvWalkInput()
            }
        }

        super.initGui()
    }

    override fun onGuiClosed() {
        mc.thePlayer.movementInput = MovementInputFromOptions(mc.gameSettings)
        loadText = "Loading..."
        playerData = null
        profileName = null
    }

    fun updateProfile(profile: String?) = playerData?.profileOrSelected(profile)?.cuteName?.let { profileName = it }

    fun loadPlayer(name: String?, profileName: String? = null) = launch {
        getProfile(name ?: mc.thePlayer.name).fold(
            onSuccess = {
                playerData = it
                setPlayer(it)
                updateProfile(profileName)
            }, onFailure = {
                modMessage(it.message)
                failed()
            }
        )
    }

    private fun failed() { loadText = "Failed to grab profile data." }

    override fun doesGuiPauseGame(): Boolean = false
}