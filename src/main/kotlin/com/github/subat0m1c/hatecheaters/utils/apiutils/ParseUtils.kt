package com.github.subat0m1c.hatecheaters.utils.apiutils

import com.github.subat0m1c.hatecheaters.modules.HateCheatersModule
import com.github.subat0m1c.hatecheaters.utils.LogHandler.logJsonToFile
import com.github.subat0m1c.hatecheaters.utils.LogHandler.logger
import com.github.subat0m1c.hatecheaters.utils.WebUtils.getInputStream
import com.github.subat0m1c.hatecheaters.utils.WebUtils.getUUIDbyName
import com.github.subat0m1c.hatecheaters.utils.apiutils.HypixelData.PlayerInfo
import com.github.subat0m1c.hatecheaters.utils.apiutils.SkyCryptData.skyCryptToHypixel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

object ParseUtils {
    private val apiServer: String get() = if (HateCheatersModule.server == "default") "http://subat0mic.duckdns.org/get/" else HateCheatersModule.server
    private val cachedPlayerData: MutableMap<String, Pair<PlayerInfo, Long>> = mutableMapOf()
    val json: Json = Json { ignoreUnknownKeys = true }

    suspend fun getSkyblockProfile(playerName: String, cache: Boolean = true, forceSkyCrypt: Boolean = false): Result<PlayerInfo> = withContext(Dispatchers.IO) {
        cachedPlayerData[playerName.lowercase()]?.takeUnless { (System.currentTimeMillis() - it.second >= 600000) }?.first?.let { return@withContext Result.success(it) }

        val (name, uuid) = getUUIDbyName(playerName).getOrElse { return@withContext Result.failure(it) }
        logger.info("Fetching data for $name... (UUID: $uuid)")
        val profile = if (!forceSkyCrypt) getHypixelProfile(name, uuid).fold(
            onSuccess = { Result.success(it) },
            onFailure = { getSkyCryptProfile(name, uuid) }
        ) else getSkyCryptProfile(name, uuid)

        profile.onSuccess { if (cache) addToCache(it) }
    }

    private suspend fun getHypixelProfile(name: String, uuid: String): Result<PlayerInfo> = runCatching {
        val inputStream = getInputStream("$apiServer$uuid").getOrElse { return Result.failure(it) }
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        logJsonToFile(jsonString, name, "profile")

        val profileData: HypixelData.ProfilesData = json.decodeFromString(jsonString)
        profileData.cause?.let { return Result.failure(FailedToGetHypixelException("Failed to get Hypixel Data: $it")) }
        logJsonToFile(json.encodeToString(HypixelData.ProfilesData.serializer(), profileData), name, "parsed_profile")
        PlayerInfo(profileData, uuid, name)
    }

    private suspend fun getSkyCryptProfile(name: String, uuid: String): Result<PlayerInfo> = runCatching {
        val inputStream = getInputStream("https://sky.shiiyu.moe/api/v2/profile/${name}").getOrElse {
            return Result.failure(FailedToGetSkyCryptException("$name may not have joined hypixel! Error: ${it.message}"))
        }

        val jsonString = inputStream.bufferedReader().use { it.readText() }
        logJsonToFile(jsonString, name, "skycrypt_profile", "skycrypt_logs")

        val profileData = skyCryptToHypixel(json.decodeFromString(jsonString), name, uuid)
        logJsonToFile(json.encodeToString(HypixelData.ProfilesData.serializer(), profileData.profileData), name, "parsed_skycrypt_profile", "skycrypt_logs")
        profileData
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

    class FailedToGetHypixelException(message: String) : Exception(message)
    class FailedToGetSkyCryptException(message: String) : Exception(message)
}