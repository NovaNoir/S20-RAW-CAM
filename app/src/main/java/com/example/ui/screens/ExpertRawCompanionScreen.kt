package com.example.ui.screens

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CameraPreset
import com.example.data.PresetRepository
import com.example.ui.theme.AppTheme
import com.example.ui.theme.kelvinToColor

const val EXPERT_RAW_PACKAGE = "com.samsung.android.app.galaxyraw"

/**
 * Launches Samsung Expert RAW if installed, or falls back to Galaxy Store details page.
 */
fun Context.openExpertRawOrStore() {
    val launchIntent = packageManager.getLaunchIntentForPackage(EXPERT_RAW_PACKAGE)
    if (launchIntent != null) {
        startActivity(launchIntent)
        return
    }
    val market = Intent(
        Intent.ACTION_VIEW,
        Uri.parse("market://details?id=$EXPERT_RAW_PACKAGE")
    )
    try {
        startActivity(market)
    } catch (_: ActivityNotFoundException) {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://galaxystore.samsung.com/detail/$EXPERT_RAW_PACKAGE")
            )
        )
    }
}

/**
 * Samsung Galaxy S20 Ultra Physical Rear Camera System Modules.
 * - Ultra-Wide: 13mm equivalent, f/2.2, 12MP, 120° FOV
 * - Wide: ~26mm equivalent, f/1.8, 108MP ISOCELL Bright HM1 (1/1.33", OIS)
 * - Telephoto: 103mm equivalent, f/3.5, 48MP periscope (1/2.0", OIS, 4x optical baseline / 10x hybrid)
 */
enum class S20UltraLensModule(
    val dialLabel: String,
    val focalMm: String,
    val aperture: String,
    val sensorSpec: String,
    val opticalMagnification: String,
    val recommendedUse: String
) {
    ULTRA_WIDE(
        dialLabel = "UW (0.5x)",
        focalMm = "13mm equiv.",
        aperture = "f/2.2",
        sensorSpec = "12MP Ultra-Wide · 120° FOV",
        opticalMagnification = "0.5× Ultra-Wide",
        recommendedUse = "Expansive landscapes, architecture, Milky Way foregrounds, dramatic perspectives"
    ),
    WIDE_108MP(
        dialLabel = "W (1.0x)",
        focalMm = "26mm equiv.",
        aperture = "f/1.8",
        sensorSpec = "108MP ISOCELL HM1 · 1/1.33\" OIS",
        opticalMagnification = "1.0× Native Primary",
        recommendedUse = "Maximum dynamic range, low light, astrophotography, everyday scenes, fine detail"
    ),
    TELE_PERISCOPE(
        dialLabel = "T (4.0x/5.0x)",
        focalMm = "103mm equiv.",
        aperture = "f/3.5",
        sensorSpec = "48MP Periscope Tele · 4× Optical Baseline",
        opticalMagnification = "4× Optical (10× Hybrid Opt)",
        recommendedUse = "Distant details, background compression, candid portraits, wildlife, architectural isolation"
    )
}

data class ExpertRawDialStep(
    val stepNumber: Int,
    val dialName: String,
    val dialLocation: String,
    val recommendedValue: String,
    val copyableText: String,
    val acceptableRange: String,
    val whyRationale: String,
    val visualAccent: Color? = null
)

