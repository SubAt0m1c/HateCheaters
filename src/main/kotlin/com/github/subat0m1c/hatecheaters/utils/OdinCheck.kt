package com.github.subat0m1c.hatecheaters.utils

import com.github.subat0m1c.hatecheaters.utils.LogHandler.Logger
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
 * Modified from [SkyHanni](https://github.com/hannibal002/SkyHanni) under [LGPL-2.1](https://github.com/hannibal002/SkyHanni/blob/beta/LICENSE).
 * Includes parts modified from [Skytils](https://github.com/Skytils/SkytilsMod) under [AGPL-3.0](https://github.com/Skytils/SkytilsMod/blob/1.x/LICENSE.md).
 */
object OdinCheck {

    fun checkIfOdinIsLoaded() {
        try {
            Class.forName("me.odinmain.OdinMain")
        } catch (e: Throwable) {
            odinWarning("Odin is missing!")
            return
        }

        try {
            val currentOdin = Loader.instance().activeModList.find { it.modId == "od" || it.modId == "odclient" }?.version ?: ""
            if (compareVersions(currentOdin, "@REQUIREDODINVERSION@") == -1) odinWarning("Odin is outdated!", currentOdin)
        } catch (e: Throwable) {
            odinWarning("An unknown error occurred trying to determine Odin version!")
            return
        }
        Logger.info("Odin Loaded!")
    }

    private fun odinWarning(what: String, currentOdin: String? = null) {
        openPopupWindow(
            "$what\n" +
                    "HateCheaters requires a fairly recent version of Odin to work.\n" +
                    "Expected: @REQUIREDODINVERSION@" + (if (currentOdin != null) " | Found: $currentOdin\n" else "\n") +
                    (if ("@REQUIREDODINVERSION@" == currentOdin) "Since your version seems to be correct, contact SubAt0mic on discord.\n" else "") +
                    "Use these links to download the latest version of odin:",
            Pair("Open Odin GitHub", "https://github.com/odtheking/Odin"),
            Pair("Join Odin Discord", "https://discord.gg/odin-1041616706327552000"),
            Pair("Open Mods Folder", File(Minecraft.getMinecraft().mcDataDir, "mods").toURI().toString()),
        )
        closeMinecraft()
    }

    /**
     * Modified by [SkyHanni](https://github.com/hannibal002/SkyHanni) under [LGPL-2.1](https://github.com/hannibal002/SkyHanni/blob/beta/LICENSE).
     * Taken from [Skytils](https://github.com/Skytils/SkytilsMod) under [AGPL-3.0](https://github.com/Skytils/SkytilsMod/blob/1.x/LICENSE.md).
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
            "HateCheaters Error",
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

    private val versionRegex = Regex("(\\d)\\.(\\d)\\.(\\d)(?:\\.(\\d))?(?:\\.beta(\\d+))?")

    /**
     * UPDATE: it now generally compares versions between the 2 args, might be good idk
     *
     * I think this code is bad but i cant be bothered fixing it. also can return lower than -1.
     *
     * @return -1 if version1 is older, 1 if version1 is newer, 0 if they're the same.
     */
    fun compareVersions(version1: String, version2: String): Int {
        val v1 = versionRegex.find(version1)?.groupValues?.drop(1)?.map { it.toIntOrNull() } ?: return -2
        val v2 = versionRegex.find(version2)?.groupValues?.drop(1)?.map { it.toIntOrNull() } ?: return -2


        for (i in 0..2) {
            val compared = compareNumbers(v1[i], v2[i])
            if (compared != 0) return compared
        }

        val comparedFourth = compareNumbers(v1.getOrNull(3), v2.getOrNull(3))
        if (comparedFourth != 0) return comparedFourth

        val v1Beta = v1.getOrNull(4)
        val v2Beta = v2.getOrNull(4)

        if (v1Beta != null && v2Beta != null) return compareNumbers(v1Beta, v2Beta)
        else if (v1Beta != null) return -1
        else if (v2Beta != null) return 1

        return 0
    }

    private fun compareNumbers(n1: Int?, n2: Int?): Int {
        if (n1 == null && n2 == null) return 0
        if (n1 == null) return -1
        if (n2 == null) return 1
        return n1.compareTo(n2)
    }
}