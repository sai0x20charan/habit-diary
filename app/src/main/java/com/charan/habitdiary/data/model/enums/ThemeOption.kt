package com.charan.habitdiary.data.model.enums

enum class ThemeOption {
    SYSTEM_DEFAULT,
    LIGHT,
    DARK;

    override fun toString(): String {
        return when (this) {
            SYSTEM_DEFAULT -> "System"
            LIGHT -> "Light"
            DARK -> "Dark"
        }
    }

    fun fromString(value: String): ThemeOption {
        return when (value) {
            "Light" -> LIGHT
            "Dark" -> DARK
            else -> SYSTEM_DEFAULT
        }
    }
}