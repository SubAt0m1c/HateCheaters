package com.github.subat0m1c.hatecheaters.utils.toasts

import com.github.subat0m1c.hatecheaters.modules.skyblock.HateCheatersModule
import com.github.subat0m1c.hatecheaters.modules.skyblock.HateCheatersModule.toasts
import com.github.subat0m1c.hatecheaters.utils.odinwrappers.*
import com.github.subat0m1c.hatecheaters.utils.toasts.ToastRenderer.ToastCorner.Companion.getFromIndex
import me.odinmain.OdinMain.mc
import me.odinmain.features.impl.render.ClickGUIModule
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

object ToastRenderer {
    enum class ToastCorner(val displayName: String) {
        TOP_LEFT("Top Left"),
        TOP_RIGHT("Top Right"),
        BOTTOM_LEFT("Bottom Left"),
        BOTTOM_RIGHT("Bottom Right");

        companion object {
            fun getFromIndex(index: Int) = entries[index]
        }
    }

    private fun renderAll() {
        var toasts = ToastManager.toasts
        if (toasts.isEmpty()) return

        val corner = getFromIndex(HateCheatersModule.toastCorner)
        val toastX =
            if (corner == ToastCorner.TOP_RIGHT || corner == ToastCorner.BOTTOM_RIGHT) mc.displayWidth - 20f else 20f
        var toastY =
            if (corner == ToastCorner.TOP_LEFT || corner == ToastCorner.TOP_RIGHT) mc.displayHeight - 20f else 20f

        val xMult = if (corner == ToastCorner.TOP_RIGHT || corner == ToastCorner.BOTTOM_RIGHT) 1 else -1
        val yMult = if (corner == ToastCorner.TOP_LEFT || corner == ToastCorner.TOP_RIGHT) -1 else 1

        Shaders.startDraw()
        Shaders.pushMatrix()

        val first = toasts.first()
        toastY += if (yMult == -1) first.toastHeight * yMult else 0f
        toasts = toasts.drop(1)
        renderToast(first, toastX - if (xMult == 1) first.width else 0f, toastY, xMult)
        if (first.isExpired) {
            first.shiftAnimation.start()
            toastY += (first.shiftAnimation.getNextPos() * yMult) - (10 * yMult)
        }
        toastY += (first.toastHeight + 20) * yMult
        toasts.forEach {
            renderToast(it, toastX - if (xMult == 1) first.width else 0f, toastY, xMult)
            toastY += (it.toastHeight + 20) * yMult
        }

        Shaders.popMatrix()
        Shaders.stopDraw()
    }

    private fun renderToast(toast: Toast, x: Float, y: Float, mult: Int) {
        var x = x
        if (toast.isExpired) {
            toast.endAnimation.start()
            x += toast.endAnimation.getNextPos() * mult
        } else {
            toast.startAnimation.start()
            x += toast.startAnimation.getNextPos() * mult
        }

        Shaders.rect(
            Box(x - 2, mc.displayHeight - toast.toastHeight - y - 2, toast.width + 4, toast.toastHeight + 4),
            color = ClickGUIModule.clickGUIColor.hc(),
            radius = 10f,
        )

        Shaders.rect(
            Box(x, mc.displayHeight - toast.toastHeight - y, toast.width, toast.toastHeight),
            color = Colors.gray26,
            radius = 10f,
        )

        Shaders.scissor(
            x, mc.displayHeight - toast.toastHeight - y + toast.toastHeight - 10, toast.width, 10f
        )

        Shaders.rect(
            Box(
                x + (toast.width - toast.progressBarWidth) / 2,
                mc.displayHeight - toast.toastHeight - y,
                toast.progressBarWidth,
                toast.toastHeight
            ),
            color = Colors.WHITE,
            radius = 10f,
        )
        Shaders.popScissor()

        val textX = x + 10f
        val textY = mc.displayHeight - toast.toastHeight - y

        wrapText(toast.title, (toast.width - 30).toInt(), toast.textScale * 1.5f).forEachIndexed { i, line ->
            Text.text(
                line,
                textX,
                textY + 10 + (i * 10) + (i * Text.textHeight(toast.textScale * 1.5f)),
                toast.textScale * 1.5f,
                Colors.WHITE,
                center = false
            )
        }

        wrapText(toast.message, (toast.width - 30).toInt(), toast.textScale).forEachIndexed { i, line ->
            Text.text(
                line,
                textX,
                textY + toast.titleHeight + 10 + (i * 10) + (i * Text.textHeight(toast.textScale)),
                toast.textScale,
                Colors.WHITE,
                center = false
            )
        }
    }

    @SubscribeEvent
    fun onPostRender(event: RenderGameOverlayEvent.Post) {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL || !toasts) return
        renderAll()
    }

    fun wrapText(text: String, width: Int, textScale: Float = 1f): List<String> {
        if (text.isEmpty()) return emptyList()
        val lines = mutableListOf<String>()

        for (line in text.split("\n")) {
            var currentLine = ""

            for (original in line.trimStart().split(Regex("\\s+"))) {
                var word = original

                while (Text.getWidth(word) > width / textScale) {
                    if (currentLine.isNotEmpty()) {
                        lines.add(currentLine)
                        currentLine = ""
                    }

                    var splitIndex = word.length
                    for (i in 1..word.length) {
                        val part = word.substring(0, i)
                        if (Text.getWidth(part) > width / textScale) {
                            splitIndex = (i - 1).coerceAtLeast(1)
                            break
                        }
                    }

                    lines.add(word.substring(0, splitIndex))
                    word = word.substring(splitIndex)
                }

                if (Text.getWidth(currentLine) + Text.getWidth(word) + (if (currentLine.isNotEmpty()) 1 else 0) <= width / textScale) {
                    if (currentLine.isNotEmpty()) currentLine += " "
                    currentLine += word
                } else {
                    if (currentLine.isNotEmpty()) {
                        lines.add(currentLine)
                    }
                    currentLine = word
                }
            }

            if (currentLine.isNotEmpty()) lines.add(currentLine)
        }

        return lines
    }
}