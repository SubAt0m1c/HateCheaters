package com.github.subat0m1c.hatecheaters.utils

import com.github.subat0m1c.hatecheaters.utils.ChatUtils
import com.github.subat0m1c.hatecheaters.utils.LogHandler.Logger
import com.github.subat0m1c.hatecheaters.HateCheaters
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

object CheckUpdate { // hi my goat SubAt0m1c <3
    private const val githubAPIURL = "https://api.github.com/repos/subat0m1c/HateCheaters/releases/latest"
    private val currentVersion: String = HateCheaters.version
    private val updateScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    fun lookForUpdates() {
        updateScope.launch {
            try {
                val url = URL(githubAPIURL)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.setRequestProperty("Accept", "application/json")
                connection.setRequestProperty("User-Agent", "Mozilla/5.0")
                connection.connectTimeout = 5000
                connection.readTimeout = 5000

                Logger.info("Currently on HateCheaters $currentVersion")

                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    val jsonResponse = connection.inputStream.bufferedReader().use { it.readText() }

                    checkVersion(jsonResponse)
                } else {
                    Logger.warning("Failed to check GitHub releases. HTTP ${connection.responseCode}")
                }
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
            val newReleaseURL = "https://github.com/SubAt0m1c/HateCheaters/releases/tag/"
            val releaseLink = "$newReleaseURL$latestVersion"
            delay(2000)
            ChatUtils.modMessage("Update available! ($currentVersion → $latestVersion)")
            ChatUtils.chatConstructor {
                clickText(
                    "§bH§3C §8»§r Click HERE to copy the latest release link",
                    "/hcdev writetoclipboard $releaseLink",
                    hoverText = listOf("Click to copy the release link to clipboard") // ill try make it open the link but idk how to do that atm!
                )
            }.print()
            Logger.info("HC > Update available.")
        } else {
            Logger.info("HC > No update needed.")
        }
    }

    private fun compareVersions(version1: String, version2: String): Int {
        fun parseVersion(version: String): List<Int> {
            return if (version.contains(".")) {
                version.split(".").map { it.toIntOrNull() ?: 0 }
            } else {
                version.map { it.toString().toIntOrNull() ?: 0 }
            }
        }

        val v1Components = parseVersion(version1)
        val v2Components = parseVersion(version2)

        val maxLength = maxOf(v1Components.size, v2Components.size)
        val paddedV1 = v1Components + List(maxLength - v1Components.size) { 0 }
        val paddedV2 = v2Components + List(maxLength - v2Components.size) { 0 }

        for (i in 0 until maxLength) {
            if (paddedV1[i] > paddedV2[i]) return 1
            if (paddedV1[i] < paddedV2[i]) return -1
        }
        return 0
    }
}
