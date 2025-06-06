package com.github.subat0m1c.hatecheaters.utils

import com.github.subat0m1c.hatecheaters.HateCheaters.Companion.launch
import com.github.subat0m1c.hatecheaters.modules.skyblock.HateCheatersModule
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.debug
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.getCurrentDateTimeString
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.FileHandler
import java.util.logging.Logger as javaLogger
import java.util.logging.SimpleFormatter
import java.util.zip.GZIPOutputStream

/**
 * im pretty sure this is bad but i could not figure out making log4j actually log to the right folder
 */
object LogHandler {
    object Logger {
        val log: javaLogger = javaLogger.getLogger("HateCheatersLogger")

        fun info(message: Any?) = log.info(message.toString()).also { debug(message) }

        fun warning(message: Any?) = log.warning(message.toString()).also { debug(message) }

        fun severe(message: Any?) = log.severe(message.toString()).also { debug(message) }

        fun fine(message: Any?) = log.fine(message.toString()).also { debug(message) }

        fun message(message: Any, data: Any? = null) =
            log.info("Recieved message: $message." + (data?.let { " With data: $it" } ?: ""))
                .also { debug("$message." + (data?.let { " With data: $it" } ?: "")) }
    }

    init {
        compressOldLogs()
        setupLogger()
    }

    private fun compressOldLogs() = launch {
        val logDir = File("config/hatecheaters/mod_logs")
        if (!logDir.exists() || !logDir.isDirectory) return@launch
        val logFiles = logDir.listFiles { file -> file.isFile && file.name.endsWith(".log") }
        logFiles?.forEach { file ->
            val tarGzFileName = "${file.absolutePath}.tar.gz"
            compressFileToTarGz(file, tarGzFileName)
            file.delete()
        }
    }

    private fun compressFileToTarGz(file: File, tarGzFileName: String) = runCatching {
        FileOutputStream(tarGzFileName).use { output ->
            GZIPOutputStream(output).use { gzipOut ->
                FileInputStream(file).use { input ->
                    val buffer = ByteArray(1024)
                    var len: Int
                    while (input.read(buffer).also { len = it } >= 0) {
                        gzipOut.write(buffer, 0, len)
                    }
                }
            }
        }
    }.onFailure { it.printStackTrace() }

    private fun setupLogger() = runCatching {
        val logDir = File("config/hatecheaters/mod_logs")
        if (!logDir.exists()) logDir.mkdirs()

        val fileHandler = FileHandler("${logDir.absolutePath}/${SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(Date())}.log", true)
        fileHandler.formatter = SimpleFormatter()
        Logger.log.addHandler(fileHandler)
        Logger.log.useParentHandlers = false
    }.onFailure { it.printStackTrace() }

//    fun logJsonToFile(jsonString: String, name: String = "unknown", type: String = "unknown", dir: String = "hypixel_logs") {
//        if (!HateCheatersModule.enabled || !HateCheatersModule.logJson) return
//        val logFolder = File(File("config/hatecheaters"), dir)
//        if (!logFolder.exists()) logFolder.mkdirs()
//        File(logFolder, "${name}_${type}_${getCurrentDateTimeString()}.json").writeText(jsonString)
//    }
}