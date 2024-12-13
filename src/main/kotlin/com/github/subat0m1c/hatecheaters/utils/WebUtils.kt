package com.github.subat0m1c.hatecheaters.utils

import com.github.subat0m1c.hatecheaters.utils.ChatUtils.modMessage
import com.github.subat0m1c.hatecheaters.utils.LogHandler.Logger
import com.github.subat0m1c.hatecheaters.utils.apiutils.ParseUtils.getSkyblockProfile
import com.github.subat0m1c.hatecheaters.utils.apiutils.ParseUtils.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

object WebUtils {

    class Server(private val server: String) {
        fun getServer(endpoint: EndPoints, uuid: String): String = server + endpoint.append + uuid
        enum class EndPoints(val append: String) {
            SECRETS("secrets/"),
            PROFILE("get/"),
        }
    }

    object Queue {
        private val mutex = Mutex()

        suspend fun <T> queue(block: suspend () -> T): T = mutex.withLock {
            val result = block()
            delay(100)
            return result
        }
    }

    private val players = listOf(
        "SubAt0mic",
        "15h",
        "nurgl",
        "grassbl0ck",
        "moistcheecks",
        "ensyl",
        "deathstreeks",
        "kasji",
        "eatplastic",
        "xmin_terminator"
    )

    suspend fun testQue(): Unit = withContext(Dispatchers.IO) {
        getSkyblockProfile(players.random()).fold(
            onSuccess = { modMessage("Successfully got data for ${it.name}") },
            onFailure = { modMessage(it.message) }
        )
    }

    suspend fun getInputStream(url: String): Result<InputStream> = withContext(Dispatchers.IO) { Queue.queue { runInputStream(url) } }

    private fun runInputStream(url: String): Result<InputStream> = runCatching {
        Logger.info(url)
        val connection = setupHTTPConnection(URL(url))

        if (connection.responseCode == HttpURLConnection.HTTP_OK) {
            return@runCatching connection.inputStream.also { Logger.info("Successfully fetched data for $url") }
        } else {
            val response = connection.responseMessage
            Logger.warning("Failed to fetch data for $url: $response")
            return Result.failure(InputStreamException("Failed to establish input stream for $url: $response"))
        }
    }

    private const val USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36"

    private fun setupHTTPConnection(url: URL) = (url.openConnection() as HttpURLConnection).apply {
        requestMethod = "GET"
        readTimeout = 10000
        connectTimeout = 5000
        setRequestProperty("Accept", "application/json")
        setRequestProperty("User-Agent", USER_AGENT)
    }

    fun getUUIDbyName(name: String): Result<MojangData> = runCatching {
        val connection = setupHTTPConnection(URL("https://api.mojang.com/users/profiles/minecraft/$name"))

        if (connection.responseCode == HttpURLConnection.HTTP_OK) {
            json.decodeFromString(connection.inputStream.bufferedReader().use {it.readText()})
        } else {
            Logger.warning("Failed to get uuid for player $name")
            return Result.failure(FailedToGetUUIDException("Failed to get uuid. ($name may not exist!) Error: ${connection.responseMessage}"))
        }
    }

    @Serializable
    data class MojangData(
        val name: String,
        @SerialName("id")
        val uuid: String
    )

    class FailedToGetUUIDException(message: String) : Exception(message)
    class InputStreamException(message: String) : Exception(message)
}