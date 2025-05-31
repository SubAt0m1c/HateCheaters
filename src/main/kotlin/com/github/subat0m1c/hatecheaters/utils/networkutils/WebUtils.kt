package com.github.subat0m1c.hatecheaters.utils.networkutils

import com.github.subat0m1c.hatecheaters.HateCheaters
import com.github.subat0m1c.hatecheaters.utils.LogHandler.Logger
import com.github.subat0m1c.hatecheaters.utils.networkutils.SSLUtils.createSslContext
import com.github.subat0m1c.hatecheaters.utils.networkutils.SSLUtils.getTrustManager
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.*
import okio.IOException
import org.apache.http.impl.EnglishReasonPhraseCatalog
import java.io.InputStream
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object WebUtils {
    private const val USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36"

    suspend inline fun <reified T> streamAndRead(url: String, json: Json = HateCheaters.json): Result<T> = runCatching {
        return getInputStream(url).map {
            json.decodeFromString<T>(
                it.bufferedReader().use { reader -> reader.readText() })
        }
    }

    suspend fun getInputStream(url: String): Result<InputStream> =
        clientCall(
            Request.Builder().url(url).build()
        ).onFailure { e -> Logger.warning("Failed to get input stream. Error: ${e.message}") }

    suspend fun getUUIDbyName(name: String): Result<MojangData> =
        streamAndRead<MojangData>("https://api.mojang.com/users/profiles/minecraft/$name")

    private suspend fun clientCall(request: Request): Result<InputStream> = suspendCoroutine { cont ->
        client.newCall(request).enqueue(
            object : Callback {
                override fun onFailure(call: Call, e: IOException) = cont.resume(Result.failure(e))

                override fun onResponse(call: Call, response: Response) = try {
                    if (response.isSuccessful) response.body?.let { Result.success(it.byteStream()) } ?: Result.failure(
                        InputStreamException("Empty response body for ${request.url}")
                    )
                    else Result.failure(
                        InputStreamException(
                            "Failed to establish input stream for ${request.url}: ${
                                EnglishReasonPhraseCatalog.INSTANCE.getReason(
                                    response.code,
                                    null
                                )
                            }"
                        )
                    )
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