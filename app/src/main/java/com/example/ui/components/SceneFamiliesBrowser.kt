package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PresetCategory
import com.example.data.SceneFamilyInfo
import com.example.ui.theme.AppTheme
import kotlinx.coroutines.launch

@Composable
fun SceneFamiliesBrowser(
    families: List<SceneFamilyInfo>,
    selectedFamilyId: Int?,
    onSelectFamily: (Int?) -> Unit,
    onPreviousFamily: () -> Unit,
    onNextFamily: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Scroll to selected family when it changes
    LaunchedEffect(selectedFamilyId) {
        if (selectedFamilyId != null && selectedFamilyId in 1..families.size) {
            coroutineScope.launch {
                listState.animateScrollToItem((selectedFamilyId - 1).coerceAtLeast(0))
            }
        }
    }

    val selectedFamily = remember(selectedFamilyId, families) {
        families.find { it.id == selectedFamilyId }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("scene_families_browser")
    ) {
        // Bar Header: Section Title + Navigation Stepper
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Collections,
                    contentDescription = null,
                    tint = AppTheme.colors.amberPrimary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "56 SCENE FAMILIES",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = AppTheme.colors.amberPrimary,
                    letterSpacing = 1.4.sp
                )
                if (selectedFamilyId != null) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "· #${String.format("%02d", selectedFamilyId)}/56",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = AppTheme.colors.cyanAccent
                    )
                }
            }

            // Stepper controls
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (selectedFamilyId != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(AppTheme.colors.surfaceVariant)
                            .border(0.5.dp, AppTheme.colors.border, RoundedCornerShape(999.dp))
                            .clickable { onSelectFamily(null) }
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                            .testTag("clear_family_filter")
                    ) {
                        Text(
                            text = "ALL (56)",
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = AppTheme.colors.textSecondary
                        )
                    }
                }

                IconButton(
                    onClick = onPreviousFamily,
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(AppTheme.colors.surfaceVariant)
                        .border(0.5.dp, AppTheme.colors.border, CircleShape)
                        .testTag("prev_family_btn")
                ) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowLeft,
                        contentDescription = "Previous Family",
                        tint = if (selectedFamilyId != null) AppTheme.colors.amberPrimary else AppTheme.colors.textMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }

                IconButton(
                    onClick = onNextFamily,
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(AppTheme.colors.surfaceVariant)
                        .border(0.5.dp, AppTheme.colors.border, CircleShape)
                        .testTag("next_family_btn")
                ) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowRight,
                        contentDescription = "Next Family",
                        tint = if (selectedFamilyId != null) AppTheme.colors.amberPrimary else AppTheme.colors.textMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Horizontal Carousel of the 56 Families with Hover & Smooth Transitions
        LazyRow(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("scene_families_carousel"),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(families, key = { it.id }) { family ->
                val isSelected = (selectedFamilyId == family.id)
                SceneFamilyCardItem(
                    family = family,
                    isSelected = isSelected,
                    onClick = {
                        if (isSelected) {
                            onSelectFamily(null)
                        } else {
                            onSelectFamily(family.id)
                        }
                    }
                )
            }
        }

        // Animated Info Banner for Selected Scene Family
        AnimatedVisibility(
            visible = selectedFamily != null,
            enter = fadeIn(animationSpec = tween(250)) + expandVertically(animationSpec = spring(stiffness = Spring.StiffnessMediumLow)),
            exit = fadeOut(animationSpec = tween(180)) + shrinkVertically(animationSpec = tween(180))
        ) {
            selectedFamily?.let { fam ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(AppTheme.colors.cardSurface)
                        .border(0.5.dp, AppTheme.colors.amberPrimary.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                        .padding(12.dp)
                        .testTag("selected_family_banner")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(999.dp))
                                        .background(AppTheme.colors.amberGlow)
                                        .border(0.5.dp, AppTheme.colors.amberPrimary, RoundedCornerShape(999.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "FAMILY #${String.format("%02d", fam.id)} OF 56",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        color = AppTheme.colors.amberHighlight
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(999.dp))
                                        .background(AppTheme.colors.surfaceVariant)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "18 Presets",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        color = AppTheme.colors.cyanAccent
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = fam.name,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Serif,
                                color = AppTheme.colors.textPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = fam.bestUse,
                                fontSize = 11.sp,
                                color = AppTheme.colors.textSecondary,
                                maxLines = 2
                            )
                        }

                        IconButton(
                            onClick = { onSelectFamily(null) },
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(AppTheme.colors.surfaceVariant)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Close Family Banner",
                                tint = AppTheme.colors.textSecondary,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SceneFamilyCardItem(
    family: SceneFamilyInfo,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isPressed by interactionSource.collectIsPressedAsState()

    // Smooth hover & interaction animation states
    val scale by animateFloatAsState(
        targetValue = when {
            isPressed -> 0.96f
            isHovered || isSelected -> 1.04f
            else -> 1.0f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "scaleAnim"
    )

    val currentBorder = AppTheme.colors.border
    val currentAmber = AppTheme.colors.amberPrimary
    val currentCyan = AppTheme.colors.cyanAccent
    val currentAmberGlow = AppTheme.colors.amberGlow
    val currentSurfaceVariant = AppTheme.colors.surfaceVariant
    val currentCardSurface = AppTheme.colors.cardSurface
    val currentTextPrimary = AppTheme.colors.textPrimary
    val currentTextSecondary = AppTheme.colors.textSecondary
    val currentTextMuted = AppTheme.colors.textMuted

    val borderColor by animateColorAsState(
        targetValue = when {
            isSelected -> currentAmber
            isHovered -> currentCyan
            else -> currentBorder
        },
        animationSpec = tween(durationMillis = 200),
        label = "borderColorAnim"
    )

    val backgroundColor by animateColorAsState(
        targetValue = when {
            isSelected -> currentAmberGlow
            isHovered -> currentSurfaceVariant
            else -> currentCardSurface
        },
        animationSpec = tween(durationMillis = 200),
        label = "backgroundColorAnim"
    )

    val textColor by animateColorAsState(
        targetValue = when {
            isSelected -> currentTextPrimary
            isHovered -> currentTextSecondary
            else -> currentTextMuted
        },
        animationSpec = tween(durationMillis = 200),
        label = "textColorAnim"
    )

    Box(
        modifier = Modifier
            .scale(scale)
            .clip(RoundedCornerShape(14.dp))
            .background(backgroundColor)
            .border(width = if (isSelected) 1.dp else 0.5.dp, color = borderColor, shape = RoundedCornerShape(14.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 10.dp, vertical = 8.dp)
            .testTag("scene_family_chip_${family.id}")
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.width(118.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "#${String.format("%02d", family.id)}",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = if (isSelected) currentAmber else currentCyan
                )
                Text(
                    text = "18p",
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    color = if (isSelected) currentAmber.copy(alpha = 0.8f) else currentTextMuted
                )
            }

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = family.name,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = textColor,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = family.category.title.uppercase(),
                fontSize = 8.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.SemiBold,
                color = if (isSelected) currentAmber.copy(alpha = 0.8f) else currentTextMuted,
                letterSpacing = 0.5.sp
            )
        }
    }
}
