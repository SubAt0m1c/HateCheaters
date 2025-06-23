package com.github.subat0m1c.hatecheaters.utils.apiutils

import com.github.subat0m1c.hatecheaters.modules.skyblock.HateCheatersModule
import com.github.subat0m1c.hatecheaters.utils.LogHandler.Logger
import com.github.subat0m1c.hatecheaters.utils.apiutils.HypixelData.PlayerInfo
import com.github.subat0m1c.hatecheaters.utils.apiutils.HypixelData.ProfilesData
import com.github.subat0m1c.hatecheaters.utils.networkutils.WebUtils
import com.github.subat0m1c.hatecheaters.utils.networkutils.WebUtils.getUUIDbyName
import com.github.subat0m1c.hatecheaters.utils.networkutils.WebUtils.streamAndRead
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object HypixelApi {
    private val url =
        if (HateCheatersModule.server == "default") "http://127.0.0.1:8000/" else "https://${HateCheatersModule.server}/"

    private fun getServer(endpoint: EndPoint, uuid: String): String = url + endpoint.name.lowercase() + "/" + uuid

    private val cachedPlayerData: MutableMap<String, Pair<PlayerInfo, Long>> = mutableMapOf()
    val cachedPlayers get() = cachedPlayerData.keys

    suspend fun getSecrets(playerName: String, uuid: String? = null): Result<Long> = getSecretData(
        uuid ?: getUUIDbyName(playerName).getOrElse { e -> return Result.failure(e) }.uuid
    )

    suspend fun getProfile(playerName: String): Result<PlayerInfo> = withContext(Dispatchers.IO) {
        getFromCache(playerName)?.let { cached -> return@withContext Result.success(cached) }
        getUUIDbyName(playerName).fold(
            { getProfileData(it).onSuccess { data -> addToCache(data) } },
            { e -> Result.failure(e) })
    }

    private suspend fun getSecretData(uuid: String): Result<Long> =
        streamAndRead<Long>(getServer(EndPoint.SECRETS, uuid))

    private suspend fun getProfileData(playerData: WebUtils.MojangData): Result<PlayerInfo> =
        streamAndRead<ProfilesData>(getServer(EndPoint.GET, playerData.uuid)).map {
            it.failed?.let { failure -> return Result.failure(FailedToGetHypixelException("Failed to get Hypixel Data: $failure")) }
            PlayerInfo(it, playerData.uuid, playerData.name)
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

    private fun getFromCache(name: String, time: Long = 600000) =
        cachedPlayerData[name.lowercase()]?.takeUnless { (System.currentTimeMillis() - it.second >= time) }?.first

    enum class EndPoint { SECRETS, GET }
    class FailedToGetHypixelException(message: String) : Exception(message)
}