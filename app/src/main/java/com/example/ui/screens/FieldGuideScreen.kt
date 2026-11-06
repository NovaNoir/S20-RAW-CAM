package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import com.example.data.PresetRepository
import com.example.ui.theme.AppTheme
import com.example.ui.theme.kelvinToColor

@Composable
fun FieldGuideScreen(modifier: Modifier = Modifier) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Quick Decision", "WB Cheat Sheet", "S20 Rules")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppTheme.colors.canvasBg)
            .testTag("field_guide_screen")
    ) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 12.dp)
        ) {
            Text(
                text = "EXPERT RAW FIELD REFERENCE",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = AppTheme.colors.amberPrimary,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Field Guide & Rules",
                fontFamily = FontFamily.Serif,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Light,
                fontSize = 28.sp,
                color = AppTheme.colors.textPrimary
            )
        }

        // Pro Camera Style Tab Bar
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = AppTheme.colors.canvasBg,
            contentColor = AppTheme.colors.amberPrimary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = AppTheme.colors.amberPrimary
                )
            },
            divider = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(0.5.dp)
                        .background(AppTheme.colors.border)
                )
            }
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = 12.sp,
                            fontWeight = if (selectedTab == index) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (selectedTab == index) AppTheme.colors.amberPrimary else AppTheme.colors.textMuted,
                            letterSpacing = 0.5.sp
                        )
                    },
                    modifier = Modifier.testTag("field_guide_tab_$index")
                )
            }
        }

        when (selectedTab) {
            0 -> QuickDecisionTab()
            1 -> WhiteBalanceTab()
            2 -> S20RulesTab()
        }
    }
}

