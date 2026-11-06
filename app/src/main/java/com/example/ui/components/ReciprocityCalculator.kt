package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AppTheme
import kotlin.math.pow
import kotlin.math.roundToInt

enum class ReciprocityProfile(
    val title: String,
    val description: String,
    val exponentP: Double,
    val thresholdSec: Double
) {
    S20_ULTRA_RAW(
        title = "S20 Ultra Sensor RAW",
        description = "Digital CMOS sensor with dark current buildup & thermal noise compensation past 4s",
        exponentP = 1.14,
        thresholdSec = 2.0
    ),
    DIGITAL_LONG_EXPOSURE(
        title = "Standard Digital Sensor",
        description = "General CMOS/BSI sensor long exposure thermal curve",
        exponentP = 1.10,
        thresholdSec = 3.0
    ),
    ILFORD_HP5(
        title = "Ilford HP5 Plus (Film)",
        description = "Schwarzschild law Tc = T^1.31 above 1 second",
        exponentP = 1.31,
        thresholdSec = 1.0
    ),
    KODAK_TRI_X(
        title = "Kodak Tri-X 400 (Film)",
        description = "Classic silver halide curve Tc = T^1.38",
        exponentP = 1.38,
        thresholdSec = 1.0
    ),
    FUJI_ACROS(
        title = "Fuji Neopan Acros 100",
        description = "Famous low-reciprocity emulsion (virtually flat until 120s)",
        exponentP = 1.05,
        thresholdSec = 120.0
    )
}

@Composable
fun ReciprocityCalculator(
    modifier: Modifier = Modifier
) {
    // Base Exposure Seconds (from 0.5s up to 120s)
    var baseSeconds by remember { mutableFloatStateOf(8.0f) }
    var selectedProfile by remember { mutableStateOf(ReciprocityProfile.S20_ULTRA_RAW) }

    // Calculate Compensated Time
    val compensatedSeconds = remember(baseSeconds, selectedProfile) {
        calculateReciprocityCompensation(baseSeconds.toDouble(), selectedProfile)
    }

    val additionalSeconds = (compensatedSeconds - baseSeconds).coerceAtLeast(0.0)
    val stopCorrection = if (baseSeconds > 0) {
        kotlin.math.log2(compensatedSeconds / baseSeconds)
    } else 0.0

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("reciprocity_calculator_card"),
        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.cardSurface),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(0.5.dp, AppTheme.colors.border)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Timer,
                        contentDescription = null,
                        tint = AppTheme.colors.amberPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "RECIPROCITY FAILURE CALCULATOR",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppTheme.colors.amberPrimary,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.5.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(AppTheme.colors.amberGlow)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "+${String.format("%.1f", stopCorrection)} EV",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppTheme.colors.amberPrimary,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Comparison Hero: Base Time -> Compensated Time
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(AppTheme.colors.surfaceVariant)
                    .border(0.5.dp, AppTheme.colors.border, RoundedCornerShape(16.dp))
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "BASE METERED TIME",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppTheme.colors.textMuted,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = formatTimeSeconds(baseSeconds.toDouble()),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppTheme.colors.textPrimary,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Icon(
                    imageVector = Icons.Filled.HourglassEmpty,
                    contentDescription = null,
                    tint = AppTheme.colors.cyanAccent,
                    modifier = Modifier.size(24.dp)
                )

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "COMPENSATED TIME",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppTheme.colors.amberPrimary,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = formatTimeSeconds(compensatedSeconds),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = AppTheme.colors.amberPrimary,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Quick Base Time Presets
            Text(
                text = "INPUT BASE EXPOSURE TIME",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = AppTheme.colors.textMuted,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            val quickTimes = listOf(1f, 2f, 4f, 8f, 15f, 30f, 60f, 120f)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                quickTimes.forEach { sec ->
                    val isSelected = (baseSeconds - sec).let { kotlin.math.abs(it) < 0.2f }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(if (isSelected) AppTheme.colors.amberPrimary else AppTheme.colors.surfaceVariant)
                            .border(0.5.dp, if (isSelected) AppTheme.colors.amberPrimary else AppTheme.colors.border, RoundedCornerShape(999.dp))
                            .clickable { baseSeconds = sec }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .testTag("reciprocity_time_${sec.toInt()}")
                    ) {
                        Text(
                            text = "${sec.toInt()}s",
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) (if (AppTheme.colors.isLight) Color.White else Color.Black) else AppTheme.colors.textSecondary,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Slider for fine tuning
            Slider(
                value = baseSeconds,
                onValueChange = { baseSeconds = it },
                valueRange = 0.5f..120f,
                colors = SliderDefaults.colors(
                    thumbColor = AppTheme.colors.amberPrimary,
                    activeTrackColor = AppTheme.colors.amberPrimary,
                    inactiveTrackColor = AppTheme.colors.surfaceVariant
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("reciprocity_slider")
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Sensor / Film Profile Selection
            Text(
                text = "FILM / SENSOR CHARACTERISTIC",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = AppTheme.colors.textMuted,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                ReciprocityProfile.entries.forEach { profile ->
                    val isSelected = selectedProfile == profile
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) AppTheme.colors.amberGlow else AppTheme.colors.surfaceVariant)
                            .border(
                                0.5.dp,
                                if (isSelected) AppTheme.colors.amberPrimary else AppTheme.colors.border,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { selectedProfile = profile }
                            .padding(horizontal = 12.dp, vertical = 9.dp)
                            .testTag("reciprocity_profile_${profile.name}"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = profile.title,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isSelected) AppTheme.colors.amberPrimary else AppTheme.colors.textPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = profile.description,
                                fontSize = 10.sp,
                                color = AppTheme.colors.textMuted,
                                lineHeight = 14.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "p=${profile.exponentP}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppTheme.colors.cyanAccent,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Info note
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(AppTheme.colors.surfaceVariant.copy(alpha = 0.5f))
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = null,
                    tint = AppTheme.colors.cyanAccent,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Requires +${formatTimeSeconds(additionalSeconds)} extra exposure time to counteract sensitivity drop-off during long exposures.",
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 11.sp,
                    color = AppTheme.colors.textSecondary,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

private fun calculateReciprocityCompensation(baseSec: Double, profile: ReciprocityProfile): Double {
    if (baseSec <= profile.thresholdSec) {
        return baseSec
    }
    // Schwarzschild formula: Tc = T^p / (threshold^(p - 1))
    val p = profile.exponentP
    val t0 = profile.thresholdSec
    return (baseSec.pow(p)) / (t0.pow(p - 1.0))
}

private fun formatTimeSeconds(seconds: Double): String {
    return when {
        seconds < 1.0 -> String.format("%.2fs", seconds)
        seconds < 60.0 -> String.format("%.1fs", seconds)
        else -> {
            val mins = (seconds / 60).toInt()
            val remSec = (seconds % 60).roundToInt()
            "${mins}m ${remSec}s"
        }
    }
}
