package com.github.subat0m1c.hatecheaters.utils.toasts

import com.github.subat0m1c.hatecheaters.utils.odinwrappers.Text
import com.github.subat0m1c.hatecheaters.utils.toasts.ToastRenderer.wrapText

class Toast(
    val title: String,
    val message: String,
    val width: Float = 300f,
    private var height: Float = 75f,
    val textScale: Float = 1.5f,
    expiresInSeconds: Long = 5L
) {
    private val createdAt = System.currentTimeMillis()
    private val expiresAt = (expiresInSeconds * 1000L) + System.currentTimeMillis()
    val startAnimation = ToastAnimation(width, 0f, 300)
    val endAnimation = ToastAnimation(0f, width, 300)
    val shiftAnimation = ToastAnimation(0f, -height - 10, 150)
    var titleHeight = -1f
    var messageHeight = -1f
    private var calculated = false

    val isExpired: Boolean
        get() = System.currentTimeMillis() > expiresAt

    val progressBarWidth: Float
        get() = width - ((System.currentTimeMillis() - createdAt).toFloat() / (expiresAt - createdAt).toFloat() * width)

    val toastHeight: Float
        get() {
            if (calculated) return height
            var titleHeight: Float
            var messageHeight: Float

            wrapText(title, (width - 30).toInt(), textScale * 1.5f).size.let { size ->
                titleHeight = (size * 10) + (size * Text.textHeight(textScale * 1.5f))
            }
            wrapText(message, (width - 30).toInt(), textScale).size.let { size ->
                messageHeight = (size * 10) + (size * Text.textHeight(textScale))
            }
            this.titleHeight = titleHeight
            this.messageHeight = messageHeight
            height = maxOf(titleHeight + messageHeight + 20, height)
            shiftAnimation.endOffset = -height - 10
            calculated = true
            return height
        }
}