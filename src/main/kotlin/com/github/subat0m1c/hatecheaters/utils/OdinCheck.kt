package com.github.subat0m1c.hatecheaters.utils

import net.minecraft.client.Minecraft
import net.minecraftforge.fml.common.FMLCommonHandler
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
 * Modified from skyhanni, which includes parts modified from skytils.
 */
object OdinCheck {

    @JvmStatic
    fun checkIfOdinIsLoaded() {
        try {
            Class.forName("me.odinmain.OdinMain")
        } catch (e: Throwable) {
            odinWarning("missing")
            return
        }
    }

    private fun odinWarning(what: String) {
        openPopupWindow(
            "Odin is $what!\n" +
                    "HateCheaters requires the latest version of Odin to work.\n" +
                    "Use these links to download the latest version:",
            Pair("Open Odin GitHub", "https://github.com/odtheking/Odin"),
            Pair("Join OdinDiscord", "https://discord.gg/odin-1041616706327552000"),
            Pair("Open Mods Folder", File(Minecraft.getMinecraft().mcDataDir, "mods").toURI().toString()),
        )
        closeMinecraft()
    }

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
}