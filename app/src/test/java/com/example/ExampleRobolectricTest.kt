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
    fun s20Ultra_expertRawModuleSpecs() {
        assertEquals("com.samsung.android.app.galaxyraw", com.example.ui.screens.EXPERT_RAW_PACKAGE)

        val lenses = com.example.ui.screens.S20UltraLensModule.entries
        assertEquals(3, lenses.size)

        val ultraWide = com.example.ui.screens.S20UltraLensModule.ULTRA_WIDE
        assertEquals("UW (0.5x)", ultraWide.dialLabel)
        assertEquals("13mm equiv.", ultraWide.focalMm)
        assertEquals("f/2.2", ultraWide.aperture)

        val wide108 = com.example.ui.screens.S20UltraLensModule.WIDE_108MP
        assertEquals("W (1.0x)", wide108.dialLabel)
        assertEquals("26mm equiv.", wide108.focalMm)
        assertEquals("f/1.8", wide108.aperture)

        val periscope = com.example.ui.screens.S20UltraLensModule.TELE_PERISCOPE
        assertEquals("T (4.0x/5.0x)", periscope.dialLabel)
        assertEquals("103mm equiv.", periscope.focalMm)
        assertEquals("f/3.5", periscope.aperture)
        assertTrue(periscope.sensorSpec.contains("4× Optical Baseline"))
    }

    @Test
    fun errorLogger_filePersistenceAndRead() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val logger = com.example.util.ErrorLogger

        // Clear any previous logs
        logger.clearLogs(context)

        // Log a test warning and error with exception
        logger.logWarning("TEST_TAG", "Sample warning for diagnostics", context)
        val dummyException = IllegalStateException("Simulated camera connection timeout")
        logger.logError("CAMERA_SUBSYSTEM", "Failed to communicate with sensor", dummyException, context)

        // Verify log file exists in internal storage
        val logFile = logger.getLogFile(context)
        assertTrue(logFile.exists())
        assertTrue(logFile.length() > 0)

        // Read logs and verify content
        val logOutput = logger.readLogs(context)
        assertTrue(logOutput.contains("S20-RAW-CAM DIAGNOSTIC REPORT"))
        assertTrue(logOutput.contains("Sample warning for diagnostics"))
        assertTrue(logOutput.contains("Simulated camera connection timeout"))
        assertTrue(logOutput.contains("CAMERA_SUBSYSTEM"))

        // Test clearing logs
        val cleared = logger.clearLogs(context)
        assertTrue(cleared)
        val afterClear = logger.readLogs(context)
        assertFalse(afterClear.contains("Simulated camera connection timeout"))
    }
}

