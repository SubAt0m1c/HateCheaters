package com.github.subat0m1c.hatecheaters.utils

import com.github.subat0m1c.hatecheaters.HateCheatersObject.scope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.FileHandler
import java.util.logging.Logger
import java.util.logging.SimpleFormatter
import java.util.zip.GZIPOutputStream
import kotlin.concurrent.thread

object LogHandler {
    val logger: Logger = Logger.getLogger("HateCheatersLogger")

    init {
        compressOldLogs()
        setupLogger()
    }

    private fun compressOldLogs() {
        scope.launch {
            val logDir = File("logs")
            if (logDir.exists() && logDir.isDirectory) {
                val logFiles = logDir.listFiles { file -> file.isFile && file.name.endsWith(".log") }
                logFiles?.forEach { file ->
                    val tarGzFileName = "${file.absolutePath}.tar.gz"
                    compressFileToTarGz(file, tarGzFileName)
                    file.delete()
                }
            }
        }
    }

    private fun compressFileToTarGz(file: File, tarGzFileName: String) {
        try {
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
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun setupLogger() {
        try {
            val logDir = File("logs")
            if (!logDir.exists()) {
                logDir.mkdirs()
            }

            val dateFormat = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss")
            val dateString = dateFormat.format(Date())
            val logFileName = "${logDir.absolutePath}/yourmod_$dateString.log"

            val fileHandler = FileHandler(logFileName, true)
            fileHandler.formatter = SimpleFormatter()
            logger.addHandler(fileHandler)
            logger.useParentHandlers = false
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}