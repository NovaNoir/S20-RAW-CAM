package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.PhotoSizeSelectActual
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.data.CameraPreset
import com.example.data.PresetRepository
import com.example.ui.theme.AppTheme
import com.example.ui.theme.kelvinToColor
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch

/**
 * Samsung Galaxy S20 Ultra Native Camera Viewfinder Aspect Ratios.
 * S20 Ultra has a 20:9 (3200x1440) AMOLED display.
 * The primary sensor (108MP ISOCELL Bright HM1) is natively 4:3 (3:4 in portrait).
 */
enum class S20UltraAspectRatio(
    val label: String,
    val ratioWidth: Float,
    val ratioHeight: Float,
    val subtitle: String
) {
    RATIO_3_4("3:4", 3f, 4f, "108MP Sensor Native"),
    RATIO_9_16("9:16", 9f, 16f, "16:9 Widescreen"),
    RATIO_1_1("1:1", 1f, 1f, "1:1 Square Format"),
    RATIO_FULL("20:9 Full", 9f, 20f, "S20 Ultra 20:9 AMOLED")
}

/**
 * Samsung Galaxy S20 Ultra Physical Rear Lens Array.
 */
enum class S20UltraLens(
    val zoomLabel: String,
    val focalMm: String,
    val aperture: String,
    val sensorSpec: String,
    val scaleFactor: Float
) {
    ULTRA_WIDE(
        zoomLabel = "0.5",
        focalMm = "13mm",
        aperture = "f/2.2",
        sensorSpec = "12MP Ultra-Wide · 120° FOV",
        scaleFactor = 0.75f
    ),
    WIDE_108MP(
        zoomLabel = "1.0",
        focalMm = "24mm",
        aperture = "f/1.8",
        sensorSpec = "108MP ISOCELL HM1 · 1/1.33\" OIS",
        scaleFactor = 1.0f
    ),
    TELE_PERISCOPE(
        zoomLabel = "5.0",
        focalMm = "103mm",
        aperture = "f/3.5",
        sensorSpec = "48MP Periscope Tele · 100x Space Zoom",
        scaleFactor = 1.6f
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraViewfinderScreen(
    initialPreset: CameraPreset? = null,
    modifier: Modifier = Modifier
) {
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    var currentPreset by remember(initialPreset) {
        mutableStateOf(initialPreset ?: PresetRepository.presets.first())
    }

    var selectedAspectRatio by remember { mutableStateOf(S20UltraAspectRatio.RATIO_3_4) }
    var selectedLens by remember { mutableStateOf(S20UltraLens.WIDE_108MP) }

    var showGrid by remember { mutableStateOf(true) }
    var showLevel by remember { mutableStateOf(true) }
    var showHistogram by remember { mutableStateOf(true) }
    var captureFlashAlpha by remember { mutableStateOf(0f) }

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("camera_viewfinder_screen")
    ) {
        // VIEWPORT CONTAINER: Exact S20 Ultra aspect ratio framing
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            val maxW = maxWidth
            val maxH = maxHeight

            // Calculate framed viewport size based on selected S20 Ultra aspect ratio
            val viewportModifier = if (selectedAspectRatio == S20UltraAspectRatio.RATIO_FULL) {
                Modifier.fillMaxSize()
            } else {
                val targetRatio = selectedAspectRatio.ratioWidth / selectedAspectRatio.ratioHeight
                Modifier
                    .fillMaxWidth()
                    .aspectRatio(targetRatio)
            }

            // Sensor Viewport Canvas
            Box(
                modifier = viewportModifier
                    .clipToBounds()
                    .graphicsLayer {
                        scaleX = selectedLens.scaleFactor
                        scaleY = selectedLens.scaleFactor
                    },
                contentAlignment = Alignment.Center
            ) {
                if (cameraPermissionState.status.isGranted) {
                    CameraXPreview(modifier = Modifier.fillMaxSize())
                } else {
                    SimulatedViewfinder(
                        preset = currentPreset,
                        lens = selectedLens,
                        onRequestPermission = { cameraPermissionState.launchPermissionRequest() }
                    )
                }

                // Composition Grid Overlay
                if (showGrid) {
                    RuleOfThirdsGrid(modifier = Modifier.fillMaxSize())
                }

                // Center Artificial Horizon Level
                if (showLevel) {
                    HorizonLevelIndicator(modifier = Modifier.fillMaxSize())
                }
            }
        }

        // Shutter Flash Animation Overlay
        if (captureFlashAlpha > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White.copy(alpha = captureFlashAlpha))
            )
        }

        // OVERLAY CONTROLS (Top Bar + Bottom Bar)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 8.dp, bottom = 4.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // TOP BAR: Telemetry + Notch Clearance + Aspect Ratio & Aids
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left Section: RAW Format & Preset Identity
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .background(AppTheme.colors.amberPrimary)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "RAW 16-BIT",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (AppTheme.colors.isLight) Color.White else Color.Black,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 0.5.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        Text(
                            text = "#${String.format("%02d", currentPreset.id)}",
                            color = AppTheme.colors.amberPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    // S20 Ultra Centered Infinity-O Punch Hole Safe Clearance Gap
                    Spacer(modifier = Modifier.width(28.dp))

                    // Right Section: Aspect Ratio Toggle & Viewfinder Aids
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // S20 Ultra Aspect Ratio Switcher Pill
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .background(AppTheme.colors.cardSurface.copy(alpha = 0.85f))
                                .border(0.5.dp, AppTheme.colors.border, RoundedCornerShape(999.dp))
                                .clickable {
                                    val nextIndex = (selectedAspectRatio.ordinal + 1) % S20UltraAspectRatio.entries.size
                                    selectedAspectRatio = S20UltraAspectRatio.entries[nextIndex]
                                    Toast.makeText(
                                        context,
                                        "Aspect Ratio: ${selectedAspectRatio.label} (${selectedAspectRatio.subtitle})",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .testTag("aspect_ratio_switcher")
                        ) {
                            Text(
                                text = selectedAspectRatio.label,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppTheme.colors.amberPrimary,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        // Grid Toggle
                        IconButton(
                            onClick = { showGrid = !showGrid },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.GridOn,
                                contentDescription = "Toggle Grid",
                                tint = if (showGrid) AppTheme.colors.amberPrimary else AppTheme.colors.textMuted,
                                modifier = Modifier.size(17.dp)
                            )
                        }

                        // Level Toggle
                        IconButton(
                            onClick = { showLevel = !showLevel },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Navigation,
                                contentDescription = "Toggle Level",
                                tint = if (showLevel) AppTheme.colors.amberPrimary else AppTheme.colors.textMuted,
                                modifier = Modifier.size(17.dp)
                            )
                        }

                        // Info / Histogram Toggle
                        IconButton(
                            onClick = { showHistogram = !showHistogram },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Info,
                                contentDescription = "Toggle Info",
                                tint = if (showHistogram) AppTheme.colors.cyanAccent else AppTheme.colors.textMuted,
                                modifier = Modifier.size(17.dp)
                            )
                        }
                    }
                }

                // S20 Ultra Dynamic AMOLED 2X / Sensor Telemetry Banner
                AnimatedVisibility(visible = showHistogram) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(AppTheme.colors.cardSurface.copy(alpha = 0.92f))
                            .border(0.5.dp, AppTheme.colors.border, RoundedCornerShape(14.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "S20 ULTRA • ${selectedLens.sensorSpec}",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppTheme.colors.amberPrimary,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "${currentPreset.name} · ${currentPreset.bestUse}",
                                fontSize = 11.sp,
                                color = AppTheme.colors.textSecondary,
                                maxLines = 1
                            )
                        }

                        MiniHistogram(evValue = currentPreset.evValue)
                    }
                }
            }

            // BOTTOM BAR: Triple Lens Selector + Preset Row + HUD + Shutter
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AppTheme.colors.cardSurface.copy(alpha = 0.95f))
                    .border(BorderStroke(0.5.dp, AppTheme.colors.border), shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .padding(top = 8.dp, bottom = 6.dp)
            ) {
                // S20 Ultra Native Triple-Lens Zoom Selector (0.5x, 1.0x, 5.0x)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(AppTheme.colors.surfaceVariant)
                            .border(0.5.dp, AppTheme.colors.border, RoundedCornerShape(999.dp))
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        S20UltraLens.entries.forEach { lens ->
                            val isLensSelected = selectedLens == lens
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(999.dp))
                                    .background(if (isLensSelected) AppTheme.colors.amberPrimary else Color.Transparent)
                                    .clickable {
                                        selectedLens = lens
                                        Toast.makeText(
                                            context,
                                            "Switched to ${lens.sensorSpec} (${lens.focalMm} ${lens.aperture})",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    .padding(horizontal = 14.dp, vertical = 4.dp)
                                    .testTag("lens_zoom_${lens.zoomLabel.replace(".", "_")}")
                            ) {
                                Text(
                                    text = "${lens.zoomLabel}x",
                                    fontSize = 11.sp,
                                    fontWeight = if (isLensSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isLensSelected) (if (AppTheme.colors.isLight) Color.White else Color.Black) else AppTheme.colors.textSecondary,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Presets Quick Carousel
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PresetRepository.presets.take(28).forEach { preset ->
                        val isSelected = preset.id == currentPreset.id
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .background(if (isSelected) AppTheme.colors.amberPrimary else AppTheme.colors.surfaceVariant)
                                .border(0.5.dp, if (isSelected) AppTheme.colors.amberPrimary else AppTheme.colors.border, RoundedCornerShape(999.dp))
                                .clickable { currentPreset = preset }
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                                .testTag("viewfinder_preset_${preset.id}")
                        ) {
                            Text(
                                text = "${String.format("%02d", preset.id)} ${preset.name}",
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) (if (AppTheme.colors.isLight) Color.White else Color.Black) else AppTheme.colors.textSecondary,
                                maxLines = 1
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Exposure Telemetry Readout
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(AppTheme.colors.surfaceVariant)
                        .border(0.5.dp, AppTheme.colors.border, RoundedCornerShape(14.dp))
                        .padding(vertical = 6.dp, horizontal = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ViewfinderParam(label = "FOCAL", value = selectedLens.focalMm, valueColor = AppTheme.colors.cyanAccent)
                    ViewfinderParam(label = "APER", value = selectedLens.aperture, valueColor = AppTheme.colors.cyanAccent)
                    ViewfinderParam(label = "ISO", value = currentPreset.iso.replace("ISO ", ""), valueColor = AppTheme.colors.amberPrimary)
                    ViewfinderParam(label = "SPEED", value = currentPreset.shutter, valueColor = AppTheme.colors.textPrimary)
                    ViewfinderParam(label = "WB", value = currentPreset.wb, valueColor = kelvinToColor(currentPreset.wbKelvin))
                    ViewfinderParam(label = "EV", value = currentPreset.ev, valueColor = if (currentPreset.ev == "0") AppTheme.colors.textMuted else AppTheme.colors.amberPrimary)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Camera Shutter & Actions Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Aspect ratio readout button
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(AppTheme.colors.surfaceVariant)
                            .border(0.5.dp, AppTheme.colors.border, CircleShape)
                            .clickable {
                                val nextIndex = (selectedAspectRatio.ordinal + 1) % S20UltraAspectRatio.entries.size
                                selectedAspectRatio = S20UltraAspectRatio.entries[nextIndex]
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = selectedAspectRatio.label.replace(" Full", ""),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppTheme.colors.cyanAccent,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    // Main Big Camera Shutter Button (68dp)
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(AppTheme.colors.border)
                            .padding(2.dp)
                            .clip(CircleShape)
                            .background(AppTheme.colors.cardSurface)
                            .padding(4.dp)
                            .clip(CircleShape)
                            .background(AppTheme.colors.amberPrimary)
                            .clickable {
                                coroutineScope.launch {
                                    captureFlashAlpha = 0.85f
                                    val anim = Animatable(0.85f)
                                    anim.animateTo(0f, animationSpec = tween(140))
                                    captureFlashAlpha = 0f
                                }
                                Toast.makeText(
                                    context,
                                    "Simulated S20 Ultra RAW (16-Bit DNG): ${selectedLens.focalMm} ${selectedLens.aperture}, ${currentPreset.shutter}, ${currentPreset.iso}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            .testTag("camera_shutter_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CameraAlt,
                            contentDescription = "Capture Test Frame",
                            tint = if (AppTheme.colors.isLight) Color.White else Color.Black,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    // Info button for preset sensor tips
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(AppTheme.colors.surfaceVariant)
                            .border(0.5.dp, AppTheme.colors.border, CircleShape)
                            .clickable {
                                Toast.makeText(context, currentPreset.tips, Toast.LENGTH_LONG).show()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = "Tips",
                            tint = AppTheme.colors.amberPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ViewfinderParam(label: String, value: String, valueColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = AppTheme.colors.textMuted,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 0.5.sp
        )
        Spacer(modifier = Modifier.height(1.dp))
        Text(
            text = value,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = valueColor,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
private fun RuleOfThirdsGrid(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val gridColor = Color.White.copy(alpha = 0.25f)
        val stroke = 1.dp.toPx()

        // Vertical lines
        drawLine(gridColor, Offset(w / 3f, 0f), Offset(w / 3f, h), stroke)
        drawLine(gridColor, Offset(2 * w / 3f, 0f), Offset(2 * w / 3f, h), stroke)

        // Horizontal lines
        drawLine(gridColor, Offset(0f, h / 3f), Offset(w, h / 3f), stroke)
        drawLine(gridColor, Offset(0f, 2 * h / 3f), Offset(w, 2 * h / 3f), stroke)
    }
}

@Composable
private fun HorizonLevelIndicator(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val center = Offset(w / 2f, h / 2f)
        val levelColor = Color(0xFFFFB300).copy(alpha = 0.75f)
        val stroke = 1.5.dp.toPx()

        // Center reticle
        drawCircle(
            color = levelColor,
            radius = 24.dp.toPx(),
            center = center,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = stroke)
        )
        // Center crosshair tick
        drawLine(levelColor, Offset(center.x - 36.dp.toPx(), center.y), Offset(center.x - 12.dp.toPx(), center.y), stroke)
        drawLine(levelColor, Offset(center.x + 12.dp.toPx(), center.y), Offset(center.x + 36.dp.toPx(), center.y), stroke)
    }
}

@Composable
private fun MiniHistogram(evValue: Float) {
    Canvas(modifier = Modifier.size(width = 44.dp, height = 22.dp)) {
        val w = size.width
        val h = size.height
        val barCount = 12
        val barWidth = w / barCount

        for (i in 0 until barCount) {
            val normalizedX = i.toFloat() / barCount
            val peak = 0.5f + (evValue * 0.15f)
            val diff = kotlin.math.abs(normalizedX - peak)
            val barHeight = (1f - (diff * 1.6f)).coerceIn(0.15f, 0.95f) * h

            drawRect(
                color = Color(0xFF00E5FF).copy(alpha = 0.8f),
                topLeft = Offset(i * barWidth, h - barHeight),
                size = androidx.compose.ui.geometry.Size(barWidth - 1.dp.toPx(), barHeight)
            )
        }
    }
}

@Composable
private fun SimulatedViewfinder(
    preset: CameraPreset,
    lens: S20UltraLens,
    onRequestPermission: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0D12)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(AppTheme.colors.surfaceVariant)
                    .border(0.5.dp, AppTheme.colors.border, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.CameraAlt,
                    contentDescription = null,
                    tint = AppTheme.colors.amberPrimary,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Live Viewfinder Simulation",
                fontFamily = FontFamily.Serif,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Light,
                fontSize = 20.sp,
                color = AppTheme.colors.textPrimary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Simulating ${lens.sensorSpec} (${lens.focalMm} ${lens.aperture}). Grant camera permission to project live sensor feed.",
                style = MaterialTheme.typography.bodySmall,
                color = AppTheme.colors.textSecondary,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onRequestPermission,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppTheme.colors.amberPrimary,
                    contentColor = if (AppTheme.colors.isLight) Color.White else Color.Black
                ),
                shape = RoundedCornerShape(999.dp),
                modifier = Modifier.testTag("grant_camera_permission_btn")
            ) {
                Text("Enable Camera Feed", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun CameraXPreview(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            val previewView = PreviewView(ctx)
            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

            cameraProviderFuture.addListener({
                try {
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }
                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, ContextCompat.getMainExecutor(ctx))

            previewView
        }
    )
}
