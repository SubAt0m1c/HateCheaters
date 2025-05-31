package com.github.subat0m1c.hatecheaters.utils.toasts

class ToastAnimation(
    private var startOffset: Float,
    var endOffset: Float,
    private val animationDuration: Long
) {
    private val initialStartOffset = startOffset
    private val initialEndOffset = endOffset
    private var started = false
    private var animationStart = 0L
    private var currentAnimationTime: Long = 0L

    fun start() {
        if (started) return
        animationStart = System.currentTimeMillis()
        started = true
    }

    fun reset() {
        animationStart = 0L
        currentAnimationTime = 0L
        startOffset = initialStartOffset
        endOffset = initialEndOffset
        started = false
    }

    fun hasEnded(): Boolean {
        return currentAnimationTime >= animationDuration
    }

    fun getNextPos(): Float {
        currentAnimationTime = System.currentTimeMillis() - animationStart
        if (hasEnded()) return endOffset
        return startOffset + (endOffset - startOffset) * (currentAnimationTime.toFloat() / animationDuration)
    }
}