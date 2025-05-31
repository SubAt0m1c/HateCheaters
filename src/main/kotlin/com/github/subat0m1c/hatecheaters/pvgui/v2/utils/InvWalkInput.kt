package com.github.subat0m1c.hatecheaters.pvgui.v2.utils

import com.github.subat0m1c.hatecheaters.modules.render.ProfileViewer.invwalk
import com.github.subat0m1c.hatecheaters.pvgui.v2.PVGui
import me.odinmain.OdinMain.mc
import net.minecraft.potion.Potion
import net.minecraft.util.MovementInputFromOptions
import org.lwjgl.input.Keyboard

/**
 * moving in custom guis should be allowed since moving in them gives no in game advantage.
 *
 * the gui isn't in vanilla, nor is there a vanilla equivalent, therefore does not have vanilla restrictions applied. think of it like a hud element where your cursor can move.
 *
 * this must be toggled on, and will NOT run at ALL when toggled off, for no possible risk.
 *
 * this also uses entirely vanilla code with slight adjustments (different press detection, added sprint detection) to work in guis.
 */
class InvWalkInput : MovementInputFromOptions(mc.gameSettings) {
    override fun updatePlayerMoveState() {
        if (!invwalk || mc.currentScreen != PVGui) return super.updatePlayerMoveState() //reverts to vanilla behavior; shouldn't ever actually need to happen.
        moveStrafe = 0.0f
        moveForward = 0.0f

        if (Keyboard.isKeyDown(mc.gameSettings.keyBindForward.keyCode)) moveForward++

        if (Keyboard.isKeyDown(mc.gameSettings.keyBindBack.keyCode)) moveForward--

        if (Keyboard.isKeyDown(mc.gameSettings.keyBindLeft.keyCode)) moveStrafe++

        if (Keyboard.isKeyDown(mc.gameSettings.keyBindRight.keyCode)) moveStrafe--

        jump = Keyboard.isKeyDown(mc.gameSettings.keyBindJump.keyCode)
        sneak = Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.keyCode)

        if (sneak) {
            moveForward *= 0.3f
            moveStrafe *= 0.3f
        }

        //this being *here* isn't vanilla, however otherwise is.
        if (canSprint && Keyboard.isKeyDown(mc.gameSettings.keyBindSprint.keyCode)) mc.thePlayer.isSprinting = true
    }
}

inline val canSprint
    get() = !mc.thePlayer.isSprinting && mc.thePlayer.moveForward >= 0.8F && (mc.thePlayer.foodStats.foodLevel > 6.0F || mc.thePlayer.capabilities.allowFlying) && !mc.thePlayer.isUsingItem && !mc.thePlayer.isPotionActive(
        Potion.blindness
    )