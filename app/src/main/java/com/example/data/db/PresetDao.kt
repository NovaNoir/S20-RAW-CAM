package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface PresetDao {
    @Query("SELECT * FROM camera_presets ORDER BY id ASC")
    fun getAllPresets(): Flow<List<PresetEntity>>

    @Query("SELECT * FROM camera_presets WHERE isFavorite = 1 ORDER BY id ASC")
    fun getFavoritePresets(): Flow<List<PresetEntity>>

    @Query("SELECT id FROM camera_presets WHERE isFavorite = 1")
    fun getFavoriteIds(): Flow<List<Int>>

    @Query("SELECT * FROM camera_presets WHERE familyId = :familyId ORDER BY id ASC")
    fun getPresetsByFamily(familyId: Int): Flow<List<PresetEntity>>

    @Query("SELECT * FROM camera_presets WHERE category = :category ORDER BY id ASC")
    fun getPresetsByCategory(category: String): Flow<List<PresetEntity>>

    @Query("SELECT * FROM camera_presets WHERE id = :id LIMIT 1")
    suspend fun getPresetById(id: Int): PresetEntity?

    @Query("SELECT * FROM camera_presets WHERE id = :id LIMIT 1")
    fun getPresetByIdFlow(id: Int): Flow<PresetEntity?>

    @Query("SELECT COUNT(*) FROM camera_presets")
    suspend fun getPresetCount(): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(presets: List<PresetEntity>)

    @Query("UPDATE camera_presets SET isFavorite = :isFavorite WHERE id = :presetId")
    suspend fun updateFavorite(presetId: Int, isFavorite: Boolean)

    @Query("UPDATE camera_presets SET userNote = :note WHERE id = :presetId")
    suspend fun updateNote(presetId: Int, note: String)

    @Query("""
        SELECT * FROM camera_presets 
        WHERE (:query = '' 
               OR name LIKE '%' || :query || '%' 
               OR familyName LIKE '%' || :query || '%'
               OR bestUse LIKE '%' || :query || '%'
               OR variant LIKE '%' || :query || '%'
               OR variantCode LIKE '%' || :query || '%'
               OR tips LIKE '%' || :query || '%'
               OR lens LIKE '%' || :query || '%'
               OR iso LIKE '%' || :query || '%'
               OR shutter LIKE '%' || :query || '%'
               OR wb LIKE '%' || :query || '%'
               OR category LIKE '%' || :query || '%'
               OR meter LIKE '%' || :query || '%')
        ORDER BY id ASC
    """)
    fun searchPresets(query: String): Flow<List<PresetEntity>>
}
