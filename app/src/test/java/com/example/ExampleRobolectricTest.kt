package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.PresetStorage
import com.example.data.db.AppDatabase
import com.example.data.db.RoomPresetRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    private lateinit var db: AppDatabase
    private lateinit var repository: RoomPresetRepository

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = RoomPresetRepository(db.presetDao())
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun roomDatabase_seedAndFavoriteFlow() = runBlocking {
        repository.ensureSeeded()

        val presets = repository.allPresets.first()
        assertEquals(1008, presets.size)

        val first = presets[0]
        assertFalse(first.isFavorite)

        // Toggle Favorite
        repository.toggleFavorite(first.id, first.isFavorite)

        val favs = repository.favoritePresets.first()
        assertEquals(1, favs.size)
        assertEquals(first.id, favs[0].id)
        assertTrue(favs[0].isFavorite)

        // Save note
        repository.saveNote(first.id, "Golden light test note")
        val updated = repository.getPresetById(first.id)
        assertNotNull(updated)
        assertEquals("Golden light test note", updated?.userNote)
    }

    @Test
    fun readStringFromContext() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Camera Presets", appName)
    }

    @Test
    fun presetStorage_toggleFavorite() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val storage = PresetStorage(context)

        assertFalse(storage.isFavorite(1))
        storage.toggleFavorite(1)
        assertTrue(storage.isFavorite(1))
        storage.toggleFavorite(1)
        assertFalse(storage.isFavorite(1))
    }

    @Test
    fun presetStorage_saveAndRetrieveNotes() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val storage = PresetStorage(context)

        storage.saveNote(5, "Shot on Santa Monica Pier at sunset")
        assertEquals("Shot on Santa Monica Pier at sunset", storage.getNote(5))
    }

    @Test
    fun themeManager_toggleAndPersist() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val themeManager = com.example.ui.theme.ThemeManager.getInstance(context)

        // Set to Dark
        themeManager.setThemeMode(com.example.ui.theme.AppThemeMode.SOPHISTICATED_DARK)
        assertEquals(com.example.ui.theme.AppThemeMode.SOPHISTICATED_DARK, themeManager.themeMode.value)
        assertFalse(themeManager.themeMode.value.isLight)

        // Toggle to Light
        themeManager.toggleTheme()
        assertEquals(com.example.ui.theme.AppThemeMode.HIGH_CONTRAST_LIGHT, themeManager.themeMode.value)
        assertTrue(themeManager.themeMode.value.isLight)

        // Toggle back to Dark
        themeManager.toggleTheme()
        assertEquals(com.example.ui.theme.AppThemeMode.SOPHISTICATED_DARK, themeManager.themeMode.value)
    }

    @Test
    fun reciprocityCalculator_profilesAndCalculations() {
        val s20Profile = com.example.ui.components.ReciprocityProfile.S20_ULTRA_RAW
        assertTrue(s20Profile.exponentP > 1.0)
        assertEquals(2.0, s20Profile.thresholdSec, 0.01)

        val filmProfile = com.example.ui.components.ReciprocityProfile.ILFORD_HP5
        assertEquals(1.31, filmProfile.exponentP, 0.01)
        assertEquals(1.0, filmProfile.thresholdSec, 0.01)
    }

    @Test
    fun s20Ultra_cameraAspectRatiosAndLenses() {
        // Verify S20 Ultra native aspect ratios
        val ratios = com.example.ui.screens.S20UltraAspectRatio.entries
        assertEquals(4, ratios.size)
        val full20x9 = com.example.ui.screens.S20UltraAspectRatio.RATIO_FULL
        assertEquals(9f, full20x9.ratioWidth, 0.001f)
        assertEquals(20f, full20x9.ratioHeight, 0.001f)

        val native3x4 = com.example.ui.screens.S20UltraAspectRatio.RATIO_3_4
        assertEquals(3f, native3x4.ratioWidth, 0.001f)
        assertEquals(4f, native3x4.ratioHeight, 0.001f)

        // Verify S20 Ultra triple-lens configuration
        val lenses = com.example.ui.screens.S20UltraLens.entries
        assertEquals(3, lenses.size)
        val ultraWide = com.example.ui.screens.S20UltraLens.ULTRA_WIDE
        assertEquals("0.5", ultraWide.zoomLabel)
        assertEquals("13mm", ultraWide.focalMm)

        val wide108 = com.example.ui.screens.S20UltraLens.WIDE_108MP
        assertEquals("1.0", wide108.zoomLabel)
        assertEquals("24mm", wide108.focalMm)

        val periscope = com.example.ui.screens.S20UltraLens.TELE_PERISCOPE
        assertEquals("5.0", periscope.zoomLabel)
        assertEquals("103mm", periscope.focalMm)
    }
}

