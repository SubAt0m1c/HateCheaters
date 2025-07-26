package com.github.subat0m1c.hatecheaters.pvgui.v2

import com.github.subat0m1c.hatecheaters.HateCheaters.Companion.launch
import com.github.subat0m1c.hatecheaters.modules.render.ProfileViewer
import com.github.subat0m1c.hatecheaters.modules.render.ProfileViewer.invwalk
import com.github.subat0m1c.hatecheaters.modules.render.ProfileViewer.scale
import com.github.subat0m1c.hatecheaters.pvgui.v2.pages.Overview.setPlayer
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.InvWalkInput
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.ResettableLazy
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.modMessage
import com.github.subat0m1c.hatecheaters.utils.apiutils.HypixelApi.getProfile
import com.github.subat0m1c.hatecheaters.utils.apiutils.HypixelApi.getProfileUUID
import com.github.subat0m1c.hatecheaters.utils.apiutils.HypixelData.PlayerInfo
import com.github.subat0m1c.hatecheaters.utils.odinwrappers.Shaders
import me.odinmain.OdinMain
import me.odinmain.clickgui.settings.impl.KeybindSetting
import me.odinmain.utils.ui.animations.LinearAnimation
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.MovementInputFromOptions

object PVGui : GuiScreen() {
    val animation = LinearAnimation<Float>(400)

    var playerData: PlayerInfo? = null
    var profileName: String? = null
        set(value) {
            field = value
            ResettableLazy.resetAll()
        }

    var loadText = "Loading..."

    private val sr get() = ScaledResolution(OdinMain.mc)

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        Shaders.startDraw()
        Shaders.pushMatrix()
        Shaders.center()
        Shaders.scale(scale.toFloat())

        GlStateManager.scale(1.0 / sr.scaleFactor, 1.0 / sr.scaleFactor, 1.0)
        GlStateManager.translate(OdinMain.mc.displayWidth / 2.0, OdinMain.mc.displayHeight / 2.0, 0.0)
        GlStateManager.scale(scale, scale, 1.0)

        if (animation.isAnimating()) {
            Shaders.translate(0f, animation.get(-10f, 0f))
            Shaders.globalAlpha(animation.get(0f, 1f))
        }

        PageHandler.preDraw()
        Shaders.popMatrix()
        Shaders.stopDraw()
//        scale(1f / scale, 1f / scale, 1.0)
//        translate(-mc.displayWidth / 2.0, -mc.displayHeight / 2.0, -1.0)
//        scale(sr.scaleFactor.toDouble(), sr.scaleFactor.toDouble(), 1.0)
        super.drawScreen(mouseX, mouseY, partialTicks)
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        PageHandler.handleClick(mouseButton)
        super.mouseClicked(mouseX, mouseY, mouseButton)
    }

    override fun initGui() {
        animation.start()
        PageHandler.reset()

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
        playerData?.name?.let { ProfileViewer.loaded(it, profileName) }

        loadText = "Loading..."
        playerData = null
        profileName = null
    }

    fun updateProfile(profile: String?) = playerData?.profileOrSelected(profile)?.cuteName?.let { profileName = it }

    fun loadPlayer(player: ProfileViewer.PvPlayer) = launch {
        if (player.uuid != null) getProfileUUID(
            player.uuid.toString().replace("-", ""),
            player.name
        ).fold(::successfulLoad, ::loadFail)
        else getProfile(player.name).fold(::successfulLoad, ::loadFail)
    }

    private fun successfulLoad(player: PlayerInfo) {
        if (player.profileData.profiles.isEmpty()) {
            loadText = "No profiles found for ${player.name}."
            return
        }
        playerData = player
        setPlayer(player)
        updateProfile(profileName)
    }

    private fun loadFail(error: Throwable) {
        modMessage(error.message)
        loadText = "Failed to grab profile data."
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {
        if ((ProfileViewer.settings.find { it.name == "Keybind" } as KeybindSetting).value.key == keyCode) {
            mc.displayGuiScreen(null)
        }

        super.keyTyped(typedChar, keyCode)
    }

    override fun doesGuiPauseGame(): Boolean = false
}