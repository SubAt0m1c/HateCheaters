package com.github.subat0m1c.hatecheaters.utils

import com.github.subat0m1c.hatecheaters.modules.HateCheaters
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.modMessage
import com.github.subat0m1c.hatecheaters.utils.WebUtils.getInputStream
import com.github.subat0m1c.hatecheaters.utils.WebUtils.getUUIDbyName
import com.github.subat0m1c.hatecheaters.utils.jsonobjects.HypixelApiStats
import com.github.subat0m1c.hatecheaters.utils.jsonobjects.HypixelApiStats.PlayerInfo
import com.github.subat0m1c.hatecheaters.utils.jsonobjects.HypixelApiStats.ProfilesData as HypixelProfiles
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.File
import java.io.InputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object JsonParseUtils {

    inline val server: String get() = if (HateCheaters.server == "default") "http://subat0mic.duckdns.org/rawProfile?uuid=" else HateCheaters.server

    //val parsedPlayerList: MutableMap<String, Pair<FullProfiles, Long>> = mutableMapOf()

    val parsedHypixelList: MutableMap<String, Triple<HypixelProfiles, Long, String>> = mutableMapOf()

    val json: Json = Json { ignoreUnknownKeys = true }

    private fun parseHypixelData(inputStream: InputStream, name: String): HypixelProfiles {
        val jsonString = inputStream.bufferedReader().use { it.readText() }

        logJsonToFile(jsonString, name, "profile")

        val profileData: HypixelApiStats.ProfilesData = json.decodeFromString(jsonString)

        return profileData.also { logJsonToFile(json.encodeToString(HypixelApiStats.ProfilesData.serializer(), profileData), name, "parsed_profile") }
    }

    suspend fun getHypixelSkyblockProfile(name: String, skipClientCache: Boolean = false, profileId: String? = null): PlayerInfo? = withContext(Dispatchers.IO) {
        return@withContext try {
            modMessage("Fetching data for $name")
            val cachedPlayer =  parsedHypixelList[name.lowercase()]
            cachedPlayer?.second?.let {
                if (System.currentTimeMillis() - it >= 600000 || skipClientCache) {
                    modMessage("removed $name from cache because they haven't been cached in 10 minutes.")
                    parsedHypixelList.remove(name)
                    return@let null
                }
                val parsedProfiles = cachedPlayer.first
                modMessage("Using cached data for $name")
                return@withContext PlayerInfo(parsedProfiles, cachedPlayer.third, name)
            }

            val uuid = getUUIDbyName(name) ?: return@withContext null
            val inputStream = getInputStream(name, "$server$uuid") ?: return@withContext null
            val profiles = parseHypixelData(inputStream, name)
            addToHypixelCache(name, profiles, uuid)
            return@withContext PlayerInfo(profiles, uuid, name)
        } catch (e: Exception) {
            modMessage("Error fetching player profile data for $name: ${e.message}")
            null
        }
    }

    private fun logJsonToFile(jsonString: String, name: String = "unknown", type: String = "unknown") {
        if (!HateCheaters.enabled || !HateCheaters.logJson) return
        val minecraftDir = File("config/hatecheaters")
        val logFolder = File(minecraftDir, "json_logs")
        if (!logFolder.exists()) {
            logFolder.mkdirs()
        }

        val logFile = File(logFolder, "${name}_${type}_${getCurrentDateTimeString()}.json")

        logFile.writeText(jsonString)
    }

    private fun getCurrentDateTimeString(): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")
        val currentDateTime = LocalDateTime.now()
        return currentDateTime.format(formatter)
    }

    private fun addToHypixelCache(name: String, profiles: HypixelProfiles, uuid: String) {
        val time = System.currentTimeMillis()
        if (parsedHypixelList.size >= 5) {
            parsedHypixelList.entries
                .maxByOrNull { time - it.value.second }?.key
                .let { parsedHypixelList.remove(it); modMessage("Removed $it from cache list.") }
        }

        parsedHypixelList[name.lowercase()] = Triple(profiles, time, uuid)
        modMessage("Added $name to cache list. Cache size is now ${parsedHypixelList.size}/5.")
    }

    /**private fun parseDungeonData(inputStream: InputStream, name: String = "unknown", profileId: String? = null): DungeonProfiles {
        val jsonString = inputStream.bufferedReader().use { it.readText() }

        logJsonToFile(jsonString, name, "Dungeons")

        val profilesData: DungeonProfiles = json.decodeFromString(jsonString)

        json.encodeToJsonElement(DungeonProfiles.serializer(), profilesData)

        return profilesData.also { logJsonToFile(json.encodeToString(DungeonProfiles.serializer(), profilesData), name, "parsed_dungeons") }
    }*/

    /**private fun parsePlayerData(inputStream: InputStream, name: String = "unknown"): FullProfiles {
        val jsonString = inputStream.bufferedReader().use { it.readText() }

        logJsonToFile(jsonString, name, "Profiles")

        val profilesData: FullProfiles = json.decodeFromString(jsonString)

        return profilesData.also { logJsonToFile(json.encodeToString(FullProfiles.serializer(), profilesData), name, "parsed_full_profile") }
    }*/

    /**suspend fun getPlayerProfile(name: String, skipCache: Boolean = false, profileId: String? = null): FullProfile? = withContext(Dispatchers.IO) {
        return@withContext try {
            parsedPlayerList[name]?.second?.let {
                if (System.currentTimeMillis() - it >= 600000 || skipCache) {
                    modMessage("removed $name from cache because they haven't been cached in 10 minutes.")
                    parsedPlayerList.remove(name)
                    return@let null
                }
                modMessage("Using cached data for $name")
                val parsedProfiles = parsedPlayerList[name]?.first
                return@withContext profileId?.let { parsedProfiles?.profiles?.get(it) } ?: parsedProfiles?.profiles?.values?.find { it.current }
            }

            modMessage("Fetching player profile data for $name...")
            val inputStream = getInputStream(name, "https://sky.shiiyu.moe/api/v2/profile/$name") ?: return@withContext null
            val profiles = parsePlayerData(inputStream, name)
            addToCache(name, profiles)
            return@withContext profileId?.let { profiles.profiles[it] } ?: profiles.profiles.values.find { it.current }
        } catch (e: Exception) {
            modMessage("Error fetching player profile data for $name: ${e.message}")
            null
        }
    }*/

    /**suspend fun getDungeonProfile(name: String, profileId: String? = null): DungeonProfile? = withContext(Dispatchers.IO) {
        return@withContext try {
            modMessage("Fetching dungeon profile data for $name...")
            val inputStream = getInputStream(name, "https://sky.shiiyu.moe/api/v2/dungeons/$name") ?: return@withContext null
            val profiles = parseDungeonData(inputStream, name)
            return@withContext profileId?.let { profiles.profiles[it] } ?: profiles.profiles.values.find { it.selected }
        } catch (e: Exception) {
            modMessage("Error fetching dungeon profile data for $name: ${e.message}")
            null
        }
    }*/

    /**private fun addToCache(name: String, profiles: FullProfiles) {
        val time = System.currentTimeMillis()
        if (parsedPlayerList.size >= 5) {
            parsedPlayerList.entries
                .maxByOrNull { time - it.value.second }?.key
                .let { parsedPlayerList.remove(it); modMessage("Removed $it from cache list.") }
        }

        parsedPlayerList[name] = Pair(profiles, time)
        modMessage("Added $name to cache list. Cache size is now ${parsedPlayerList.size}/5.")
    }*/
}
