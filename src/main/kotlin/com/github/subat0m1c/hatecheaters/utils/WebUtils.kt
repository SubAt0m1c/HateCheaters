package com.github.subat0m1c.hatecheaters.utils

import com.github.subat0m1c.hatecheaters.utils.ChatUtils.modMessage
import com.github.subat0m1c.hatecheaters.utils.LogHandler.Logger
import com.github.subat0m1c.hatecheaters.utils.SSLUtils.createSslContext
import com.github.subat0m1c.hatecheaters.utils.SSLUtils.getTrustManager
import com.github.subat0m1c.hatecheaters.utils.apiutils.ParseUtils.getSkyblockProfile
import com.github.subat0m1c.hatecheaters.utils.apiutils.ParseUtils.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import okhttp3.*
import okio.IOException
import java.io.InputStream
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object WebUtils {

    class Server(private val server: String) {
        fun getServer(endpoint: EndPoints, uuid: String): String = server + endpoint.append + uuid
        enum class EndPoints(val append: String) {
            SECRETS("secrets/"),
            PROFILE("get/"),
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

    suspend fun getInputStream(url: String): Result<InputStream> {
        Logger.info(url)
        val request = Request.Builder().url(url).build()

        clientCall(request).fold(
            onSuccess = { return Result.success(it) },
            onFailure = { e -> return Result.failure<InputStream>(e).also { Logger.warning("Failed to get input stream. Error: ${e.message}") } }
        )
    }

    private const val USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36"

    suspend fun getUUIDbyName(name: String): Result<MojangData> {
        val request = Request.Builder()
            .url("https://api.mojang.com/users/profiles/minecraft/$name")
            .build()

        clientCall(request).fold(
            onSuccess = { return Result.success(json.decodeFromString<MojangData>(it.bufferedReader().use { it.readText() })) },
            onFailure = { e -> return Result.failure<MojangData>(e).also { Logger.warning("Failed to get uuid stream. Error: ${e.message}") } }
        )
    }

    private suspend fun clientCall(request: Request): Result<InputStream> = suspendCoroutine { cont ->
        client.newCall(request).enqueue(
            object : Callback {
                override fun onFailure(call: Call, e: IOException) = cont.resume(Result.failure(e))

                override fun onResponse(call: Call, response: Response) = try {
                    if (response.isSuccessful) {
                        response.body?.let {
                            Result.success(it.byteStream())
                        } ?: Result.failure(InputStreamException("Failed to establish input stream for ${request.url}: ${response.message}"))
                    } else {
                        Result.failure(InputStreamException("Failed to establish input stream for ${request.url}: ${response.message}"))
                    }
                } catch (e: Exception) {
                    Result.failure(e)
                }.let { cont.resume(it) }
            }
        )

    }

    val client = OkHttpClient.Builder().apply {
        sslSocketFactory(createSslContext().socketFactory, getTrustManager())

        dispatcher(Dispatcher().apply {
            maxRequests = 1
            maxRequestsPerHost = 1
        })

        readTimeout(10, TimeUnit.SECONDS)
        connectTimeout(5, TimeUnit.SECONDS)
        writeTimeout(10, TimeUnit.SECONDS)

        addInterceptor { chain ->
            chain.request().newBuilder()
                .header("Accept", "application/json")
                .header("User-Agent", USER_AGENT)
                .build()
                .let { chain.proceed(it) }
        }
    }.build()

    @Serializable
    data class MojangData(
        val name: String,
        @SerialName("id")
        val uuid: String
    )

    class InputStreamException(message: String) : Exception(message)
}