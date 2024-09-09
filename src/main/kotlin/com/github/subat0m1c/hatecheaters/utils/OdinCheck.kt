package com.github.subat0m1c.hatecheaters.utils

import me.odinmain.OdinMain.VERSION
import net.minecraft.client.Minecraft
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.Loader
import java.awt.Desktop
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.io.File
import java.net.URI
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JOptionPane
import javax.swing.UIManager

/**
 * Modified from SkyHanni under LGPL-2.1, which includes parts modified from Skytils under AGPL-3.0
 */
object OdinCheck {

    @JvmStatic
    fun checkIfOdinIsLoaded() {
        try {
            Class.forName("me.odinmain.OdinMain")
        } catch (e: Throwable) {
            odinWarning("Odin is missing!")
            return
        }

        try {
            if (isOldOdin) odinWarning("Odin is outdated!")
        } catch (e: Throwable) {
            odinWarning("An unknown error occurred trying to determine Odin version!")
            return
        }

    }

    private fun odinWarning(what: String) {
        openPopupWindow(
            "$what\n" +
                    "HateCheaters requires a fairly recent version of Odin to work. (@REQUIREDODINVERSION@)\n" +
                    "If you're SURE you're using the correct odin, contact SubAt0mic on discord.\n" +
                    "Otherwise, use these links to download the latest version of odin:",
            Pair("Open Odin GitHub", "https://github.com/odtheking/Odin"),
            Pair("Join Odin Discord", "https://discord.gg/odin-1041616706327552000"),
            Pair("Open Mods Folder", File(Minecraft.getMinecraft().mcDataDir, "mods").toURI().toString()),
        )
        closeMinecraft()
    }

    /**
     * Modified from Skytils
     */
    private fun openPopupWindow(errorMessage: String, vararg options: Pair<String, String>) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        val frame = JFrame()
        frame.isUndecorated = true
        frame.isAlwaysOnTop = true
        frame.setLocationRelativeTo(null)
        frame.isVisible = true

        val buttons = mutableListOf<JButton>()
        for ((name, link) in options) {
            val button = JButton(name)
            button.addMouseListener(object : MouseAdapter() {
                override fun mouseClicked(event: MouseEvent) {
                    try {
                        Desktop.getDesktop().browse(URI(link))
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                }
            })
            buttons.add(button)
        }
        val close = JButton("Close")
        close.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(event: MouseEvent) {
                try {
                    closeMinecraft()
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
        })
        buttons.add(close)

        val allOptions = buttons.toTypedArray()
        JOptionPane.showOptionDialog(
            frame,
            errorMessage,
            "Odin Error",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.ERROR_MESSAGE,
            null,
            allOptions,
            allOptions[0]
        )
    }

    fun closeMinecraft() {
        FMLCommonHandler.instance().handleExit(-1)
        FMLCommonHandler.instance().expectServerStopped()
    }

    private val versionRegex = Regex("(\\d)\\.(\\d)\\.(\\d)(?:\\.(\\d))?(?:\\.beta(\\d))?")

    private val isOldOdin: Boolean get() {
        val odinVersion = Loader.instance().activeModList.find { it.modId == "od" || it.modId == "odclient" }?.version ?: ""
        val matchResult = versionRegex.find(odinVersion)?.groupValues
        val odinResult = versionRegex.find("@REQUIREDODINVERSION@")?.groupValues

        return matchResult?.mapIndexedNotNull { index, s ->
            if ((s.toIntOrNull() ?: 0) < (odinResult?.get(index)?.toIntOrNull() ?: 0)) true else null
        }?.isNotEmpty() ?: true
    }
}