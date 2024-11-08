package com.github.subat0m1c.hatecheaters.pvgui.v2.utils

import kotlin.reflect.KProperty

/**
 * Used so you can lazily define values in the class and reset them when the profile is loaded.
 * Reset when profile type is updated
 *
 * All values using player values at any point MUST use this.
 */
fun <T> profileLazy(initializer: () -> T) = ProfileLazy.create(initializer)

class ProfileLazy<out T>(private val initializer: () -> T) {
    private var _value: T? = null

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T = _value ?: initializer().also { _value = it }

    fun reset() { _value = null }
    fun noInit() = _value

    companion object {
        private val allLazies = mutableListOf<ProfileLazy<*>>()

        fun <T> create(initializer: () -> T): ProfileLazy<T> {
            val lazy = ProfileLazy(initializer)
            allLazies.add(lazy)
            return lazy
        }

        fun resetAll() = allLazies.forEach { it.reset() }
    }
}