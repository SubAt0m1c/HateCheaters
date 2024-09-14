package com.github.subat0m1c.hatecheaters.utils

import com.github.subat0m1c.hatecheaters.utils.ChatUtils.modMessage
import com.github.subat0m1c.hatecheaters.utils.jsonobjects.DungeonData.Profile as DungeonProfile
import com.github.subat0m1c.hatecheaters.utils.jsonobjects.DungeonData.Profiles as DungeonProfiles
import com.github.subat0m1c.hatecheaters.utils.jsonobjects.ProfileData.Profile as FullProfile
import com.github.subat0m1c.hatecheaters.utils.jsonobjects.ProfileData.Profiles as FullProfiles
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.File
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object JsonParseUtils {

    val parsedPlayerList: MutableMap<String, Pair<FullProfiles, Long>> = mutableMapOf()

    private val json: Json = Json { ignoreUnknownKeys = true }

    private fun parseDungeonData(inputStream: InputStream, name: String = "unknown"): DungeonProfiles {
        val jsonString = inputStream.bufferedReader().use { it.readText() }

        logJsonToFile(jsonString, name, "Dungeons")

        val profilesData: DungeonProfiles = json.decodeFromString(jsonString)
        return profilesData
    }

    private fun parsePlayerData(inputStream: InputStream, name: String = "unknown"): FullProfiles {
        val jsonString = inputStream.bufferedReader().use { it.readText() }

        logJsonToFile(jsonString, name, "Profiles")

        val profilesData: FullProfiles = json.decodeFromString(jsonString)
        return profilesData
    }

    suspend fun getPlayerProfile(name: String, skipCache: Boolean = false): FullProfile? = withContext(Dispatchers.IO) {
        return@withContext try {
            parsedPlayerList[name]?.second?.let {
                if (System.currentTimeMillis() - it >= 600000 || skipCache) {
                    modMessage("removed $name from cache because they havent been cached in 10 minutes.")
                    parsedPlayerList.remove(name)
                    return@let null
                }
                modMessage("Using cached data for $name")
                return@withContext parsedPlayerList[name]?.first?.profiles?.values?.find { it.current }
            }

            modMessage("Fetching player profile data for $name...")
            val inputStream = getProfileDataStream(name, "https://sky.shiiyu.moe/api/v2/profile/") ?: return@withContext null
            val profiles = parsePlayerData(inputStream, name)
            addToCache(name, profiles)
            profiles.profiles.values.find { it.current }
        } catch (e: Exception) {
            modMessage("Error fetching player profile data for $name: ${e.message}")
            null
        }
    }

    suspend fun getDungeonProfile(name: String): DungeonProfile? = withContext(Dispatchers.IO) {
        return@withContext try {
            modMessage("Fetching dungeon profile data for $name...")
            val inputStream = getProfileDataStream(name, "https://sky.shiiyu.moe/api/v2/dungeons/") ?: return@withContext null
            val profiles = parseDungeonData(inputStream, name)
            profiles.profiles.values.find { it.selected }
        } catch (e: Exception) {
            modMessage("Error fetching dungeon profile data for $name: ${e.message}")
            null
        }
    }

    private suspend fun getProfileDataStream(name: String, url: String): InputStream? = withContext(Dispatchers.IO) {
        return@withContext try {
            val connection = setupHTTPConnection(URL(url + name))

            //503 is likely rate limit, though 429 seems to be the standard.
            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                connection.inputStream.also {
                    modMessage("Successfully fetched data for $name")
                }
            } else {
                modMessage("Failed to fetch data for $name: ${connection.responseMessage}")
                null
            }
        } catch (e: Exception) {
            modMessage("Error fetching data for $name: ${e.message}")
            null
        }
    }

    private fun setupHTTPConnection(url: URL): HttpURLConnection {
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.setRequestProperty("Accept", "application/json")
        connection.setRequestProperty("User-Agent", "Mozilla/5.0")
        return connection
    }

    suspend fun checkIfPlayerExists(name: String): Boolean = withContext(Dispatchers.IO) {
        val connection = setupHTTPConnection(URL("https://api.mojang.com/users/profiles/minecraft/$name"))
        return@withContext connection.responseCode == HttpURLConnection.HTTP_OK
    }

    fun logJsonToFile(jsonString: String, name: String = "unknown", type: String = "unknown") {
        val minecraftDir = File("config/hatecheaters")
        val logFolder = File(minecraftDir, "json_logs")
        if (!logFolder.exists()) {
            logFolder.mkdirs()
        }

        val logFile = File(logFolder, "${name}_${type}_${getCurrentDateTimeString()}.json")

        logFile.writeText(jsonString)
    }

    fun getCurrentDateTimeString(): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")
        val currentDateTime = LocalDateTime.now()
        return currentDateTime.format(formatter)
    }

    fun addToCache(name: String, profiles: FullProfiles) {
        val time = System.currentTimeMillis()
        if (parsedPlayerList.size >= 5) {
            parsedPlayerList.entries
                .sortedByDescending { time - it.value.second }
                .firstOrNull()?.key
                .let { parsedPlayerList.remove(it); modMessage("removed ${it} from cache list.") }
        }

        modMessage("added ${name} to cache list")
        parsedPlayerList[name] = Pair(profiles, time)
    }

}
