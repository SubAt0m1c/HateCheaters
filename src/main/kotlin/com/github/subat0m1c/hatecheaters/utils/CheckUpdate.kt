package com.github.subat0m1c.hatecheaters.utils

import com.github.subat0m1c.hatecheaters.utils.ChatUtils
import com.github.subat0m1c.hatecheaters.utils.LogHandler.Logger
import com.github.subat0m1c.hatecheaters.utils.WebUtils
import me.odinmain.utils.skyblock.getChatBreak
import com.github.subat0m1c.hatecheaters.utils.OdinCheck.compareVersions
import com.github.subat0m1c.hatecheaters.modules.HateCheatersModule.checkUpdates
import com.github.subat0m1c.hatecheaters.HateCheaters
import net.minecraft.event.ClickEvent
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import kotlinx.coroutines.*
import java.net.HttpURLConnection
import java.net.URL

val json = Json {
    isLenient = true
    ignoreUnknownKeys = true
}

@Serializable
data class ReleaseInfo(
    val tag_name: String
)

object CheckUpdate {
    private const val githubAPIURL = "https://api.github.com/repos/subat0m1c/HateCheaters/releases/latest"
    private inline val currentVersion: String get() = HateCheaters.version

    fun lookForUpdates() {
        if (!checkUpdates) {
            return
        }

        HateCheaters.scope.launch {
            try {
                Logger.info("Currently on HateCheaters $currentVersion")
                val jsonResponse = WebUtils.getInputStream(githubAPIURL).getOrElse {
                    return@launch Logger.warning("Failed to fetch data from GitHub API: ${it.message}")
                }
                val jsonString = jsonResponse.bufferedReader().use { it.readText() }
                checkVersion(jsonString)
            } catch (e: Exception) {
                Logger.warning("Error while checking for updates: ${e.message}")
            }
        }
    }


    private suspend fun checkVersion(jsonResponse: String) {
        val releaseInfo = json.decodeFromString<ReleaseInfo>(jsonResponse)
        val latestVersion = releaseInfo.tag_name

        val isNewVersionAvailable = compareVersions(latestVersion, currentVersion)
        if (isNewVersionAvailable < 0) {
            delay(2000)
            ChatUtils.modMessage("You are on a development build! Hi!")
            Logger.info("HC > Development Build Detected!")
        } else if (isNewVersionAvailable > 0) {
            val releaseLink = "https://github.com/SubAt0m1c/HateCheaters/releases/tag/$latestVersion"
            delay(2000)
            ChatUtils.chatConstructor {
                displayText(getChatBreak())
                displayText("§bH§3C §8»§r Update available! ($currentVersion → $latestVersion) ")
                clickText(
                    "Click here to open the latest release link!",
                    releaseLink,
                    listOf("Click to open the release link."),
                    ClickEvent.Action.OPEN_URL
                )
                displayText(getChatBreak())
            }.print()

            Logger.info("HC > Update available.")
        } else {
            Logger.info("HC > No update needed.")
        }
    }
}