@Composable
private fun QuickDecisionTab() {
    val rules = PresetRepository.quickDecisionRules

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = AppTheme.colors.cardSurface),
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(0.5.dp, AppTheme.colors.border)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.AutoGraph, contentDescription = null, tint = AppTheme.colors.amberPrimary, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Instant exposure baseline for common real-world conditions. Use as your initial starting position before checking the histogram.",
                        style = MaterialTheme.typography.bodySmall,
                        color = AppTheme.colors.textSecondary,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        items(rules) { rule ->
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
                            text = rule.condition,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AppTheme.colors.textPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(AppTheme.colors.surfaceVariant)
                            .border(0.5.dp, AppTheme.colors.border, RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 9.dp)
                    ) {
                        Text(
                            text = rule.settings,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AppTheme.colors.amberPrimary,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = rule.note,
                        style = MaterialTheme.typography.bodySmall,
                        color = AppTheme.colors.textSecondary,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

@Composable
private fun WhiteBalanceTab() {
    var interactiveKelvin by remember { mutableFloatStateOf(5200f) }
    val currentKelvinInt = interactiveKelvin.toInt()
    val cheatSheet = PresetRepository.wbCheatSheet

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Interactive Kelvin Simulator
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = AppTheme.colors.cardSurface),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(0.5.dp, AppTheme.colors.border)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "INTERACTIVE KELVIN TESTER",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppTheme.colors.amberPrimary,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.5.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .clip(CircleShape)
                                    .background(kelvinToColor(currentKelvinInt))
                                    .border(0.5.dp, AppTheme.colors.border, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "${currentKelvinInt}K",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppTheme.colors.textPrimary,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Text(
                            text = getKelvinDescription(currentKelvinInt),
                            fontSize = 12.sp,
                            color = AppTheme.colors.cyanAccent,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Slider(
                        value = interactiveKelvin,
                        onValueChange = { interactiveKelvin = it },
                        valueRange = 2500f..7500f,
                        steps = 50,
                        colors = SliderDefaults.colors(
                            thumbColor = AppTheme.colors.amberPrimary,
                            activeTrackColor = AppTheme.colors.amberPrimary,
                            inactiveTrackColor = AppTheme.colors.surfaceVariant
                        ),
                        modifier = Modifier.testTag("kelvin_slider")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("2500K (Warm/Candle)", fontSize = 10.sp, color = AppTheme.colors.textMuted)
                        Text("5200K (Daylight)", fontSize = 10.sp, color = AppTheme.colors.textMuted)
                        Text("7500K (Deep Shade)", fontSize = 10.sp, color = AppTheme.colors.textMuted)
                    }
                }
            }
        }

        // White Balance Reference Table
        item {
            Text(
                text = "LIGHTING & STARTING WB",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = AppTheme.colors.textMuted,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.5.sp,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }

        items(cheatSheet) { item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { interactiveKelvin = item.kelvin.toFloat() },
                colors = CardDefaults.cardColors(containerColor = AppTheme.colors.cardSurface),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(0.5.dp, AppTheme.colors.border)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(kelvinToColor(item.kelvin))
                                .border(0.5.dp, AppTheme.colors.border, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = item.lighting,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = AppTheme.colors.textPrimary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(AppTheme.colors.surfaceVariant)
                            .border(0.5.dp, AppTheme.colors.border, RoundedCornerShape(999.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = item.range,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AppTheme.colors.amberPrimary,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun S20RulesTab() {
    val rules = PresetRepository.s20UltraRules

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Field Note Hero
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = AppTheme.colors.cardSurface),
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(0.5.dp, AppTheme.colors.amberPrimary.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Info, contentDescription = null, tint = AppTheme.colors.amberPrimary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "OFFICIAL FIELD NOTE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppTheme.colors.amberPrimary,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "These 50 presets are deliberately conservative starting points for the S20 Ultra sensor generation. Real exposure depends on the scene, lens, lighting and software build. Treat the histogram and highlight clipping as the final authority.",
                        style = MaterialTheme.typography.bodySmall,
                        color = AppTheme.colors.textSecondary,
                        lineHeight = 19.sp
                    )
                }
            }
        }

        item {
            Text(
                text = "S20 ULTRA-SPECIFIC PRACTICAL RULES",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = AppTheme.colors.textMuted,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.5.sp,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }

        items(rules) { rule ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = AppTheme.colors.cardSurface),
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(0.5.dp, AppTheme.colors.border)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(AppTheme.colors.amberGlow),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = AppTheme.colors.amberPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = rule.title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AppTheme.colors.textPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = rule.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = AppTheme.colors.textSecondary,
                            lineHeight = 19.sp
                        )
                    }
                }
            }
        }

        item {
            val context = LocalContext.current
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
                    .testTag("card_field_guide_diagnostics"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AppTheme.colors.surfaceVariant),
                border = BorderStroke(0.5.dp, AppTheme.colors.border)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.BugReport,
                            contentDescription = null,
                            tint = AppTheme.colors.amberPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "SYSTEM DIAGNOSTICS & LOGS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppTheme.colors.amberPrimary,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Capture application exceptions and runtime telemetry to internal storage, and export via the Android system share sheet.",
                        style = MaterialTheme.typography.bodySmall,
                        color = AppTheme.colors.textSecondary,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { com.example.util.ErrorLogger.exportLogs(context) },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AppTheme.colors.amberPrimary,
                            contentColor = if (AppTheme.colors.isLight) Color.White else Color.Black
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_export_logs_field_guide")
                    ) {
                        Icon(Icons.Filled.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Export Diagnostic Error Logs", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

private fun getKelvinDescription(kelvin: Int): String {
    return when {
        kelvin < 3200 -> "Warm Tungsten / Candle"
        kelvin < 4000 -> "Night City / Neon"
        kelvin < 4800 -> "Fluorescent / Cool Indoor"
        kelvin < 5600 -> "Standard Daylight"
        kelvin < 6200 -> "Cloudy Daylight"
        kelvin < 6800 -> "Overcast / Open Shade"
        else -> "Deep Mountain Shade"
    }
}
