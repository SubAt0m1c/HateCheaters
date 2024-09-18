package com.github.subat0m1c.hatecheaters.utils

import com.github.subat0m1c.hatecheaters.utils.ChatUtils.modMessage
import com.github.subat0m1c.hatecheaters.utils.JsonParseUtils.json
import com.github.subat0m1c.hatecheaters.utils.jsonobjects.HypixelApiStats
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

object WebUtils {

    suspend fun getInputStream(data: String, url: String): InputStream? = withContext(Dispatchers.IO) {
        return@withContext try {
            val connection = setupHTTPConnection(URL(url))

            modMessage(connection.contentType)
            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                connection.inputStream.also {
                    modMessage("Successfully fetched data for $data")
                }
            } else {
                modMessage("Failed to fetch data for $data: ${connection.responseMessage}")
                null
            }
        } catch (e: Exception) {
            modMessage("Error fetching data for $data: ${e.message}")
            null
        }
    }

    private fun setupHTTPConnection(url: URL): HttpURLConnection {
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.setRequestProperty("Accept", "application/json")
        connection.setRequestProperty("User-Agent", "Mozilla/5.0")
        return connection
    }

    suspend fun getUUIDbyName(name: String): String? = withContext(Dispatchers.IO) {
        val connection = setupHTTPConnection(URL("https://api.mojang.com/users/profiles/minecraft/$name"))

        try {
            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val data: Map<String, String> = json.decodeFromString(connection.inputStream.bufferedReader().use {it.readText()})
                return@withContext data["id"]
            } else {
                modMessage("Failed to get uuid for player $name")
            }
        } catch (e: Exception) {
            modMessage("Error fetching uuid for player $name")
        }

        return@withContext null
    }
}