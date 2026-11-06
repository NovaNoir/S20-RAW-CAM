package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.TipsAndUpdates
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ReciprocityCalculator
import com.example.ui.theme.AppTheme
import kotlin.math.log2
import kotlin.math.roundToInt

@Composable
fun ExposureCalculatorScreen(modifier: Modifier = Modifier) {
    val isoList = listOf(50, 100, 200, 400, 800, 1600, 3200)
    val shutterLabels = listOf(
        "1/1000s" to (1f / 1000f),
        "1/500s" to (1f / 500f),
        "1/250s" to (1f / 250f),
        "1/125s" to (1f / 125f),
        "1/60s" to (1f / 60f),
        "1/30s" to (1f / 30f),
        "1/15s" to (1f / 15f),
        "1/4s" to (1f / 4f),
        "1s" to 1f,
        "2s" to 2f,
        "5s" to 5f,
        "10s" to 10f,
        "20s" to 20f,
        "30s" to 30f
    )

    var selectedIsoIndex by remember { mutableIntStateOf(0) } // ISO 50
    var selectedShutterIndex by remember { mutableIntStateOf(1) } // 1/500s
    val aperture = 1.8f // S20 Ultra Main 1x sensor F1.8

    val currentIso = isoList[selectedIsoIndex]
    val (shutterLabel, shutterSec) = shutterLabels[selectedShutterIndex]

    // EV = log2(N^2 / t) - log2(ISO / 100)
    val ev100 = remember(shutterSec, currentIso) {
        val baseEv = log2((aperture * aperture) / shutterSec)
        val isoOffset = log2(currentIso.toFloat() / 100f)
        (baseEv - isoOffset).let { (it * 10).roundToInt() / 10f }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(AppTheme.colors.canvasBg)
            .testTag("exposure_calculator_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Title
        item {
            Column(modifier = Modifier.padding(top = 4.dp, start = 4.dp)) {
                Text(
                    text = "CALCULATOR & RECIPROCITY",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppTheme.colors.amberPrimary,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Exposure Equivalents",
                    fontFamily = FontFamily.Serif,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Light,
                    fontSize = 28.sp,
                    color = AppTheme.colors.textPrimary
                )
            }
        }

        // EV Result Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = AppTheme.colors.cardSurface),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(0.5.dp, AppTheme.colors.border)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "ESTIMATED EXPOSURE VALUE (EV @ ISO 100)",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppTheme.colors.textMuted,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.2.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "EV $ev100",
                        fontSize = 42.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = AppTheme.colors.amberPrimary,
                        fontFamily = FontFamily.Monospace
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = getSceneFromEv(ev100),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = AppTheme.colors.textPrimary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(AppTheme.colors.surfaceVariant)
                            .border(0.5.dp, AppTheme.colors.border, RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Lens: 1x (f/1.8)", fontSize = 11.sp, color = AppTheme.colors.textMuted, fontFamily = FontFamily.Monospace)
                        Text("Shutter: $shutterLabel", fontSize = 11.sp, color = AppTheme.colors.textPrimary, fontFamily = FontFamily.Monospace)
                        Text("ISO: $currentIso", fontSize = 11.sp, color = AppTheme.colors.amberPrimary, fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }

        // ISO Selector
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = AppTheme.colors.cardSurface),
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(0.5.dp, AppTheme.colors.border)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "SELECT SENSOR ISO",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppTheme.colors.textMuted,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "ISO $currentIso",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppTheme.colors.amberPrimary,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        isoList.forEachIndexed { index, iso ->
                            val isSelected = selectedIsoIndex == index
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(999.dp))
                                    .background(if (isSelected) AppTheme.colors.amberPrimary else AppTheme.colors.surfaceVariant)
                                    .border(0.5.dp, if (isSelected) AppTheme.colors.amberPrimary else AppTheme.colors.border, RoundedCornerShape(999.dp))
                                    .clickable { selectedIsoIndex = index }
                                    .padding(horizontal = 14.dp, vertical = 7.dp)
                                    .testTag("calc_iso_$iso")
                            ) {
                                Text(
                                    text = "$iso",
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) (if (AppTheme.colors.isLight) Color.White else Color.Black) else AppTheme.colors.textSecondary,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }
        }

        // Shutter Speed Selector
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = AppTheme.colors.cardSurface),
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(0.5.dp, AppTheme.colors.border)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "SELECT SHUTTER SPEED",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppTheme.colors.textMuted,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = shutterLabel,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppTheme.colors.cyanAccent,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        shutterLabels.forEachIndexed { index, (label, _) ->
                            val isSelected = selectedShutterIndex == index
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(999.dp))
                                    .background(if (isSelected) AppTheme.colors.amberPrimary else AppTheme.colors.surfaceVariant)
                                    .border(0.5.dp, if (isSelected) AppTheme.colors.amberPrimary else AppTheme.colors.border, RoundedCornerShape(999.dp))
                                    .clickable { selectedShutterIndex = index }
                                    .padding(horizontal = 12.dp, vertical = 7.dp)
                                    .testTag("calc_shutter_$index")
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) (if (AppTheme.colors.isLight) Color.White else Color.Black) else AppTheme.colors.textSecondary,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }
        }

        // Equivalent Shutter Reciprocity Chart
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = AppTheme.colors.cardSurface),
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(0.5.dp, AppTheme.colors.border)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.SwapHoriz, contentDescription = null, tint = AppTheme.colors.amberPrimary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "EQUIVALENT EXPOSURES (SAME BRIGHTNESS)",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppTheme.colors.amberPrimary,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val equivalents = listOf(
                        Triple(50, calculateEquivShutter(currentIso, shutterSec, 50), "Cleanest baseline"),
                        Triple(100, calculateEquivShutter(currentIso, shutterSec, 100), "Standard daylight"),
                        Triple(200, calculateEquivShutter(currentIso, shutterSec, 200), "Fast action street"),
                        Triple(400, calculateEquivShutter(currentIso, shutterSec, 400), "Indoor ambient / dusk"),
                        Triple(800, calculateEquivShutter(currentIso, shutterSec, 800), "Handheld night")
                    )

                    equivalents.forEach { (iso, speed, note) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "ISO $iso",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (iso == currentIso) AppTheme.colors.amberPrimary else AppTheme.colors.textPrimary,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier.width(70.dp)
                                )
                                Text(
                                    text = speed,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = AppTheme.colors.cyanAccent,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Text(
                                text = note,
                                fontSize = 11.sp,
                                color = AppTheme.colors.textMuted
                            )
                        }
                    }
                }
            }
        }

        // Reciprocity Failure Calculator Component
        item {
            ReciprocityCalculator()
        }

        // Expert S20 Ultra Tip
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = AppTheme.colors.cardSurface),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(0.5.dp, AppTheme.colors.border)
            ) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.Top) {
                    Icon(Icons.Filled.TipsAndUpdates, contentDescription = null, tint = AppTheme.colors.amberPrimary, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Field Rule: In Samsung Expert RAW, changing shutter speed changes motion blur, while ISO changes sensor amplification. Always protect your needed shutter speed first before raising ISO.",
                        style = MaterialTheme.typography.bodySmall,
                        color = AppTheme.colors.textSecondary,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(70.dp))
        }
    }
}

