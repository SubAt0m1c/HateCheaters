package com.github.subat0m1c.hatecheaters.pvgui.v2.utils

import kotlin.reflect.KProperty

fun <T> profileLazy(initializer: () -> T) = ProfileLazy.create(initializer)

class ProfileLazy<out T>(private val initializer: () -> T) {
    private var _value: T? = null

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T = _value?.let { return it } ?: initializer().also { _value = it }

    fun reset() { _value = null }

    companion object {
        private val allLazies = mutableListOf<ProfileLazy<*>>()

        fun <T> create(initializer: () -> T): ProfileLazy<T> {
            val lazy = ProfileLazy(initializer)
            allLazies.add(lazy)
            return lazy
        }

        fun resetAll() {
            allLazies.forEach { it.reset() }
        }
    }
}