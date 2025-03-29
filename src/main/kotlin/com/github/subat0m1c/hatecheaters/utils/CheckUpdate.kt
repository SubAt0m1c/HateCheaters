package com.github.subat0m1c.hatecheaters.utils

import com.github.subat0m1c.hatecheaters.utils.LogHandler.Logger
import me.odinmain.utils.skyblock.getChatBreak
import com.github.subat0m1c.hatecheaters.utils.OdinCheck.compareVersions
import com.github.subat0m1c.hatecheaters.HateCheaters
import com.github.subat0m1c.hatecheaters.HateCheaters.Companion.launch
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.modMessage
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
        try {
            Logger.info("Currently on HateCheaters $currentVersion")
            val jsonString = WebUtils.getInputStream(GITHUB_API_URL).getOrElse { return@launch }.bufferedReader().use { it.readText() }
            checkVersion(jsonString)
        } catch (e: Exception) {
            Logger.warning("Error while checking for updates: ${e.message}")
        }
    }

    private fun checkVersion(jsonResponse: String) {
        val latestVersion = json.decodeFromString<ReleaseInfo>(jsonResponse).tagName

        val isNewVersionAvailable = compareVersions(latestVersion, currentVersion)
        runIn(40) {
           when {
               isNewVersionAvailable < 0 -> {
                   Logger.info("HC > Development Build Detected!")
                   modMessage("You are on a development build! Hi!")
               }
               isNewVersionAvailable > 0 -> {
                   ChatUtils.chatConstructor {
                       displayText(getChatBreak())
                       displayText("§bH§3C §8»§r Update available! ($currentVersion → $latestVersion) ")
                       clickText(
                           "Click here to open the latest release link!",
                           "https://github.com/SubAt0m1c/HateCheaters/releases/tag/$latestVersion",
                           listOf("Click to open the release link."),
                           ClickEvent.Action.OPEN_URL
                       )
                       displayText(getChatBreak())
                   }.print()

                   Logger.info("HC > Update available.")
               } else -> Logger.info("HC > No update needed.")
           }
        }
    }

    @Serializable
    data class ReleaseInfo(
        @SerialName("tag_name")
        val tagName: String
    )
}
