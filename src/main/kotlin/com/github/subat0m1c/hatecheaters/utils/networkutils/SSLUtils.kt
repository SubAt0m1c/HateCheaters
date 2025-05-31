package com.github.subat0m1c.hatecheaters.utils.networkutils

import com.github.subat0m1c.hatecheaters.HateCheaters
import com.github.subat0m1c.hatecheaters.utils.LogHandler.Logger
import java.security.KeyStore
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

object SSLUtils {
    private val TrustManager: TrustManagerFactory by lazy {
        TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm()).apply {
            init(KeyStore.getInstance(KeyStore.getDefaultType()).apply {
                load(HateCheaters::class.java.getResourceAsStream("/hccacerts.jks"), "changeit".toCharArray())
            })
        }
    }

    fun createSslContext(): SSLContext = SSLContext.getInstance("TLS").apply {
        try {
            init(null, TrustManager.trustManagers, null)
            Logger.info("Created SSLContext successfully.")
        } catch (e: Exception) {
            Logger.severe("Failed to create SSLContext: ${e.message}")
            throw e
        }
    }

    fun getTrustManager(): X509TrustManager = try {
        val manager = TrustManager.trustManagers
        if (manager.isNotEmpty()) manager[0] as X509TrustManager
        else throw IllegalStateException("No TrustManager found")
    } catch(e: Exception) {
        Logger.severe("Failed to get TrustManager: ${e.message}")
        throw e
    }
}