private fun getSceneFromEv(ev: Float): String {
    return when {
        ev >= 15f -> "Brilliant Sun on Sand / Snow / Beach"
        ev >= 14f -> "Bright Sunny Daylight (Clear Sky)"
        ev >= 12f -> "Weak Hazy Sunlight / Soft Shadows"
        ev >= 10f -> "Cloudy Overcast Day / Open Shade"
        ev >= 8f -> "Heavy Overcast / Golden Sunset Glow"
        ev >= 6f -> "Bright Commercial Interior / Window Light"
        ev >= 4f -> "Average Home / Restaurant Evening Lighting"
        ev >= 2f -> "Night Street / Bright Storefront Lights"
        ev >= 0f -> "Distant Night City / Landscape Under Full Moon"
        else -> "Very Dark Night / Astrophotography Sky"
    }
}

private fun calculateEquivShutter(fromIso: Int, fromShutter: Float, toIso: Int): String {
    val factor = fromIso.toFloat() / toIso.toFloat()
    val newShutter = fromShutter * factor
    return when {
        newShutter >= 1f -> String.format("%.1fs", newShutter)
        newShutter >= 0.001f -> {
            val denominator = (1f / newShutter).roundToInt()
            "1/${denominator}s"
        }
        else -> String.format("%.4fs", newShutter)
    }
}
