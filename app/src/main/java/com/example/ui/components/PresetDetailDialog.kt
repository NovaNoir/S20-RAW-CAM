package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.CameraPreset
import com.example.ui.theme.AppTheme
import com.example.ui.theme.kelvinToColor

@Composable
fun PresetDetailDialog(
    preset: CameraPreset,
    isFavorite: Boolean,
    noteText: String,
    onToggleFavorite: () -> Unit,
    onSaveNote: (String) -> Unit,
    onOpenInCamera: (CameraPreset) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var editingNote by remember(preset.id, noteText) { mutableStateOf(noteText) }
    var isEditingNoteMode by remember(preset.id) { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .testTag("preset_detail_dialog")
                .clip(RoundedCornerShape(32.dp))
                .border(0.5.dp, AppTheme.colors.border, RoundedCornerShape(32.dp)),
            color = AppTheme.colors.cardSurface
        ) {
            Column(
                modifier = Modifier
                    .padding(22.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Top Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .background(AppTheme.colors.amberGlow)
                                .border(0.5.dp, AppTheme.colors.amberPrimary.copy(alpha = 0.4f), RoundedCornerShape(999.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = String.format("#%03d", preset.id),
                                color = AppTheme.colors.amberPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 0.5.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = preset.name,
                                fontFamily = FontFamily.Serif,
                                fontStyle = FontStyle.Italic,
                                fontWeight = FontWeight.Light,
                                fontSize = 20.sp,
                                color = AppTheme.colors.textPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${preset.variantCode} — ${preset.variant}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = AppTheme.colors.amberPrimary,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Row {
                        IconButton(
                            onClick = onToggleFavorite,
                            modifier = Modifier.testTag("dialog_fav_btn")
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                contentDescription = "Favorite",
                                tint = if (isFavorite) AppTheme.colors.amberPrimary else AppTheme.colors.textMuted
                            )
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Close",
                                tint = AppTheme.colors.textSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Big Camera Settings HUD Grid
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = AppTheme.colors.surfaceVariant),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(0.5.dp, AppTheme.colors.border)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "EXPERT RAW PARAMETERS",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppTheme.colors.textMuted,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.2.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            DetailHudCell(title = "LENS", value = preset.lens, valueColor = AppTheme.colors.cyanAccent)
                            DetailHudCell(title = "ISO", value = preset.iso, valueColor = AppTheme.colors.amberPrimary)
                            DetailHudCell(title = "SHUTTER", value = preset.shutter, valueColor = AppTheme.colors.textPrimary)
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = AppTheme.colors.border, thickness = 0.5.dp)
                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            DetailWbCell(title = "WHITE BALANCE", value = preset.wb, kelvin = preset.wbKelvin)
                            DetailHudCell(title = "FOCUS", value = preset.focus, valueColor = AppTheme.colors.greenAccent)
                            DetailHudCell(
                                title = "EV BIAS",
                                value = preset.ev,
                                valueColor = if (preset.ev == "0") AppTheme.colors.textMuted else AppTheme.colors.amberPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = AppTheme.colors.border, thickness = 0.5.dp)
                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            DetailHudCell(title = "METERING", value = preset.meter, valueColor = AppTheme.colors.textPrimary)
                            DetailHudCell(title = "VARIANT", value = preset.variantCode, valueColor = AppTheme.colors.amberPrimary)
                            DetailHudCell(title = "CATEGORY", value = preset.category.title.takeWhile { it != ' ' }, valueColor = AppTheme.colors.cyanAccent)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Best Use Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = AppTheme.colors.surfaceVariant),
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(0.5.dp, AppTheme.colors.border)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Info,
                                contentDescription = null,
                                tint = AppTheme.colors.cyanAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "BEST USE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppTheme.colors.cyanAccent,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 1.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = preset.bestUse,
                            style = MaterialTheme.typography.bodyMedium,
                            color = AppTheme.colors.textPrimary
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = preset.tips,
                            style = MaterialTheme.typography.bodySmall,
                            color = AppTheme.colors.textSecondary,
                            lineHeight = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Step by step setup in Samsung Expert RAW
                Text(
                    text = "HOW TO APPLY IN EXPERT RAW",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppTheme.colors.textMuted,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.2.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                val steps = listOf(
                    "1. Open Expert RAW and switch to the ${preset.lens} lens button.",
                    "2. Set ISO to ${preset.iso} (Main sensor base ISO 50 is best).",
                    "3. Set Shutter speed to ${preset.shutter}.",
                    "4. Set White Balance (WB) to ${preset.wb}.",
                    "5. Set EV bias to ${preset.ev} and Focus to ${preset.focus}.",
                    "6. Take a test frame and inspect histogram highlights."
                )

                steps.forEach { step ->
                    Text(
                        text = step,
                        style = MaterialTheme.typography.bodySmall,
                        color = AppTheme.colors.textSecondary,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Field Notes section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.EditNote,
                            contentDescription = null,
                            tint = AppTheme.colors.amberPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "MY FIELD NOTES",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppTheme.colors.amberPrimary,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                    }

                    if (!isEditingNoteMode && editingNote.isNotEmpty()) {
                        Text(
                            text = "Edit",
                            color = AppTheme.colors.cyanAccent,
                            fontSize = 12.sp,
                            modifier = Modifier
                                .clickable { isEditingNoteMode = true }
                                .padding(4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                if (isEditingNoteMode || editingNote.isEmpty()) {
                    OutlinedTextField(
                        value = editingNote,
                        onValueChange = { editingNote = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("note_input_field"),
                        placeholder = {
                            Text(
                                "Add personal shooting notes, location, or tweaks...",
                                color = AppTheme.colors.textMuted,
                                fontSize = 12.sp
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AppTheme.colors.amberPrimary,
                            unfocusedBorderColor = AppTheme.colors.border,
                            focusedTextColor = AppTheme.colors.textPrimary,
                            unfocusedTextColor = AppTheme.colors.textPrimary,
                            focusedContainerColor = AppTheme.colors.surfaceVariant,
                            unfocusedContainerColor = AppTheme.colors.surfaceVariant
                        ),
                        shape = RoundedCornerShape(14.dp),
                        maxLines = 3
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            onSaveNote(editingNote)
                            isEditingNoteMode = false
                            Toast.makeText(context, "Note saved", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AppTheme.colors.amberPrimary,
                            contentColor = if (AppTheme.colors.isLight) Color.White else Color.Black
                        ),
                        shape = RoundedCornerShape(999.dp),
                        modifier = Modifier
                            .align(Alignment.End)
                            .testTag("save_note_btn")
                    ) {
                        Text("Save Note", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                } else {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isEditingNoteMode = true },
                        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.surfaceVariant),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(0.5.dp, AppTheme.colors.border)
                    ) {
                        Text(
                            text = editingNote,
                            style = MaterialTheme.typography.bodySmall,
                            color = AppTheme.colors.textPrimary,
                            modifier = Modifier.padding(14.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Bottom Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText(
                                "Camera Preset",
                                "Preset #${preset.id} ${preset.name}\nLens: ${preset.lens} | ISO: ${preset.iso} | Shutter: ${preset.shutter} | WB: ${preset.wb} | EV: ${preset.ev} | Focus: ${preset.focus}\nBest use: ${preset.bestUse}"
                            )
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Settings copied to clipboard", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("copy_settings_btn"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = AppTheme.colors.textPrimary),
                        border = BorderStroke(0.5.dp, AppTheme.colors.border)
                    ) {
                        Icon(Icons.Filled.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Copy", fontSize = 13.sp)
                    }

                    Button(
                        onClick = {
                            onOpenInCamera(preset)
                            onDismiss()
                        },
                        modifier = Modifier
                            .weight(1.3f)
                            .testTag("open_in_camera_btn"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AppTheme.colors.amberPrimary,
                            contentColor = if (AppTheme.colors.isLight) Color.White else Color.Black
                        )
                    ) {
                        Icon(Icons.Filled.Tune, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Companion Guide", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailHudCell(title: String, value: String, valueColor: Color) {
    Column(horizontalAlignment = Alignment.Start) {
        Text(
            text = title,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = AppTheme.colors.textMuted,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 0.5.sp
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = valueColor,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
private fun DetailWbCell(title: String, value: String, kelvin: Int) {
    Column(horizontalAlignment = Alignment.Start) {
        Text(
            text = title,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = AppTheme.colors.textMuted,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 0.5.sp
        )
        Spacer(modifier = Modifier.height(2.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(9.dp)
                    .clip(CircleShape)
                    .background(kelvinToColor(kelvin))
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = value,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = AppTheme.colors.textPrimary,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}
