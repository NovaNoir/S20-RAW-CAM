package com.example.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PresetStorage(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("camera_presets_prefs", Context.MODE_PRIVATE)

    private val _favoriteIds = MutableStateFlow<Set<Int>>(emptySet())
    val favoriteIds: StateFlow<Set<Int>> = _favoriteIds.asStateFlow()

    private val _notesMap = MutableStateFlow<Map<Int, String>>(emptyMap())
    val notesMap: StateFlow<Map<Int, String>> = _notesMap.asStateFlow()

    init {
        loadFavorites()
        loadNotes()
    }

    private fun loadFavorites() {
        val stringSet = prefs.getStringSet(KEY_FAVORITES, emptySet()) ?: emptySet()
        _favoriteIds.value = stringSet.mapNotNull { it.toIntOrNull() }.toSet()
    }

    private fun loadNotes() {
        val notes = mutableMapOf<Int, String>()
        prefs.all.forEach { (key, value) ->
            if (key.startsWith(KEY_NOTE_PREFIX) && value is String && value.isNotBlank()) {
                val id = key.removePrefix(KEY_NOTE_PREFIX).toIntOrNull()
                if (id != null) {
                    notes[id] = value
                }
            }
        }
        _notesMap.value = notes
    }

    fun toggleFavorite(presetId: Int) {
        val current = _favoriteIds.value.toMutableSet()
        if (current.contains(presetId)) {
            current.remove(presetId)
        } else {
            current.add(presetId)
        }
        prefs.edit().putStringSet(KEY_FAVORITES, current.map { it.toString() }.toSet()).apply()
        _favoriteIds.value = current
    }

    fun saveNote(presetId: Int, note: String) {
        val trimmed = note.trim()
        val currentNotes = _notesMap.value.toMutableMap()
        if (trimmed.isEmpty()) {
            prefs.edit().remove("${KEY_NOTE_PREFIX}$presetId").apply()
            currentNotes.remove(presetId)
        } else {
            prefs.edit().putString("${KEY_NOTE_PREFIX}$presetId", trimmed).apply()
            currentNotes[presetId] = trimmed
        }
        _notesMap.value = currentNotes
    }

    fun getNote(presetId: Int): String {
        return _notesMap.value[presetId] ?: ""
    }

    fun isFavorite(presetId: Int): Boolean {
        return _favoriteIds.value.contains(presetId)
    }

    companion object {
        private const val KEY_FAVORITES = "fav_preset_ids"
        private const val KEY_NOTE_PREFIX = "preset_note_"
    }
}
