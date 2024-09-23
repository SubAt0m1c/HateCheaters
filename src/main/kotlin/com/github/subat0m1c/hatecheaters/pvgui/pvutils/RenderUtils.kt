package com.github.subat0m1c.hatecheaters.pvgui.pvutils

import com.github.subat0m1c.hatecheaters.pvgui.*
import com.github.subat0m1c.hatecheaters.pvgui.PVGui.font
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.mcWidth
import me.odinmain.OdinMain.mc
import me.odinmain.utils.render.getMCTextHeight
import me.odinmain.utils.render.mcText
import me.odinmain.utils.render.translate
import net.minecraft.client.gui.inventory.GuiInventory.drawEntityOnScreen
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.EntityLivingBase
import org.lwjgl.input.Mouse
import java.util.*


object RenderUtils {

    fun drawPlayerOnScreen(x: Double, y: Double, scale: Int, mouseX: Int, mouseY: Int, renderPlayer: EntityLivingBase, screen: ScreenObjects) {
        GlStateManager.disableLighting()
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
        translate(x, y, 200f)
        drawEntityOnScreen(0, 0, scale, ((x*2)-mouseX).toFloat(), (mouseY-y).toFloat(), renderPlayer)
        GlStateManager.enableLighting()
    }

    fun isObjectHovered(x: Double, y: Double, w: Double, h: Double, mouseX: Int, mouseY: Int, screen: ScreenObjects): Boolean {
        val realX = screen.getRealX(x)
        val realY = screen.getRealY(y)
        val scaledW = screen.getScaledW(w)
        val scaledH = screen.getScaledH(h)
        return (mouseX.toDouble() in realX..realX+scaledW && mouseY.toDouble() in realY..realY+scaledH)
    }

    fun isObjectHovered(x: Double, y: Double, w: Double, h: Double, screen: ScreenObjects): Boolean {
        val realX = screen.getRealX(x)
        val realY = screen.getRealY(y)
        val scaledW = screen.getScaledW(w)
        val scaledH = screen.getScaledH(h)
        return (getMouseX.toDouble() in realX..realX+scaledW && getMouseY.toDouble() in realY..realY+scaledH)
    }

    fun getDashedUUID(uuidStr: String): UUID {
        val formattedUUID = uuidStr.substring(0, 8) + "-" +
                uuidStr.substring(8, 12) + "-" +
                uuidStr.substring(12, 16) + "-" +
                uuidStr.substring(16, 20) + "-" +
                uuidStr.substring(20, 32);

        return UUID.fromString(formattedUUID)
    }

    val getMouseX: Int get() = Mouse.getX() * mc.currentScreen.width / mc.displayWidth

    val getMouseY: Int get() = mc.currentScreen.height - Mouse.getY() * mc.currentScreen.height / mc.displayHeight - 1

    fun somethingWentWrong(screen: ScreenObjects) {
        val text = "Something went Wrong!"
        val text2 = "This shouldn't happen! Please report it! (with logs)"
        val centerY = screen.mainCenterY
        val fontScale = 3f
        mcText(text, screen.mainCenterX - ((text.mcWidth * fontScale) / 2), centerY - ((getMCTextHeight() * fontScale)), fontScale, font, center = false)
        mcText(text2, screen.mainCenterX - ((text2.mcWidth * fontScale) / 2), centerY + ((getMCTextHeight() * fontScale)), fontScale, font, center = false)
    }

}