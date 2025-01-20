package com.github.subat0m1c.hatecheaters.commands.impl

import com.github.stivais.commodore.utils.GreedyString
import com.github.subat0m1c.hatecheaters.HateCheaters.Companion.launch
import com.github.subat0m1c.hatecheaters.commands.commodore
import com.github.subat0m1c.hatecheaters.modules.HateCheatersModule
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.modMessage
import com.github.subat0m1c.hatecheaters.utils.WebUtils
import com.github.subat0m1c.hatecheaters.utils.WebUtils.testQue
import com.github.subat0m1c.hatecheaters.utils.apiutils.ApiUtils.memberData
import com.github.subat0m1c.hatecheaters.utils.apiutils.ParseUtils.getSkyblockProfile
import me.odinmain.config.Config
import net.minecraft.client.gui.GuiScreen
import java.util.*
import kotlin.concurrent.schedule

val DevCommand = commodore("hcdev") {

    var server: String? = null

    literal("apitest") {
        runs { name: String, skipCache: Boolean?, forceSkycrypt: Boolean? ->
            launch {
                getSkyblockProfile(name, skipCache ?: false, forceSkycrypt ?: false)
                    .getOrElse { return@launch modMessage(it.message) }.memberData ?: return@launch modMessage("Could not find player data.")
                modMessage("Succeeded!")
            }
        }

        literal("que").runs {
            launch {
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
            The expected server uses custom endpoints, which means the input server must be complete until a <${WebUtils.Server.EndPoints.entries.joinToString(" | ")}>/<uuid>.
            For example: "http://servernamehere/v2/skyblock/" will run as http://servernamehere/v2/skyblock/get/<uuid>.
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

    literal("writetoclipboard").runs { text: GreedyString ->
        GuiScreen.setClipboardString(text.string)
        modMessage("Copied \"${text.string}\"!")
    }
}