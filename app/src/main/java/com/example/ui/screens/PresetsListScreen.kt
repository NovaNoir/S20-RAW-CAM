package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.NorthWest
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Tune
import com.example.data.CameraPreset
import com.example.data.PresetCategory
import com.example.data.SceneFamilyInfo
import com.example.ui.PresetViewModel
import androidx.compose.ui.platform.LocalContext
import com.example.ui.components.LogViewerDialog
import com.example.ui.components.PresetCard
import com.example.ui.components.PresetDetailDialog
import com.example.ui.components.SceneFamiliesBrowser
import com.example.ui.components.ThemeSettingsDialog
import com.example.ui.theme.AppTheme
import com.example.ui.theme.AppThemeMode

@Composable
fun PresetsListScreen(
    viewModel: PresetViewModel,
    onOpenInCamera: (CameraPreset) -> Unit,
    themeMode: AppThemeMode = AppThemeMode.SOPHISTICATED_DARK,
    onToggleTheme: () -> Unit = {},
    onSelectThemeMode: (AppThemeMode) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedPresetForDetail by remember { mutableStateOf<CameraPreset?>(null) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var showLogDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppTheme.colors.canvasBg)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Screen Header: Title + Telemetry readout + Theme Switch
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "S20 ULTRA • EXPERT RAW",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppTheme.colors.amberPrimary,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.8.sp
                        )
                        Text(
                            text = "1,008-Preset Master",
                            fontFamily = FontFamily.Serif,
                            fontStyle = FontStyle.Italic,
                            fontWeight = FontWeight.Light,
                            fontSize = 28.sp,
                            color = AppTheme.colors.textPrimary,
                            letterSpacing = (-0.5).sp
                        )
                    }

                    // Header Controls: Quick Theme Toggle, Theme Menu, & Count Badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Quick Theme Toggle Pill
                        Surface(
                            onClick = onToggleTheme,
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .border(0.5.dp, AppTheme.colors.border, RoundedCornerShape(999.dp))
                                .testTag("theme_settings_switch"),
                            color = AppTheme.colors.surfaceVariant,
                            shape = RoundedCornerShape(999.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = if (themeMode.isLight) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                                    contentDescription = "Theme Switch",
                                    tint = AppTheme.colors.amberPrimary,
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = themeMode.shortLabel.uppercase(),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AppTheme.colors.textPrimary,
                                    fontFamily = FontFamily.Monospace,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }

                        // Theme Options Settings Button
                        IconButton(
                            onClick = { showThemeDialog = true },
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(AppTheme.colors.surfaceVariant)
                                .border(0.5.dp, AppTheme.colors.border, CircleShape)
                                .testTag("theme_settings_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Tune,
                                contentDescription = "Theme Settings",
                                tint = AppTheme.colors.textSecondary,
                                modifier = Modifier.size(15.dp)
                            )
                        }

                        // More Options Menu (Export Error Logs, Diagnostics)
                        Box {
                            IconButton(
                                onClick = { showMenu = true },
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .background(AppTheme.colors.surfaceVariant)
                                    .border(0.5.dp, AppTheme.colors.border, CircleShape)
                                    .testTag("menu_more_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.MoreVert,
                                    contentDescription = "Options Menu",
                                    tint = AppTheme.colors.textSecondary,
                                    modifier = Modifier.size(15.dp)
                                )
                            }

                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Export Error Logs", fontSize = 13.sp) },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Filled.Share,
                                            contentDescription = null,
                                            tint = AppTheme.colors.amberPrimary,
                                            modifier = Modifier.size(17.dp)
                                        )
                                    },
                                    onClick = {
                                        showMenu = false
                                        com.example.util.ErrorLogger.exportLogs(context)
                                    },
                                    modifier = Modifier.testTag("menu_export_logs")
                                )
                                DropdownMenuItem(
                                    text = { Text("Diagnostic Logs", fontSize = 13.sp) },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Filled.BugReport,
                                            contentDescription = null,
                                            tint = AppTheme.colors.cyanAccent,
                                            modifier = Modifier.size(17.dp)
                                        )
                                    },
                                    onClick = {
                                        showMenu = false
                                        showLogDialog = true
                                    },
                                    modifier = Modifier.testTag("menu_view_diagnostics")
                                )
                            }
                        }

                        // Count Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .background(AppTheme.colors.cardSurface)
                                .border(0.5.dp, AppTheme.colors.border, RoundedCornerShape(999.dp))
                                .padding(horizontal = 9.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${uiState.presets.size}/${uiState.totalCount}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppTheme.colors.cyanAccent,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Search Bar
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("preset_search_input"),
                    placeholder = {
                        Text(
                            uiState.predictiveHint?.let { "Suggested: $it..." }
                                ?: "Search preset, scene family, lens, ISO, variant...",
                            color = if (uiState.predictiveHint != null) AppTheme.colors.amberPrimary.copy(alpha = 0.7f) else AppTheme.colors.textMuted,
                            fontSize = 13.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = if (uiState.searchQuery.isNotEmpty() && uiState.topPredictiveFamily != null) Icons.Filled.AutoAwesome else Icons.Filled.Search,
                            contentDescription = "Search",
                            tint = if (uiState.searchQuery.isNotEmpty() && uiState.topPredictiveFamily != null) AppTheme.colors.amberPrimary else AppTheme.colors.textMuted
                        )
                    },
                    trailingIcon = {
                        if (uiState.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(Icons.Filled.Clear, contentDescription = "Clear search", tint = AppTheme.colors.textMuted)
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AppTheme.colors.amberPrimary,
                        unfocusedBorderColor = AppTheme.colors.border,
                        focusedTextColor = AppTheme.colors.textPrimary,
                        unfocusedTextColor = AppTheme.colors.textPrimary,
                        focusedContainerColor = AppTheme.colors.cardSurface,
                        unfocusedContainerColor = AppTheme.colors.cardSurface
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Active Family Scoping Indicator (If filtered to a single scene family)
                AnimatedVisibility(
                    visible = uiState.selectedFamilyId != null,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = AppTheme.colors.cardSurface,
                        border = BorderStroke(0.5.dp, AppTheme.colors.amberPrimary.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Icon(
                                    imageVector = Icons.Filled.FilterAlt,
                                    contentDescription = null,
                                    tint = AppTheme.colors.amberPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "FILTERED TO SCENE FAMILY #${String.format("%02d", uiState.selectedFamilyId ?: 0)}",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AppTheme.colors.amberPrimary,
                                        fontFamily = FontFamily.Monospace,
                                        letterSpacing = 1.sp
                                    )
                                    Text(
                                        text = uiState.activeFamilyName ?: "Scene Family",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = AppTheme.colors.textPrimary
                                    )
                                }
                            }
                            TextButton(
                                onClick = { viewModel.clearFamilyScope() },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.testTag("clear_family_scope_btn")
                            ) {
                                Text("View All 1,008", color = AppTheme.colors.cyanAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Predictive Match Card (Appears dynamically as user types when a top match is found)
                AnimatedVisibility(
                    visible = uiState.searchQuery.isNotBlank() && uiState.topPredictiveFamily != null,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    val topMatch = uiState.topPredictiveFamily
                    if (topMatch != null) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                                .testTag("predictive_match_card")
                                .clickable { viewModel.applyFamilySuggestion(topMatch.id) },
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = AppTheme.colors.cardSurface),
                            border = BorderStroke(1.dp, AppTheme.colors.amberPrimary.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                    Box(
                                        modifier = Modifier
                                            .size(30.dp)
                                            .clip(CircleShape)
                                            .background(AppTheme.colors.amberGlow)
                                            .border(0.5.dp, AppTheme.colors.amberPrimary, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.AutoAwesome,
                                            contentDescription = null,
                                            tint = AppTheme.colors.amberPrimary,
                                            modifier = Modifier.size(15.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "PREDICTIVE MATCH • #${String.format("%02d", topMatch.id)} ${topMatch.name}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = AppTheme.colors.textPrimary
                                        )
                                        Text(
                                            text = "${topMatch.category.title} • 18 Presets",
                                            fontSize = 10.sp,
                                            color = AppTheme.colors.amberPrimary,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    IconButton(
                                        onClick = { viewModel.autocompleteSearch(topMatch.name) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.NorthWest,
                                            contentDescription = "Autofill query",
                                            tint = AppTheme.colors.cyanAccent,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                    Button(
                                        onClick = { viewModel.applyFamilySuggestion(topMatch.id) },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = AppTheme.colors.amberPrimary,
                                            contentColor = if (themeMode.isLight) Color.White else Color.Black
                                        ),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                        shape = RoundedCornerShape(999.dp),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Text("Apply", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                // Predictive Category Suggestion Chips (Matches categories as user types)
                AnimatedVisibility(
                    visible = uiState.suggestedCategories.isNotEmpty(),
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp)
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "CATEGORIES:",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppTheme.colors.cyanAccent,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 0.8.sp
                        )
                        uiState.suggestedCategories.forEach { category ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(999.dp))
                                    .background(AppTheme.colors.cardSurface)
                                    .border(0.5.dp, AppTheme.colors.cyanAccent.copy(alpha = 0.6f), RoundedCornerShape(999.dp))
                                    .clickable { viewModel.applyCategorySuggestion(category) }
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                                    .testTag("suggested_category_chip_${category.name}")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Filled.Category,
                                        contentDescription = null,
                                        tint = AppTheme.colors.cyanAccent,
                                        modifier = Modifier.size(11.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = category.title,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = AppTheme.colors.cyanAccent
                                    )
                                }
                            }
                        }
                    }
                }

                // Scene Families Predictive Suggestion Chips (Suggested families as user types, or popular when blank)
                Column(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (uiState.searchQuery.isNotBlank()) "SUGGESTED FAMILIES (${uiState.suggestedFamilies.size})" else "QUICK JUMP FAMILIES",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (uiState.searchQuery.isNotBlank()) AppTheme.colors.amberPrimary else AppTheme.colors.textMuted,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        uiState.suggestedFamilies.forEach { family ->
                            val isCurrentFamily = uiState.selectedFamilyId == family.id
                            SceneFamilySuggestionChip(
                                family = family,
                                isSelected = isCurrentFamily,
                                onSelect = { viewModel.applyFamilySuggestion(family.id) },
                                onAutocomplete = { viewModel.autocompleteSearch(family.name) }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Category Chips and Favorites Filter
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Favorites Toggle Chip ("Starred")
                    val favActive = uiState.showOnlyFavorites
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(if (favActive) AppTheme.colors.amberPrimary else AppTheme.colors.cardSurface)
                            .border(0.5.dp, if (favActive) AppTheme.colors.amberPrimary else AppTheme.colors.border, RoundedCornerShape(999.dp))
                            .clickable { viewModel.toggleShowFavorites() }
                            .padding(horizontal = 14.dp, vertical = 7.dp)
                            .testTag("favorites_filter_chip")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (favActive) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                contentDescription = null,
                                tint = if (favActive) (if (themeMode.isLight) Color.White else Color.Black) else AppTheme.colors.amberPrimary,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "Starred (${uiState.favoriteIds.size})",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (favActive) (if (themeMode.isLight) Color.White else Color.Black) else AppTheme.colors.textPrimary
                            )
                        }
                    }

                    // Category Chips
                    PresetCategory.entries.forEach { category ->
                        val isSelected = (uiState.selectedCategory == category && !uiState.showOnlyFavorites)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .background(if (isSelected) AppTheme.colors.amberPrimary else AppTheme.colors.cardSurface)
                                .border(0.5.dp, if (isSelected) AppTheme.colors.amberPrimary else AppTheme.colors.border, RoundedCornerShape(999.dp))
                            .clickable {
                                viewModel.setCategory(category)
                                if (uiState.showOnlyFavorites) {
                                    viewModel.toggleShowFavorites()
                                }
                            }
                            .padding(horizontal = 14.dp, vertical = 7.dp)
                            .testTag("category_chip_${category.name}")
                        ) {
                            Text(
                                text = category.title,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) (if (themeMode.isLight) Color.White else Color.Black) else AppTheme.colors.textSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Control Variant Selector Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val variantOptions = listOf(
                        null to "All Variants",
                        "N" to "N · Normal",
                        "HL" to "HL · Highlight (-0.3)",
                        "DR" to "DR · Dyn Range (-0.7)",
                        "SP" to "SP · Spot",
                        "CT" to "CT · Center (-0.3)",
                        "MF" to "MF · Manual Focus"
                    )

                    variantOptions.forEach { (code, label) ->
                        val isVarSelected = (uiState.selectedVariantCode == code)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .background(if (isVarSelected) AppTheme.colors.cyanAccent.copy(alpha = 0.15f) else AppTheme.colors.cardSurface)
                                .border(0.5.dp, if (isVarSelected) AppTheme.colors.cyanAccent else AppTheme.colors.border, RoundedCornerShape(999.dp))
                                .clickable { viewModel.setVariantCode(code) }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                                .testTag("variant_chip_${code ?: "all"}")
                        ) {
                            Text(
                                text = label,
                                fontSize = 10.sp,
                                fontWeight = if (isVarSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isVarSelected) AppTheme.colors.cyanAccent else AppTheme.colors.textSecondary,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Smooth 56 Scene Families Browser with hover and transitions
            SceneFamiliesBrowser(
                families = viewModel.sceneFamilies,
                selectedFamilyId = uiState.selectedFamilyId,
                onSelectFamily = { viewModel.selectFamily(it) },
                onPreviousFamily = { viewModel.previousFamily() },
                onNextFamily = { viewModel.nextFamily() }
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Presets List or Loading / Empty States
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = AppTheme.colors.amberPrimary, modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Loading 1,008 presets from Room DB...",
                            color = AppTheme.colors.textMuted,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp
                        )
                    }
                }
            } else if (uiState.presets.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Filled.FilterList,
                            contentDescription = null,
                            tint = AppTheme.colors.textMuted,
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (uiState.showOnlyFavorites) "No favorite presets saved yet" else "No presets match your filters",
                            color = AppTheme.colors.textPrimary,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (uiState.showOnlyFavorites) "Tap the star on any preset to save it here for quick access" else "Try clearing search or choosing another category",
                            color = AppTheme.colors.textMuted,
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.resetFilters() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AppTheme.colors.surfaceVariant,
                                contentColor = AppTheme.colors.amberPrimary
                            ),
                            shape = RoundedCornerShape(999.dp),
                            modifier = Modifier.border(0.5.dp, AppTheme.colors.border, RoundedCornerShape(999.dp))
                        ) {
                            Icon(Icons.Filled.RestartAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Reset All Filters", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("presets_lazy_list"),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(uiState.presets, key = { it.id }) { preset ->
                        val isFav = uiState.favoriteIds.contains(preset.id)
                        PresetCard(
                            preset = preset,
                            isFavorite = isFav,
                            onToggleFavorite = { viewModel.toggleFavorite(preset.id, isFav) },
                            onClick = { selectedPresetForDetail = preset }
                        )
                    }
                }
            }
        }

        // Preset Detail Dialog
        selectedPresetForDetail?.let { preset ->
            val isFav = uiState.favoriteIds.contains(preset.id)
            PresetDetailDialog(
                preset = preset,
                isFavorite = isFav,
                noteText = uiState.notesMap[preset.id] ?: "",
                onToggleFavorite = { viewModel.toggleFavorite(preset.id, isFav) },
                onSaveNote = { viewModel.saveNote(preset.id, it) },
                onOpenInCamera = {
                    onOpenInCamera(it)
                    selectedPresetForDetail = null
                },
                onDismiss = { selectedPresetForDetail = null }
            )
        }

        // Theme Settings Dialog
        if (showThemeDialog) {
            ThemeSettingsDialog(
                currentMode = themeMode,
                onSelectMode = { mode ->
                    onSelectThemeMode(mode)
                },
                onDismiss = { showThemeDialog = false }
            )
        }

        // Diagnostic Error Log Viewer Dialog
        if (showLogDialog) {
            LogViewerDialog(
                onDismiss = { showLogDialog = false }
            )
        }
    }
}

@Composable
private fun SceneFamilySuggestionChip(
    family: SceneFamilyInfo,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onAutocomplete: () -> Unit
) {
    Surface(
        modifier = Modifier
            .testTag("suggested_family_chip_${family.id}")
            .clip(RoundedCornerShape(12.dp))
            .clickable { onSelect() },
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) AppTheme.colors.amberGlow else AppTheme.colors.cardSurface,
        border = BorderStroke(0.5.dp, if (isSelected) AppTheme.colors.amberPrimary else AppTheme.colors.border)
    ) {
        Row(
            modifier = Modifier.padding(start = 8.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (isSelected) AppTheme.colors.amberPrimary else AppTheme.colors.surfaceVariant)
                    .padding(horizontal = 5.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "#${String.format("%02d", family.id)}",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color.Black else AppTheme.colors.amberHighlight,
                    fontFamily = FontFamily.Monospace
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(
                    text = family.name,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) AppTheme.colors.amberPrimary else AppTheme.colors.textPrimary
                )
                Text(
                    text = "${family.category.title} • 18p",
                    fontSize = 9.sp,
                    color = AppTheme.colors.textMuted,
                    fontFamily = FontFamily.Monospace
                )
            }
            Spacer(modifier = Modifier.width(2.dp))
            IconButton(
                onClick = onAutocomplete,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.NorthWest,
                    contentDescription = "Autofill ${family.name}",
                    tint = AppTheme.colors.cyanAccent,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}
