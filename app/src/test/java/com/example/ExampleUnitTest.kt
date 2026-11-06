package com.example

import com.example.data.PresetCategory
import com.example.data.PresetRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun verifyAllPresetsExist() {
        val presets = PresetRepository.presets
        assertEquals(1008, presets.size)

        for (i in 1..1008) {
            val preset = presets[i - 1]
            assertEquals(i, preset.id)
            assertTrue(preset.familyId in 1..56)
            assertTrue(preset.name.isNotBlank())
            assertTrue(preset.familyName.isNotBlank())
            assertTrue(preset.lens.isNotBlank())
            assertTrue(preset.iso.isNotBlank())
            assertTrue(preset.shutter.isNotBlank())
            assertTrue(preset.wb.isNotBlank())
            assertTrue(preset.bestUse.isNotBlank())
            assertTrue(preset.meter.isNotBlank())
            assertTrue(preset.variant.isNotBlank())
            assertTrue(preset.variantCode.isNotBlank())
            assertNotNull(preset.category)
        }
    }

    @Test
    fun verifyFiftySixSceneFamilies() {
        val families = com.example.data.allSceneFamilyInfos
        assertEquals(56, families.size)

        for (i in 1..56) {
            val family = families[i - 1]
            assertEquals(i, family.id)
            assertTrue(family.name.isNotBlank())
            assertTrue(family.bestUse.isNotBlank())
            assertEquals(18, family.presetCount)
        }
    }

    @Test
    fun verifyKeyPresetsAndVariants() {
        val presets = PresetRepository.presets

        // Preset 01: Everyday Daylight (Base 1, Variant N)
        val p1 = presets[0]
        assertEquals("Everyday Daylight", p1.name)
        assertEquals("1x", p1.lens)
        assertEquals("ISO 50", p1.iso)
        assertEquals("1/500 s", p1.shutter)
        assertEquals("5200K", p1.wb)
        assertEquals("AF", p1.focus)
        assertEquals("0", p1.ev)
        assertEquals("Matrix", p1.meter)
        assertEquals("N", p1.variantCode)

        // Preset 02: Everyday Daylight (Base 1, Variant HL - Highlight protection)
        val p2 = presets[1]
        assertEquals("Everyday Daylight", p2.name)
        assertEquals("HL", p2.variantCode)
        assertEquals("-0.3", p2.ev)
        assertEquals("Matrix", p2.meter)

        // Preset 03: Everyday Daylight (Base 1, Variant DR - Dynamic range bias)
        val p3 = presets[2]
        assertEquals("Everyday Daylight", p3.name)
        assertEquals("DR", p3.variantCode)
        assertEquals("-0.7", p3.ev)

        // Preset 04: Everyday Daylight (Base 1, Variant SP - Spot metering)
        val p4 = presets[3]
        assertEquals("SP", p4.variantCode)
        assertEquals("Spot", p4.meter)

        // Preset 05: Everyday Daylight (Base 1, Variant CT - Center-weighted)
        val p5 = presets[4]
        assertEquals("CT", p5.variantCode)
        assertEquals("Center", p5.meter)
        assertEquals("-0.3", p5.ev)

        // Preset 06: Everyday Daylight (Base 1, Variant MF - Manual Focus)
        val p6 = presets[5]
        assertEquals("MF", p6.variantCode)
        assertEquals("MF", p6.focus)

        // Preset 901: Moon (Family 51, Base 1, Variant N)
        val p901 = presets[900]
        assertEquals("Moon", p901.name)
        assertEquals("4x", p901.lens)
        assertEquals("ISO 50", p901.iso)
        assertEquals("1/250 s", p901.shutter)
        assertEquals("5000K", p901.wb)
    }

    @Test
    fun verifyFieldGuideData() {
        assertEquals(9, PresetRepository.quickDecisionRules.size)
        assertEquals(8, PresetRepository.wbCheatSheet.size)
        assertEquals(6, PresetRepository.s20UltraRules.size)
    }

    @Test
    fun verifyPresetEntityMapping() {
        val original = PresetRepository.presets[0]
        val entity = com.example.data.db.PresetEntity.fromCameraPreset(original, isFavorite = true, userNote = "Great test note")
        assertEquals(original.id, entity.id)
        assertEquals(original.familyId, entity.familyId)
        assertEquals(original.name, entity.name)
        assertTrue(entity.isFavorite)
        assertEquals("Great test note", entity.userNote)

        val restored = entity.toCameraPreset()
        assertEquals(original.id, restored.id)
        assertEquals(original.familyId, restored.familyId)
        assertEquals(original.name, restored.name)
        assertTrue(restored.isFavorite)
        assertEquals("Great test note", restored.userNote)
    }

    @Test
    fun verifyPredictiveSearchSuggestions() {
        val families = com.example.data.allSceneFamilyInfos

        // 1. Empty query should return popular families
        val emptyResult = com.example.ui.PresetViewModel.computeSuggestions("", families)
        assertTrue(emptyResult.suggestedFamilies.isNotEmpty())
        assertEquals(8, emptyResult.suggestedFamilies.size)
        assertTrue(emptyResult.suggestedCategories.isEmpty())

        // 2. Query for "gold" should suggest Family #06 Golden Hour Front
        val goldenResult = com.example.ui.PresetViewModel.computeSuggestions("gold", families)
        assertNotNull(goldenResult.topPredictiveFamily)
        assertEquals(6, goldenResult.topPredictiveFamily?.id)
        assertEquals("Golden Hour Front", goldenResult.topPredictiveFamily?.name)
        assertEquals("Golden Hour Front", goldenResult.predictiveHint)

        // 3. Query for family number "51" or "#51" should suggest Moon
        val moonResult = com.example.ui.PresetViewModel.computeSuggestions("51", families)
        assertEquals(51, moonResult.topPredictiveFamily?.id)
        assertEquals("Moon", moonResult.topPredictiveFamily?.name)

        val moonPoundResult = com.example.ui.PresetViewModel.computeSuggestions("#51", families)
        assertEquals(51, moonPoundResult.topPredictiveFamily?.id)

        // 4. Query for category keyword "astro" should suggest Night & Astro category
        val astroResult = com.example.ui.PresetViewModel.computeSuggestions("astro", families)
        assertTrue(astroResult.suggestedCategories.contains(PresetCategory.NIGHT_ASTRO))
        assertTrue(astroResult.suggestedFamilies.any { it.category == PresetCategory.NIGHT_ASTRO })
    }
}
