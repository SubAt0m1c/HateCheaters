package com.github.subat0m1c.hatecheaters.utils

import com.github.subat0m1c.hatecheaters.utils.LogHandler.logger
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
    private val queue = object {
        private val mutex = Mutex()

        suspend fun <T> queue(block: suspend () -> T): T = mutex.withLock {
            val result = block()
            delay(100)
            return result
        }
    }

    val players = listOf(
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
        getSkyblockProfile(players.random())
    }

    suspend fun getInputStream(url: String): Result<InputStream> = withContext(Dispatchers.IO) { queue.queue { runInputStream(url) } }

    private fun runInputStream(url: String): Result<InputStream> = runCatching {
        logger.info(url)
        val connection = setupHTTPConnection(URL(url))

        if (connection.responseCode == HttpURLConnection.HTTP_OK) {
            connection.inputStream.also {
                logger.info("Successfully fetched data for $url")
            }
        } else {
            val response = connection.responseMessage
            logger.warning("Failed to fetch data for $url: $response")
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
            logger.warning("Failed to get uuid for player $name")
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