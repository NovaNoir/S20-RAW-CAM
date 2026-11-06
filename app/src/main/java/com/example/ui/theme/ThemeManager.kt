package com.example.ui.theme

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class AppThemeMode(
    val title: String,
    val shortLabel: String,
    val badge: String,
    val description: String,
    val bestFor: String
) {
    SOPHISTICATED_DARK(
        title = "Sophisticated Dark",
        shortLabel = "Dark",
        badge = "OLED • NIGHT",
        description = "Deep neutral canvas with warm amber and cyan HUD telemetry.",
        bestFor = "Astro, night sessions, low-light events, and battery preservation"
    ),
    HIGH_CONTRAST_LIGHT(
        title = "High-Contrast Light",
        shortLabel = "Light",
        badge = "DAYLIGHT • SUN",
        description = "Crisp daylight canvas with deep high-contrast slate typography.",
        bestFor = "Direct outdoor sunlight, midday landscapes, and high-glare field shoots"
    );

    val isLight: Boolean
        get() = this == HIGH_CONTRAST_LIGHT
}

class ThemeManager private constructor(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("camera_theme_prefs", Context.MODE_PRIVATE)

    private val _themeMode = MutableStateFlow(loadSavedTheme())
    val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

    private fun loadSavedTheme(): AppThemeMode {
        val savedName = prefs.getString(KEY_THEME_MODE, AppThemeMode.SOPHISTICATED_DARK.name)
        return try {
            AppThemeMode.valueOf(savedName ?: AppThemeMode.SOPHISTICATED_DARK.name)
        } catch (e: Exception) {
            AppThemeMode.SOPHISTICATED_DARK
        }
    }

    fun setThemeMode(mode: AppThemeMode) {
        _themeMode.value = mode
        prefs.edit().putString(KEY_THEME_MODE, mode.name).apply()
    }

    fun toggleTheme() {
        val next = if (_themeMode.value == AppThemeMode.SOPHISTICATED_DARK) {
            AppThemeMode.HIGH_CONTRAST_LIGHT
        } else {
            AppThemeMode.SOPHISTICATED_DARK
        }
        setThemeMode(next)
    }

    companion object {
        private const val KEY_THEME_MODE = "key_app_theme_mode"

        @Volatile
        private var INSTANCE: ThemeManager? = null

        fun getInstance(context: Context): ThemeManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ThemeManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
