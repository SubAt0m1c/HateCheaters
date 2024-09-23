package com.github.subat0m1c.hatecheaters.modules

import me.odinmain.OdinMain.display
import me.odinmain.features.Category
import me.odinmain.features.Module
import me.odinmain.features.settings.AlwaysActive
import me.odinmain.features.settings.impl.BooleanSetting
import me.odinmain.features.settings.impl.StringSetting
import net.minecraft.client.gui.GuiScreen
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent

@AlwaysActive
object HateCheatersModule : Module(
    name = "Hate Cheaters",
    description = "Hate cheaters nonsense",
    category = Category.SKYBLOCK
) {
    val logJson: Boolean by BooleanSetting("Log Json", default = false, description = "Logs requested json data to Config/hatecheaters/json_logs")

    var server: String by StringSetting("Api Server", default = "default", hidden = true, description = "Server to be used to connect to the api")
}