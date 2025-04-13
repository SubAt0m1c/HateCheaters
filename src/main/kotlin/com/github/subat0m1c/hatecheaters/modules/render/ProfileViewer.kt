package com.github.subat0m1c.hatecheaters.modules.render

import com.github.subat0m1c.hatecheaters.HateCheaters.Companion.screen
import com.github.subat0m1c.hatecheaters.pvgui.v2.PVGui
import com.github.subat0m1c.hatecheaters.pvgui.v2.PVGui.loadPlayer
import com.github.subat0m1c.hatecheaters.utils.LogHandler.Logger
import me.odinmain.features.Module
import me.odinmain.features.settings.AlwaysActive
import me.odinmain.features.settings.Setting.Companion.withDependency
import me.odinmain.features.settings.impl.*
import me.odinmain.utils.render.Color
import me.odinmain.utils.ui.Colors
import me.odinmain.utils.ui.clickgui.util.ColorUtil.withAlpha

@AlwaysActive
object ProfileViewer : Module(
    name = "Profile Viewer",
    description = "Lets you view profiles. /pv name or /hcpv name",
) {
    val pvCommand by BooleanSetting("PV Command", default = true, description = "Enabled the /pv command. OVERWRITES NEU PV. When disabled, /hcpv will still work. REQUIRES RESTART")
    val scale by NumberSetting("Scale", default = 1.5, increment = 0.1, min = 0.1, max = 1.5, description = "Scale of the gui.")
    val maxRows by NumberSetting("Tali Rows", default = 7, increment = 1, min = 1, max = 7, description = "Maximum number of rows that can be displayed in the talisman page. Lower will give more performance, but will render less items.")
    private val themesList = arrayListOf("Classic", "Midnight", "Light", "Sunrise", "Custom")
    val themes by SelectorSetting("Theme", defaultSelected = "Classic", themesList, description = "Preferred theme")
    val main by ColorSetting("Main", default = Colors.MINECRAFT_DARK_GRAY, false, description = "Main color (primarily background).").withDependency { themes == themesList.lastIndex }
    val accent by ColorSetting("Accent", default = Colors.MINECRAFT_BLUE, true, description = "Accent color (primarily outlines).").withDependency { themes == themesList.lastIndex }
    val font by ColorSetting("Font", default = Colors.WHITE, true, description = "Font Color (only affects items without vanilla color codes).").withDependency { themes == themesList.lastIndex }
    val items by ColorSetting("Items", default = Colors.MINECRAFT_GRAY, true, description = "Background color of items").withDependency { themes == themesList.lastIndex }
    val line by ColorSetting("Line", default = Colors.BLACK, true, description = "Line Color (primarily separators).").withDependency { themes == themesList.lastIndex }
    val code by StringSetting("Code", default = "f", 1, description = "White Text Color Code (so white on white isn't bad).").withDependency { themes == themesList.lastIndex }
    val selected by ColorSetting("Selected", default = Colors.MINECRAFT_DARK_AQUA.withAlpha(0.8f), true, description = "Color for selected buttons.").withDependency { themes == themesList.lastIndex }
    val button by ColorSetting("Button", default = Color("A9A9A9FF"), true, description = "Color for buttons").withDependency { themes == themesList.lastIndex }
    val roundness by NumberSetting("Roundness", default = 10f, increment = 0.5, min = 0, max = 20f, description = "Roundness for the whole gui.").withDependency { themes == themesList.lastIndex }
    val inventoryRound by NumberSetting("Inventory Roundness", default = 0f, increment = 0.5, min = 0, max = 20f, description =  "Roundness for inventory item backgrounds.").withDependency { themes == themesList.lastIndex }
    val rarityBackgrounds by BooleanSetting("Rarity Background", default = false, description = "Renders a background according to the rarity of the item in front of it.")

    val themeEntries = listOf(
        Theme(
            "Classic",
            Colors.MINECRAFT_DARK_GRAY,
            Colors.MINECRAFT_BLUE,
            Colors.WHITE,
            Colors.MINECRAFT_GRAY,
            Colors.WHITE,
            "f",
            Colors.MINECRAFT_DARK_AQUA.withAlpha(0.8f),
            Colors.MINECRAFT_DARK_GRAY,
            10f,
            0f,
        ),
        Theme(
            "Midnight",
            Color("151345FF"),  // main
            Colors.TRANSPARENT,       // accent <- useless
            Colors.WHITE,             // font
            Color("1c1d54FF"),  // items
            Color("040622FF"),  // line
            "f",
            Color("26236bFF"),  // selected
            Color("040622FF"),  // button
            10f,
            0f,
        ),
        Theme(
            "Light",
            Colors.WHITE,        // main
            Colors.MINECRAFT_DARK_GRAY,    // accent <- useless
            Colors.BLACK,        // font
            Colors.MINECRAFT_DARK_GRAY,    // items
            Colors.MINECRAFT_DARK_GRAY,    // line
            "0",
            Colors.MINECRAFT_GRAY,         // selected
            Colors.MINECRAFT_DARK_GRAY,    // button
            10f,
            0f,
        ),
        Theme(
            "Sunrise",
            Color("fDf1CDFF"), // main
            Colors.TRANSPARENT,      // accent <- useless
            Color("805690FF"), // font
            Color("f9dc90FF"), // items
            Color("805690FF"), // line
            "b",
            Color("f89e9dFF"), // selected
            Color("d46f93FF"), // button
            10f,
            0f,
        ),
    )

    inline val currentTheme get() = themeEntries.getOrNull(themes) ?: Theme(
        "Custom",
        main,
        accent,
        font,
        items,
        line,
        code,
        selected,
        button,
        roundness,
        inventoryRound
    )

    override fun onKeybind() {
        launchPV()
    }

    override fun onEnable() {
        launchPV()
        super.onEnable()
        toggle()
    }

}

fun launchPV(name: String? = null, profile: String? = null) {
    loadPlayer(name, profile)
    screen = PVGui
    Logger.info("Trying to display pvgui")
}

data class Theme(
    val name: String,
    val main: Color,
    val accent: Color,
    val font: Color,
    val items: Color,
    val line: Color,
    val fontCode: String,
    val selected: Color,
    val button: Color,
    val roundness: Float,
    val buttons: Float
)