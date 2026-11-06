package com.example.data.db

import com.example.data.CameraPreset
import com.example.data.PresetRepository
import com.example.data.SceneFamilyInfo
import com.example.data.allSceneFamilyInfos
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class RoomPresetRepository(private val presetDao: PresetDao) {

    val allPresets: Flow<List<PresetEntity>> = presetDao.getAllPresets()

    val favoritePresets: Flow<List<PresetEntity>> = presetDao.getFavoritePresets()

    val favoriteIds: Flow<Set<Int>> = presetDao.getFavoriteIds().map { it.toSet() }

    val sceneFamilies: List<SceneFamilyInfo> = allSceneFamilyInfos

    suspend fun ensureSeeded() = withContext(Dispatchers.IO) {
        val count = presetDao.getPresetCount()
        if (count < 1008) {
            val entities = PresetRepository.presets.map { preset ->
                PresetEntity.fromCameraPreset(preset)
            }
            presetDao.insertAll(entities)
        }
    }

    fun getPresetsByFamily(familyId: Int): Flow<List<PresetEntity>> =
        presetDao.getPresetsByFamily(familyId)

    fun getPresetsByCategory(category: String): Flow<List<PresetEntity>> =
        presetDao.getPresetsByCategory(category)

    fun searchPresets(query: String): Flow<List<PresetEntity>> =
        presetDao.searchPresets(query)

    suspend fun toggleFavorite(presetId: Int, currentFavorite: Boolean) = withContext(Dispatchers.IO) {
        presetDao.updateFavorite(presetId, !currentFavorite)
    }

    suspend fun saveNote(presetId: Int, note: String) = withContext(Dispatchers.IO) {
        presetDao.updateNote(presetId, note.trim())
    }

    suspend fun getPresetById(id: Int): PresetEntity? = withContext(Dispatchers.IO) {
        presetDao.getPresetById(id)
    }
}
