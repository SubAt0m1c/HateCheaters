package com.github.subat0m1c.hatecheaters.utils

import com.github.subat0m1c.hatecheaters.modules.HateCheaters
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.modMessage
import com.github.subat0m1c.hatecheaters.utils.WebUtils.getInputStream
import com.github.subat0m1c.hatecheaters.utils.WebUtils.getUUIDbyName
import com.github.subat0m1c.hatecheaters.utils.jsonobjects.HypixelApiStats
import com.github.subat0m1c.hatecheaters.utils.jsonobjects.HypixelApiStats.PlayerInfo
import com.github.subat0m1c.hatecheaters.utils.jsonobjects.skycrypt.ProfileData
import com.github.subat0m1c.hatecheaters.utils.jsonobjects.skycrypt.ProfileData.skyCryptToHypixel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.File
import java.io.InputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object JsonParseUtils {

    private inline val server: String get() = if (HateCheaters.server == "default") "http://subat0mic.duckdns.org/rawProfile?uuid=" else HateCheaters.server

    private val cachedPlayerData: MutableMap<String, Pair<PlayerInfo, Long>> = mutableMapOf()

    val json: Json = Json { ignoreUnknownKeys = true }

    private fun parseHypixelData(inputStream: InputStream, uuid: String, name: String): PlayerInfo {
        val jsonString = inputStream.bufferedReader().use { it.readText() }

        logJsonToFile(jsonString, name, "profile")

        val profileData: HypixelApiStats.ProfilesData = json.decodeFromString(jsonString)

        profileData.cause?.let { throw FailedToGetHypixelException("Failed to get Hypixel Data: $it") }
        return PlayerInfo(profileData, uuid, name).also { logJsonToFile(json.encodeToString(HypixelApiStats.ProfilesData.serializer(), profileData), name, "parsed_profile") }
    }

    suspend fun getSkyblockProfile(name: String, skipClientCache: Boolean = false, profileId: String? = null, forceSkyCrypt: Boolean = false): PlayerInfo? = withContext(Dispatchers.IO) {
        return@withContext try {
            modMessage("Fetching data for $name...")
            val cachedPlayer =  cachedPlayerData[name.lowercase()]
            cachedPlayer?.second?.let {
                if (System.currentTimeMillis() - it >= 600000 || skipClientCache) {
                    modMessage("removed $name from cache because they haven't been cached in 10 minutes.")
                    cachedPlayerData.remove(name)
                    return@let null
                }
                modMessage("Using cached data for $name")
                return@withContext cachedPlayer.first
            }

            if (forceSkyCrypt) throw FailedToGetHypixelException("Forced SkyCrypt!")

            val uuid = getUUIDbyName(name) ?: throw FailedToGetHypixelException("Couldn't get UUID!")
            val inputStream = getInputStream(name, "$server$uuid") ?: throw FailedToGetHypixelException("Couldn't get Hypixel API input stream!")
            val profiles = parseHypixelData(inputStream, uuid, name)
            return@withContext profiles.also { addToCache(name, profiles) }
        } catch (e: FailedToGetHypixelException) {
            modMessage("Fetching data from SkyCrypt for $name... Error: ${e.message}")
            return@withContext getSkyCryptProfile(name, profileId)
        } catch (e: Exception) {
            modMessage("Error fetching player profile data for $name: ${e.message}")
            null
        }
    }

    private suspend fun getSkyCryptProfile(name: String, profileId: String?): PlayerInfo = withContext(Dispatchers.IO) {
        val inputStream = getInputStream(name, "https://sky.shiiyu.moe/api/v2/profile/${name}") ?: throw FailedToGetSkyCryptException("Couldn't get SkyCrypt API input stream!")
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        logJsonToFile(jsonString, name, "skycrypt_profile")

        val profiles: ProfileData.Profiles = json.decodeFromString(jsonString)
        val profileData = skyCryptToHypixel(profiles, name, name)
        addToCache(name, profileData)
        return@withContext profileData.also { logJsonToFile(json.encodeToString(HypixelApiStats.ProfilesData.serializer(), it.profileData), name, "parsed_skycrypt_profile", "skycrypt_logs") }
    }

    private fun logJsonToFile(jsonString: String, name: String = "unknown", type: String = "unknown", dir: String = "hypixel_logs") {
        if (!HateCheaters.enabled || !HateCheaters.logJson) return
        val minecraftDir = File("config/hatecheaters")
        val logFolder = File(minecraftDir, dir)
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

    private fun addToCache(name: String, profiles: PlayerInfo) {
        val time = System.currentTimeMillis()
        if (profiles.profileData.profiles.isEmpty()) return modMessage("Refusing to cache empty profile!")
        if (cachedPlayerData.size >= 5) {
            cachedPlayerData.entries
                .maxByOrNull { time - it.value.second }?.key
                .let { cachedPlayerData.remove(it); modMessage("Removed $it from cache list.") }
        }

        cachedPlayerData[name.lowercase()] = Pair(profiles, time)
        modMessage("Added $name to cache list. Cache size is now ${cachedPlayerData.size}/5.")
    }
}

class FailedToGetHypixelException(message: String) : Exception(message)
class FailedToGetSkyCryptException(message: String) : Exception(message)