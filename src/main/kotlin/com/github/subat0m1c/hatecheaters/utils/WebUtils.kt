package com.github.subat0m1c.hatecheaters.utils

import com.github.subat0m1c.hatecheaters.utils.ChatUtils.modMessage
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

object WebUtils {
    suspend fun getCurrentProfile(name: String): DungeonData.Profile? {
        try {
            val jsonObject = getDungeonsData(name) ?: return null
            val profiles = DungeonData.parseData(jsonObject)
            return profiles.profiles.values.find { it.selected }
        } catch (e: Exception) {
            modMessage("Error fetching data: ${e.message}")
            return null
        }
    }

    private suspend fun getDungeonsData(name: String): JsonObject? = withContext(Dispatchers.IO) {
        modMessage("Getting data for ${name}...")
        val url = URL("https://sky.shiiyu.moe/api/v2/dungeons/${name}")
        val connection: HttpURLConnection = url.openConnection() as HttpURLConnection

        connection.setRequestProperty("Accept", "application/json")
        connection.setRequestProperty("User-Agent", "Mozilla/5.0")

        val response = try {
            if (connection.responseCode in 200..299) {
                connection.inputStream.bufferedReader().use { it.readText() }
            } else {
                modMessage("API Error: ${connection.responseCode}")
                return@withContext null
            }
        } catch (e: Exception) {
            modMessage("Error reading data: ${e.message}")
            return@withContext null
        }

        val jsonObject = try {
            JsonParser().parse(response).asJsonObject
        } catch (e: Exception) {
            modMessage("Error parsing JSON: ${e.message}")
            return@withContext null
        }

        jsonObject
    }

}