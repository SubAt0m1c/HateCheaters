package com.github.subat0m1c.hatecheaters.utils

import com.github.subat0m1c.hatecheaters.modules.HateCheatersModule
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.getCurrentDateTimeString
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.io.File

object LogHandler {
    lateinit var logger: Logger

    fun init() {
        val configFolder = File("config/hatecheaters/modlogs")
        if (!configFolder.exists()) {
            configFolder.mkdirs()
        }

        logger = LogManager.getLogger("hatecheaters")
        logger.info("Logger loaded!")
    }

    fun logJsonToFile(jsonString: String, name: String = "unknown", type: String = "unknown", dir: String = "hypixel_logs") {
        if (!HateCheatersModule.enabled || !HateCheatersModule.logJson) return
        val minecraftDir = File("config/hatecheaters")
        val logFolder = File(minecraftDir, dir)
        if (!logFolder.exists()) {
            logFolder.mkdirs()
        }

        val logFile = File(logFolder, "${name}_${type}_${getCurrentDateTimeString()}.json")

        logFile.writeText(jsonString)
    }
}