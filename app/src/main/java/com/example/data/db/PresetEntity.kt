package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.CameraPreset
import com.example.data.PresetCategory

@Entity(tableName = "camera_presets")
data class PresetEntity(
    @PrimaryKey val id: Int,
    val familyId: Int,
    val familyName: String,
    val name: String,
    val category: String,
    val lens: String,
    val iso: String,
    val isoNum: Int,
    val shutter: String,
    val shutterSeconds: Float,
    val wb: String,
    val wbKelvin: Int,
    val focus: String,
    val ev: String,
    val evValue: Float,
    val bestUse: String,
    val tips: String,
    val meter: String = "Matrix",
    val variant: String = "Normal",
    val variantCode: String = "N",
    val isFavorite: Boolean = false,
    val userNote: String = ""
) {
    fun toCameraPreset(): CameraPreset {
        val cat = try {
            PresetCategory.valueOf(category)
        } catch (e: Exception) {
            PresetCategory.ALL
        }
        return CameraPreset(
            id = id,
            familyId = familyId,
            familyName = familyName,
            name = name,
            category = cat,
            lens = lens,
            iso = iso,
            isoNum = isoNum,
            shutter = shutter,
            shutterSeconds = shutterSeconds,
            wb = wb,
            wbKelvin = wbKelvin,
            focus = focus,
            ev = ev,
            evValue = evValue,
            bestUse = bestUse,
            tips = tips,
            meter = meter,
            variant = variant,
            variantCode = variantCode,
            isFavorite = isFavorite,
            userNote = userNote
        )
    }

    companion object {
        fun fromCameraPreset(preset: CameraPreset, isFavorite: Boolean = false, userNote: String = ""): PresetEntity {
            return PresetEntity(
                id = preset.id,
                familyId = preset.familyId,
                familyName = preset.familyName.ifBlank { preset.name },
                name = preset.name,
                category = preset.category.name,
                lens = preset.lens,
                iso = preset.iso,
                isoNum = preset.isoNum,
                shutter = preset.shutter,
                shutterSeconds = preset.shutterSeconds,
                wb = preset.wb,
                wbKelvin = preset.wbKelvin,
                focus = preset.focus,
                ev = preset.ev,
                evValue = preset.evValue,
                bestUse = preset.bestUse,
                tips = preset.tips,
                meter = preset.meter,
                variant = preset.variant,
                variantCode = preset.variantCode,
                isFavorite = isFavorite,
                userNote = userNote
            )
        }
    }
}
