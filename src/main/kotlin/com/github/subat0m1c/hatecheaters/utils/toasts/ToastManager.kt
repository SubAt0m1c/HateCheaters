package com.github.subat0m1c.hatecheaters.utils.toasts

object ToastManager {
    private val _toasts = mutableListOf<Toast>()

    val toasts: List<Toast>
        get() {
            _toasts.removeIf { it.endAnimation.hasEnded() }
            return _toasts.toList()
        }

    fun addToast(toast: Toast) = _toasts.add(toast)

    fun toaster(
        title: String,
        message: String,
        width: Float = 300f,
        height: Float = 75f,
        textScale: Float = 1.5f,
        expiresInSeconds: Long = 5L
    ) {
        addToast(Toast(title, message, width, height, textScale, expiresInSeconds))
    }
}