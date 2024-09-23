package com.github.subat0m1c.hatecheaters.pvgui

import com.github.subat0m1c.hatecheaters.pvgui.PVGui.font
import com.github.subat0m1c.hatecheaters.pvgui.pages.dungeons.DungeonsPage
import com.github.subat0m1c.hatecheaters.pvgui.pages.inventory.InventoryPage
import com.github.subat0m1c.hatecheaters.pvgui.pages.overview.OverviewPage
import com.github.subat0m1c.hatecheaters.pvgui.pages.pets.PetsPage
import com.github.subat0m1c.hatecheaters.pvgui.pages.profile.ProfilePage
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.mcWidth
import com.github.subat0m1c.hatecheaters.utils.jsonobjects.HypixelProfileData.PlayerInfo
import me.odinmain.OdinMain.mc
import me.odinmain.utils.render.getMCTextHeight
import me.odinmain.utils.render.mcText
import net.minecraft.client.gui.ScaledResolution
import org.lwjgl.input.Mouse

inline val scaledFactor get() = 1f / ScaledResolution(mc).scaleFactor

enum class PVEntries(
    val page: PVPage
) {
    Overview(page = PVPage("Overview", OverviewPage)),
    Profile(page = PVPage("Profile", ProfilePage)),
    Dungeons(page = PVPage("Dungeons", DungeonsPage)),
    Inventory(page = PVPage("Inventory", InventoryPage)),
    Pets(page = PVPage("Pets", PetsPage)),
}

data class PVPage(
    val name: String,
    val page: PVGuiPage,
    val current: Boolean = false,
)

abstract class PVGuiPage {

    open fun draw(screen: ScreenObjects, player: PlayerInfo) {
        val text = "This page doesn't exist yet!"
        val text2 = "ETA: None!"
        val centerY = screen.mainCenterY
        val fontScale = 3f
        mcText(text, screen.mainCenterX-((text.mcWidth*fontScale)/2), centerY-((getMCTextHeight() *fontScale)), fontScale, font, center = false)
        mcText(text2, screen.mainCenterX-((text2.mcWidth*fontScale)/2), centerY+((getMCTextHeight() *fontScale)), fontScale, font, center = false)
    }

    open fun mouseClick(x: Int, y: Int, button: Int) {

    }

    open fun init() {

    }
}

data class ScreenObjects(
    val totalCenterX: Double,
    val totalCenterY: Double,
    val totalWidth: Double,
    val totalHeight: Double,
    val lineX: Double,
    val lineY: Double,
    val mainX: Double,
    val mainY: Double,
    val mainCenterX: Double,
    val mainCenterY: Double,
    val mainHeight: Double,
    val mainWidth: Double,
    val pageHeight: Double,
    val pageWidth: Double,
    val outlineThickness: Double,
)

fun ScreenObjects.getRealX(x: Double): Double {
    return ((this.totalCenterX - this.totalWidth/2) + x) * scaledFactor
}

fun ScreenObjects.getRealY(x: Double): Double {
    return ((this.totalCenterY - this.totalHeight/2) + x) * scaledFactor
}

fun ScreenObjects.getScaledW(w: Double): Double {
    return w * scaledFactor
}

fun ScreenObjects.getScaledH(h: Double): Double {
    return h * scaledFactor
}

val ScreenObjects.mouseX: Double get() {
    val mouseX = Mouse.getX() * mc.currentScreen.width / mc.displayWidth
    return mouseX / scaledFactor - (this.totalCenterX - this.totalWidth/2)
}

val ScreenObjects.mouseY: Double get() {
    val mouseY = mc.currentScreen.height - Mouse.getY() * mc.currentScreen.height / mc.displayHeight - 1
    return mouseY / scaledFactor - (this.totalCenterY - this.totalHeight/2)
}


fun PVPage.getY(screen: ScreenObjects, index: Int): Double {
    return screen.lineY + ((screen.pageHeight + screen.lineY) * index)
}
