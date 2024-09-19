package com.github.subat0m1c.hatecheaters.utils

import com.github.subat0m1c.hatecheaters.utils.ChatUtils.modMessage
import com.github.subat0m1c.hatecheaters.utils.LogHandler.logJsonToFile
import com.github.subat0m1c.hatecheaters.utils.LogHandler.logger
import com.github.subat0m1c.hatecheaters.utils.WebUtils.apiServer
import com.github.subat0m1c.hatecheaters.utils.WebUtils.getInputStream
import com.github.subat0m1c.hatecheaters.utils.WebUtils.getUUIDbyName
import com.github.subat0m1c.hatecheaters.utils.jsonobjects.HypixelProfileData
import com.github.subat0m1c.hatecheaters.utils.jsonobjects.HypixelProfileData.PlayerInfo
import com.github.subat0m1c.hatecheaters.utils.jsonobjects.SkyCryptProfileData
import com.github.subat0m1c.hatecheaters.utils.jsonobjects.SkyCryptProfileData.skyCryptToHypixel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.InputStream

object JsonParseUtils {

    private val cachedPlayerData: MutableMap<String, Pair<PlayerInfo, Long>> = mutableMapOf()

    val json: Json = Json { ignoreUnknownKeys = true }

    private fun parseHypixelData(inputStream: InputStream, uuid: String, name: String): PlayerInfo {
        val jsonString = inputStream.bufferedReader().use { it.readText() }

        logJsonToFile(jsonString, name, "profile")

        val profileData: HypixelProfileData.ProfilesData = json.decodeFromString(jsonString)

        profileData.cause?.let { throw FailedToGetHypixelException("Failed to get Hypixel Data: $it") }
        return PlayerInfo(profileData, uuid, name).also { logJsonToFile(json.encodeToString(HypixelProfileData.ProfilesData.serializer(), profileData), name, "parsed_profile") }
    }

    suspend fun getSkyblockProfile(name: String, skipClientCache: Boolean = false, profileId: String? = null, forceSkyCrypt: Boolean = false): PlayerInfo? = withContext(Dispatchers.IO) {
        return@withContext try {
            logger.info("Fetching data for $name...")
            val cachedPlayer =  cachedPlayerData[name.lowercase()]
            cachedPlayer?.second?.let {
                if (System.currentTimeMillis() - it >= 600000 || skipClientCache) {
                    logger.info("removed $name from cache because they haven't been cached in 10 minutes.")
                    cachedPlayerData.remove(name)
                    return@let null
                }
                logger.info("Using cached data for $name")
                return@withContext cachedPlayer.first
            }

            if (forceSkyCrypt) throw FailedToGetHypixelException("Forced SkyCrypt!")

            val uuid = getUUIDbyName(name) ?: throw FailedToGetHypixelException("Couldn't get UUID!")
            val inputStream = getInputStream(name, "$apiServer$uuid") ?: throw FailedToGetHypixelException("Couldn't get Hypixel API input stream!")
            val profiles = parseHypixelData(inputStream, uuid, name)
            return@withContext profiles.also { addToCache(name, profiles) }
        } catch (e: FailedToGetHypixelException) {
            logger.warning("Fetching data from SkyCrypt for $name... Error: ${e.message}")
            return@withContext getSkyCryptProfile(name, profileId)
        } catch (e: Exception) {
            modMessage("Error fetching player profile data for $name: ${e.message}. Report this and include latest log found in config/hatecheaters/logs")
            logger.severe(e.stackTraceToString())
            null
        }
    }

    private suspend fun getSkyCryptProfile(name: String, profileId: String?): PlayerInfo = withContext(Dispatchers.IO) {
        val inputStream = getInputStream(name, "https://sky.shiiyu.moe/api/v2/profile/${name}") ?: throw FailedToGetSkyCryptException("Couldn't get SkyCrypt API input stream!")
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        logJsonToFile(jsonString, name, "skycrypt_profile")

        val profiles: SkyCryptProfileData.SkyCryptProfiles = json.decodeFromString(jsonString)
        val profileData = skyCryptToHypixel(profiles, name, name)
        addToCache(name, profileData)
        return@withContext profileData.also { logJsonToFile(json.encodeToString(HypixelProfileData.ProfilesData.serializer(), it.profileData), name, "parsed_skycrypt_profile", "skycrypt_logs") }
    }


    private fun addToCache(name: String, profiles: PlayerInfo) {
        val time = System.currentTimeMillis()
        if (profiles.profileData.profiles.isEmpty()) return logger.info("Refusing to cache empty profile!")
        if (cachedPlayerData.size >= 5) {
            cachedPlayerData.entries
                .maxByOrNull { time - it.value.second }?.key
                .let { cachedPlayerData.remove(it); logger.info("Removed $it from cache list.") }
        }

        cachedPlayerData[name.lowercase()] = Pair(profiles, time)
        logger.info("Added $name to cache list. Cache size is now ${cachedPlayerData.size}/5.")
    }
}

class FailedToGetHypixelException(message: String) : Exception(message)
class FailedToGetSkyCryptException(message: String) : Exception(message)