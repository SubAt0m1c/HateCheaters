package com.github.subat0m1c.hatecheaters.commands.impl

import com.github.subat0m1c.hatecheaters.HateCheaters.Companion.scope
import com.github.subat0m1c.hatecheaters.commands.commodore
import com.github.subat0m1c.hatecheaters.modules.HateCheatersModule
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.modMessage
import com.github.subat0m1c.hatecheaters.utils.JsonParseUtils.getSkyblockProfile
import com.github.subat0m1c.hatecheaters.utils.WebUtils.testQue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.odinmain.config.Config
import java.util.*
import kotlin.concurrent.schedule

val DevCommand = commodore("hcdev") {

    var server: String? = null

    literal("apitest") {
        runs { name: String, skipCache: Boolean?, forceSkycrypt: Boolean? ->
            scope {
                val profiles = getSkyblockProfile(name, null, skipCache ?: false, forceSkycrypt ?: false)
                    .getOrElse { return@scope modMessage(it.message) }
                profiles.profileData.profiles.find { it.selected }?.members?.get(profiles.uuid)
                    ?: return@scope modMessage("Could not find player data.")
                modMessage("Succeeded!")
            }
        }

        literal("que").runs {
            scope {
                modMessage("testing que...")
                testQue()
            }
        }
    }

    literal("server") {
        literal("get").runs {
            modMessage("Current server: ${HateCheatersModule.server}")
        }

        runs { serverInput: String ->
            modMessage("""
            Are you sure you want to change the api server to $serverInput? 
            Changing it as an end user violates Hypixel's API terms of service.
            The expected server uses a custom endpoint, which means the input server must be complete until the uuid, for example: "http://servernamehere/v2/skyblock/profiles?uuid="
            Run /hcserver default to set the server to the default.
            Run /hcdev confirm to confirm.
            """.trimIndent())
            server = serverInput
            Timer().schedule(5000) {
                server = null
            }
        }
    }

    literal("confirm").runs {
        server?.let { ser ->
            HateCheatersModule.server = ser
            modMessage("Set server to ${HateCheatersModule.server}")
            Config.save()
            modMessage(HateCheatersModule.server)
        } ?: modMessage("Server not set! Run /hcdev server to set one.")
    }
}