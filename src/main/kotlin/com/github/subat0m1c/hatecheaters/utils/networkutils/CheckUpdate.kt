package com.github.subat0m1c.hatecheaters.utils.networkutils

import com.github.subat0m1c.hatecheaters.utils.LogHandler.Logger
import me.odinmain.utils.skyblock.getChatBreak
import com.github.subat0m1c.hatecheaters.utils.OdinCheck.compareVersions
import com.github.subat0m1c.hatecheaters.HateCheaters
import com.github.subat0m1c.hatecheaters.HateCheaters.Companion.launch
import com.github.subat0m1c.hatecheaters.utils.ChatUtils
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.modMessage
import com.github.subat0m1c.hatecheaters.utils.networkutils.WebUtils.streamAndRead
import net.minecraft.event.ClickEvent
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import me.odinmain.utils.runIn

object CheckUpdate {
    private val json = Json {
        isLenient = true
        ignoreUnknownKeys = true
    }

    private const val GITHUB_API_URL = "https://api.github.com/repos/subat0m1c/HateCheaters/releases/latest"
    private inline val currentVersion: String get() = HateCheaters.version

    fun lookForUpdates() = launch {
        Logger.info("Currently on HateCheaters $currentVersion")
        checkVersion(streamAndRead<ReleaseInfo>(GITHUB_API_URL, json).getOrElse { return@launch })
    }

    private fun checkVersion(info: ReleaseInfo) = runIn(40) {
        val (latestVersion, name, notes) = info

        val isNewVersionAvailable = compareVersions(latestVersion, currentVersion)
        when {
            isNewVersionAvailable < 0 -> {
                Logger.info("HC > Development Build Detected!")
                modMessage("You are on a development build! Hi!")
            }

            isNewVersionAvailable > 0 -> {
                ChatUtils.chatConstructor {
                    displayText(getChatBreak())
                    displayText("§bHate§3Cheaters§r Update available! ($currentVersion → $latestVersion)")
                    displayText()
                    hoverText("Release: $name, release notes: §e§lHOVER§r", listOf(formatNotes(notes)))
                    displayText()
                    clickText(
                        "Click §e§lHERE§r to open the latest release link!",
                        "https://github.com/SubAt0m1c/HateCheaters/releases/tag/$latestVersion",
                        listOf("§e§lCLICK§r to open the release link."),
                        ClickEvent.Action.OPEN_URL
                    )
                    displayText(getChatBreak())
                }.print()

                Logger.info("HC > Update available.")
            }

            else -> Logger.info("HC > No update needed.")
        }
    }

    private fun formatNotes(notes: String) = notes
        .replace("\r", "")
        .replace("#", "§l")

    @Serializable
    data class ReleaseInfo(
        @SerialName("tag_name")
        val tagName: String,
        @SerialName("name")
        val name: String,
        @SerialName("body")
        val notes: String
    )
}