@Composable
fun ExpertRawCompanionScreen(
    initialPresetId: Int = 1,
    onSelectPreset: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedPresetId by rememberSaveable { mutableIntStateOf(initialPresetId) }

    val preset = remember(selectedPresetId) {
        PresetRepository.getById(selectedPresetId) ?: PresetRepository.presets.first()
    }

    // Recommended lens based on preset genre/focal requirement
    val recommendedLens = remember(preset) {
        when {
            preset.name.contains("Milky", ignoreCase = true) ||
            preset.name.contains("Aurora", ignoreCase = true) ||
            preset.name.contains("Architecture", ignoreCase = true) -> S20UltraLensModule.ULTRA_WIDE

            preset.name.contains("Portrait", ignoreCase = true) ||
            preset.name.contains("Macro", ignoreCase = true) ||
            preset.name.contains("Distant", ignoreCase = true) -> S20UltraLensModule.TELE_PERISCOPE

            else -> S20UltraLensModule.WIDE_108MP
        }
    }

    // Step check-off completion state per preset
    val completedSteps = remember(selectedPresetId) {
        mutableStateMapOf<Int, Boolean>()
    }

    val steps = remember(preset, recommendedLens) {
        listOf(
            ExpertRawDialStep(
                stepNumber = 1,
                dialName = "Lens Module",
                dialLocation = "Expert RAW bottom switcher → ${recommendedLens.dialLabel}",
                recommendedValue = "${recommendedLens.dialLabel} (${recommendedLens.focalMm} ${recommendedLens.aperture})",
                copyableText = recommendedLens.focalMm,
                acceptableRange = recommendedLens.opticalMagnification,
                whyRationale = recommendedLens.recommendedUse
            ),
            ExpertRawDialStep(
                stepNumber = 2,
                dialName = "ISO Sensitivity",
                dialLocation = "Expert RAW dial 1 → 'ISO'",
                recommendedValue = preset.iso,
                copyableText = preset.iso.replace("ISO ", ""),
                acceptableRange = when {
                    preset.iso.contains("50") -> "ISO 50 – 100 for cleanest shadow detail"
                    preset.iso.contains("100") -> "ISO 50 – 200 depending on light stability"
                    preset.iso.contains("400") -> "ISO 200 – 800 for handheld balance"
                    preset.iso.contains("1600") -> "ISO 800 – 3200 (watch noise in shadows)"
                    else -> "Adjust as scene brightness varies"
                },
                whyRationale = "ISOCELL HM1 sensor achieves maximum dynamic range at base ISO. Keep low unless shutter duration is constrained."
            ),
            ExpertRawDialStep(
                stepNumber = 3,
                dialName = "Shutter Duration",
                dialLocation = "Expert RAW dial 2 → 'SPEED'",
                recommendedValue = preset.shutter,
                copyableText = preset.shutter,
                acceptableRange = when {
                    preset.shutter.contains("s") && !preset.shutter.contains("/") -> "Tripod Mandatory — hold phone steady"
                    preset.shutter.contains("1/500") || preset.shutter.contains("1/1000") -> "Fast action freeze range"
                    else -> "Hand-held safe if using OIS"
                },
                whyRationale = "Determines light accumulation and motion blur. ${preset.tips}"
            ),
            ExpertRawDialStep(
                stepNumber = 4,
                dialName = "EV Compensation",
                dialLocation = "Expert RAW dial 3 → 'EV'",
                recommendedValue = preset.ev,
                copyableText = preset.ev,
                acceptableRange = "-2.0 EV to +2.0 EV in 0.1 increments",
                whyRationale = if (preset.ev == "0") "Neutral metering baseline. Protect highlights from clipping." else "Intentional bias to safeguard spectral highlights or lift deep tones."
            ),
            ExpertRawDialStep(
                stepNumber = 5,
                dialName = "White Balance",
                dialLocation = "Expert RAW dial 4 → 'WB' (Kelvin slider)",
                recommendedValue = "${preset.wb} (${preset.wbKelvin}K)",
                copyableText = "${preset.wbKelvin}",
                acceptableRange = "2300K (Warm indoor) – 10000K (Deep shade / twilight)",
                whyRationale = "Locking manual Kelvin prevents color temperature drift across sequential RAW frames.",
                visualAccent = kelvinToColor(preset.wbKelvin)
            ),
            ExpertRawDialStep(
                stepNumber = 6,
                dialName = "Focus & Metering",
                dialLocation = "Expert RAW dial 5 ('FOCUS') & Top Options ('METERING')",
                recommendedValue = "AF / MF (peaking) · Matrix Metering",
                copyableText = "Matrix Metering",
                acceptableRange = "Spot metering for high-contrast backlit subjects",
                whyRationale = "Use manual focus peaking (green fringe) when photographing stars, night skylines, or macro subjects."
            ),
            ExpertRawDialStep(
                stepNumber = 7,
                dialName = "Output Format & Live Aids",
                dialLocation = "Top Settings Gear → 'Save pictures in' & Live View",
                recommendedValue = "16-Bit Linear DNG (RAW) + Grid & Histogram",
                copyableText = "16-bit Linear DNG",
                acceptableRange = "Ensure Expert RAW saves DNG files to DCIM/Expert RAW",
                whyRationale = "Reference only — always check Expert RAW's live histogram monitor and composition grid before pressing shutter."
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppTheme.colors.canvasBg)
            .testTag("expert_raw_companion_screen")
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header: Expert RAW Hand-Off Hero Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("expert_raw_hero_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = AppTheme.colors.cardSurface),
                    border = BorderStroke(1.dp, AppTheme.colors.amberGlow)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(AppTheme.colors.amberGlow),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Tune,
                                        contentDescription = null,
                                        tint = AppTheme.colors.amberPrimary,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column {
                                    Text(
                                        text = "EXPERT RAW COMPANION",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.2.sp,
                                        color = AppTheme.colors.amberPrimary,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Text(
                                        text = "Galaxy S20 Ultra Manual Guide",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = AppTheme.colors.textPrimary
                                    )
                                }
                            }

                            // Quick Launch Icon Button
                            IconButton(
                                onClick = { context.openExpertRawOrStore() },
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(AppTheme.colors.surfaceVariant)
                                    .testTag("btn_quick_launch_expert_raw")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.OpenInNew,
                                    contentDescription = "Open Expert RAW",
                                    tint = AppTheme.colors.amberPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "This app is your manual entry field manual. Choose a scene recipe below, dial the recommended settings into Samsung Expert RAW, and verify exposure with Expert RAW's live histogram.",
                            fontSize = 12.sp,
                            color = AppTheme.colors.textSecondary,
                            lineHeight = 17.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Large Call-To-Action Button
                        Button(
                            onClick = { context.openExpertRawOrStore() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("btn_open_expert_raw_main"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AppTheme.colors.amberPrimary,
                                contentColor = if (AppTheme.colors.isLight) Color.White else Color.Black
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Launch,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Open Samsung Expert RAW",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            // Presets Carousel: Quick Recipe Switching
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ACTIVE RECIPE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppTheme.colors.textMuted,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "${preset.category.title.uppercase()} · #${String.format("%02d", preset.id)}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppTheme.colors.amberPrimary,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PresetRepository.presets.forEach { p ->
                            val isSelected = p.id == preset.id
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(999.dp))
                                    .background(if (isSelected) AppTheme.colors.amberPrimary else AppTheme.colors.surfaceVariant)
                                    .border(
                                        0.5.dp,
                                        if (isSelected) AppTheme.colors.amberPrimary else AppTheme.colors.border,
                                        RoundedCornerShape(999.dp)
                                    )
                                    .clickable {
                                        selectedPresetId = p.id
                                        onSelectPreset(p.id)
                                    }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                                    .testTag("companion_preset_pill_${p.id}")
                            ) {
                                Text(
                                    text = "${String.format("%02d", p.id)} ${p.name}",
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) (if (AppTheme.colors.isLight) Color.White else Color.Black) else AppTheme.colors.textSecondary,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }

            // Recipe Overview Summary Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = AppTheme.colors.cardSurface),
                    border = BorderStroke(0.5.dp, AppTheme.colors.border)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = preset.name,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AppTheme.colors.textPrimary
                                )
                                Text(
                                    text = preset.bestUse,
                                    fontSize = 12.sp,
                                    color = AppTheme.colors.textSecondary
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(AppTheme.colors.surfaceVariant)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "EV ${preset.evValue}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AppTheme.colors.cyanAccent,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(thickness = 0.5.dp, color = AppTheme.colors.border)
                        Spacer(modifier = Modifier.height(10.dp))

                        // Progress indicator of dialed steps
                        val checkedCount = completedSteps.values.count { it }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Manual Dial Progress",
                                fontSize = 11.sp,
                                color = AppTheme.colors.textMuted
                            )
                            Text(
                                text = "$checkedCount of ${steps.size} dials checked",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (checkedCount == steps.size) AppTheme.colors.amberPrimary else AppTheme.colors.textSecondary,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }

            // Dial-by-Dial Step Checklist Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "EXPERT RAW DIAL CHECKLIST",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppTheme.colors.textMuted,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )

                    if (completedSteps.isNotEmpty()) {
                        Text(
                            text = "Reset Dials",
                            fontSize = 11.sp,
                            color = AppTheme.colors.amberPrimary,
                            modifier = Modifier.clickable { completedSteps.clear() }
                        )
                    }
                }
            }

            // Individual Dial Step Cards
            items(steps, key = { it.stepNumber }) { step ->
                val isChecked = completedSteps[step.stepNumber] == true

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { completedSteps[step.stepNumber] = !isChecked }
                        .testTag("dial_step_card_${step.stepNumber}"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isChecked) AppTheme.colors.cardSurface.copy(alpha = 0.6f) else AppTheme.colors.cardSurface
                    ),
                    border = BorderStroke(
                        width = if (isChecked) 0.5.dp else 1.dp,
                        color = if (isChecked) AppTheme.colors.border else AppTheme.colors.border.copy(alpha = 0.8f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Checkbox tracking
                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = { completedSteps[step.stepNumber] = it },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = AppTheme.colors.amberPrimary,
                                    uncheckedColor = AppTheme.colors.textMuted
                                ),
                                modifier = Modifier.size(24.dp)
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            // Step badge & title
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "STEP ${step.stepNumber} · ${step.dialName.uppercase()}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isChecked) AppTheme.colors.textMuted else AppTheme.colors.amberPrimary,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    if (step.visualAccent != null) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .clip(CircleShape)
                                                .background(step.visualAccent)
                                                .border(0.5.dp, Color.White.copy(alpha = 0.5f), CircleShape)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(2.dp))

                                Text(
                                    text = step.recommendedValue,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isChecked) AppTheme.colors.textMuted else AppTheme.colors.textPrimary,
                                    textDecoration = if (isChecked) TextDecoration.LineThrough else TextDecoration.None
                                )
                            }

                            // Copy Value Button
                            IconButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("Expert RAW ${step.dialName}", step.copyableText)
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "Copied '${step.copyableText}' for Expert RAW", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.ContentCopy,
                                    contentDescription = "Copy ${step.dialName}",
                                    tint = AppTheme.colors.textSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Location hint in Expert RAW
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(AppTheme.colors.surfaceVariant)
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "📍 ${step.dialLocation}",
                                fontSize = 11.sp,
                                color = AppTheme.colors.cyanAccent,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Acceptable range and rationale
                        Text(
                            text = "Tolerance: ${step.acceptableRange}",
                            fontSize = 11.sp,
                            color = AppTheme.colors.textSecondary,
                            fontWeight = FontWeight.Medium
                        )

                        Text(
                            text = step.whyRationale,
                            fontSize = 11.sp,
                            color = AppTheme.colors.textMuted,
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            // Bottom Disclaimer Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = AppTheme.colors.surfaceVariant.copy(alpha = 0.5f)),
                    border = BorderStroke(0.5.dp, AppTheme.colors.border)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = null,
                            tint = AppTheme.colors.amberPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Starting point baseline — scene lighting, cloud cover, and subject distance can require manual trimming. Check Expert RAW's live histogram monitor to ensure shadows aren't crushed and spectral highlights aren't clipped.",
                            fontSize = 10.sp,
                            color = AppTheme.colors.textMuted,
                            lineHeight = 14.sp
                        )
                    }
                }
            }
        }
    }
}
