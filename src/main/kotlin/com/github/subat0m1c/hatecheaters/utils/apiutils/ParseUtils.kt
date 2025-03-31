package com.github.subat0m1c.hatecheaters.utils.apiutils

import com.github.subat0m1c.hatecheaters.modules.HateCheatersModule
import com.github.subat0m1c.hatecheaters.utils.LogHandler.logJsonToFile
import com.github.subat0m1c.hatecheaters.utils.LogHandler.Logger
import com.github.subat0m1c.hatecheaters.utils.WebUtils.Server
import com.github.subat0m1c.hatecheaters.utils.WebUtils.getInputStream
import com.github.subat0m1c.hatecheaters.utils.WebUtils.getUUIDbyName
import com.github.subat0m1c.hatecheaters.utils.apiutils.HypixelData.PlayerInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

object ParseUtils {
    private val apiServer: Server get() = if (HateCheatersModule.server == "default") Server("https://subat0mic.click/") else Server("https://${HateCheatersModule.server}/")
    private val cachedPlayerData: MutableMap<String, Pair<PlayerInfo, Long>> = mutableMapOf()
    val json: Json = Json { ignoreUnknownKeys = true }

    suspend fun getSecrets(playerName: String, uuid: String? = null): Result<Long> = withContext(Dispatchers.IO) {
        getSecretData(
            uuid ?: getUUIDbyName(playerName).getOrElse { return@withContext Result.failure(it) }.uuid
        ).onSuccess { Logger.info("Received data $it.") }
    }

    suspend fun getSkyblockProfile(playerName: String, cache: Boolean = true): Result<PlayerInfo> = withContext(Dispatchers.IO) {
        getFromCache(playerName)?.let { return@withContext Result.success(it) }

        val (name, uuid) = getUUIDbyName(playerName).getOrElse { return@withContext Result.failure(it) }
        Logger.info("Fetching data for $name... (UUID: $uuid)")

        getHypixelData(name, uuid)
    }.onSuccess { if (cache) addToCache(it) }

    private suspend fun getSecretData(uuid: String): Result<Long> = runCatching {
        val inputStream = getInputStream(apiServer.getServer(Server.EndPoints.SECRETS, uuid)).getOrElse { return Result.failure(it) }
        json.decodeFromString(inputStream.bufferedReader().use { it.readText() })
    }

    private suspend fun getHypixelData(name: String, uuid: String): Result<PlayerInfo> = runCatching {
        val inputStream = getInputStream(apiServer.getServer(Server.EndPoints.PROFILE, uuid)).getOrElse { return Result.failure(it) }
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        logJsonToFile(jsonString, name, "profile")

        val profileData: HypixelData.ProfilesData = json.decodeFromString(jsonString)
        profileData.cause?.let { return Result.failure(FailedToGetHypixelException("Failed to get Hypixel Data: $it")) }
        logJsonToFile(json.encodeToString(HypixelData.ProfilesData.serializer(), profileData), name, "parsed_profile")
        PlayerInfo(profileData, uuid, name)
    }

    private fun addToCache(profiles: PlayerInfo) {
        val time = System.currentTimeMillis()
        if (profiles.profileData.profiles.isEmpty()) return Logger.info("Refusing to cache empty profile!")
        cachedPlayerData.entries
            .takeIf { it.size >= 5 }
            ?.maxByOrNull { time - it.value.second }?.key
            ?.let { cachedPlayerData.remove(it); Logger.info("Removed $it from cache list.") }

        cachedPlayerData[profiles.name.lowercase()] = Pair(profiles, time)
        Logger.info("Added ${profiles.name} to cache list. Cache size is now ${cachedPlayerData.size}/5.")
    }

    private fun getFromCache(name: String, time: Long = 600000) = cachedPlayerData[name.lowercase()]?.takeUnless { (System.currentTimeMillis() - it.second >= time) }?.first

    class FailedToGetHypixelException(message: String) : Exception(message)
}