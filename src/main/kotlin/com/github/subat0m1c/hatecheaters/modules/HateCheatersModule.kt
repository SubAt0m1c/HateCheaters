package com.github.subat0m1c.hatecheaters.modules

import com.github.subat0m1c.hatecheaters.utils.CheckUpdate
import me.odinmain.features.Category
import me.odinmain.features.Module
import me.odinmain.features.settings.AlwaysActive
import me.odinmain.features.settings.impl.BooleanSetting
import me.odinmain.features.settings.impl.StringSetting

@AlwaysActive
object HateCheatersModule : Module(
    name = "Hate Cheaters",
    description = "Hate cheaters nonsense",
    category = Category.SKYBLOCK
) {
    val logJson by BooleanSetting("Log Json", default = false, description = "Logs requested json data to Config/hatecheaters/json_logs")
    val debugMessages by BooleanSetting("Debug messages", default = false, description = "Prints debug messages in your chat instead of needing to open logs.")
    private val checkUpdates by BooleanSetting("Update Checker", default = true, description = "Checks GitHub for latest HateCheaters releases and notifies you if you are on an old version!")
    val server by StringSetting("Api Server", default = "default", hidden = false, description = "Server to be used to connect to the api. set to \"default\" to use the default. Only change if you know what you're doing. format: \"subdomain.domain.tld\"")

    init {
        execute(250) {
            if (!checkUpdates) destroyExecutor()
            mc.thePlayer?.let {
                CheckUpdate.lookForUpdates()
                destroyExecutor()
            }
        }
    }
}