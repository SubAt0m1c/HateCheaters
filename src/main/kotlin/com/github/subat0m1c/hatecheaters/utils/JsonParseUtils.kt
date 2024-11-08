package com.github.subat0m1c.hatecheaters.utils

import com.github.subat0m1c.hatecheaters.utils.LogHandler.logJsonToFile
import com.github.subat0m1c.hatecheaters.utils.LogHandler.logger
import com.github.subat0m1c.hatecheaters.utils.WebUtils.apiServer
import com.github.subat0m1c.hatecheaters.utils.WebUtils.getInputStream
import com.github.subat0m1c.hatecheaters.utils.WebUtils.getUUIDbyName
import com.github.subat0m1c.hatecheaters.utils.jsonobjects.HypixelProfileData
import com.github.subat0m1c.hatecheaters.utils.jsonobjects.HypixelProfileData.PlayerInfo
import com.github.subat0m1c.hatecheaters.utils.jsonobjects.SkyCryptProfileData.skyCryptToHypixel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.InputStream

object JsonParseUtils {

    private val cachedPlayerData: MutableMap<String, Pair<PlayerInfo, Long>> = mutableMapOf()

    val json: Json = Json { ignoreUnknownKeys = true }

    private fun parseHypixelData(inputStream: InputStream, uuid: String, name: String): Result<PlayerInfo> {
        val jsonString = inputStream.bufferedReader().use { it.readText() }

        logJsonToFile(jsonString, name, "profile")

        val profileData: HypixelProfileData.ProfilesData = json.decodeFromString(jsonString)

        profileData.cause?.let { return Result.failure(FailedToGetHypixelException("Failed to get Hypixel Data: $it")) }
        return Result.success(PlayerInfo(profileData, uuid, name).also { logJsonToFile(json.encodeToString(HypixelProfileData.ProfilesData.serializer(), profileData), name, "parsed_profile") })
    }

    suspend fun getSkyblockProfile(playerName: String, profileId: String? = null, cache: Boolean = true, forceSkyCrypt: Boolean = false): Result<PlayerInfo> = withContext(Dispatchers.IO) {
        val data = getUUIDbyName(playerName).fold(onSuccess = { it }, onFailure = { return@withContext Result.failure(it) })
        val uuid = data.first ?: return@withContext Result.failure(FailedToGetUUIDException("Failed to get UUID."))
        val name = data.second ?: return@withContext Result.failure(FailedToGetUUIDException("Failed to get Name."))
        val profile = if (!forceSkyCrypt) getHypixelProfile(name, uuid, false, profileId, false).fold(
            onSuccess = { Result.success(it) },
            onFailure = { getSkyCryptProfile(name, profileId, uuid) }
        ) else getSkyCryptProfile(name, profileId, uuid)
        if (cache && profile.isSuccess) addToCache(profile.getOrThrow())
        return@withContext profile
    }

    private suspend fun getHypixelProfile(name: String, uuid: String, skipClientCache: Boolean = false, profileId: String? = null, forceSkyCrypt: Boolean = false): Result<PlayerInfo> = withContext(Dispatchers.IO) {
        return@withContext try {
            logger.info("Fetching data for $name...")
            val cachedPlayer =  cachedPlayerData[name.lowercase()]
            cachedPlayer?.second?.let {
                if (System.currentTimeMillis() - it >= 600000 || skipClientCache) {
                    logger.info("removed $name from cache because they haven't been cached in 10 minutes.")
                    cachedPlayerData.remove(name)
                    return@let
                }
                logger.info("Using cached data for $name")
                return@withContext Result.success(cachedPlayer.first)
            }

            if (forceSkyCrypt) return@withContext Result.failure<PlayerInfo>(FailedToGetHypixelException("Forced SkyCrypt!"))

            val inputStream = getInputStream(name, "$apiServer$uuid") ?: return@withContext Result.failure(FailedToGetHypixelException("Couldn't get Hypixel API input stream!"))
            parseHypixelData(inputStream, uuid, name)
        } catch (e: Exception) {
            //modMessage("Error fetching player profile data for $name: ${e.message}. Report this and include latest log found in config/hatecheaters/logs")
            logger.severe(e.stackTraceToString())
            Result.failure(e)
        }
    }

    private suspend fun getSkyCryptProfile(name: String, profileId: String?, uuid: String): Result<PlayerInfo> = withContext(Dispatchers.IO) {
        return@withContext try {
            val inputStream = getInputStream(name, "https://sky.shiiyu.moe/api/v2/profile/${name}") ?: return@withContext Result.failure(FailedToGetSkyCryptException("Could not get SkyCrypt input stream. ($name may not have joined hypixel!)"))
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            logJsonToFile(jsonString, name, "skycrypt_profile", "skycrypt_logs")

            val profileData = skyCryptToHypixel(json.decodeFromString(jsonString), name, uuid)
            logJsonToFile(json.encodeToString(HypixelProfileData.ProfilesData.serializer(), profileData.profileData), name, "parsed_skycrypt_profile", "skycrypt_logs")
            Result.success(profileData)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    private fun addToCache(profiles: PlayerInfo) {
        val time = System.currentTimeMillis()
        if (profiles.profileData.profiles.isEmpty()) return logger.info("Refusing to cache empty profile!")
        if (cachedPlayerData.size >= 5) {
            cachedPlayerData.entries
                .maxByOrNull { time - it.value.second }?.key
                .let { cachedPlayerData.remove(it); logger.info("Removed $it from cache list.") }
        }

        cachedPlayerData[profiles.name.lowercase()] = Pair(profiles, time)
        logger.info("Added ${profiles.name} to cache list. Cache size is now ${cachedPlayerData.size}/5.")
    }
}

class FailedToGetUUIDException(message: String) : Exception(message)
class FailedToGetHypixelException(message: String) : Exception(message)
class FailedToGetSkyCryptException(message: String) : Exception(message)