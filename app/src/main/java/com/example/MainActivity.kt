package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CameraPreset
import com.example.data.db.AppDatabase
import com.example.data.db.RoomPresetRepository
import com.example.ui.PresetViewModel
import com.example.ui.screens.ExpertRawCompanionScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.ExposureCalculatorScreen
import com.example.ui.screens.FieldGuideScreen
import com.example.ui.screens.PresetsListScreen
import com.example.ui.theme.AppTheme
import com.example.ui.theme.MyApplicationTheme

enum class AppTab(val label: String, val icon: ImageVector, val tag: String) {
    PRESETS("Presets", Icons.Filled.Camera, "nav_presets"),
    COMPANION("Companion", Icons.Filled.Tune, "nav_companion"),
    FIELD_GUIDE("Field Guide", Icons.Filled.MenuBook, "nav_guide"),
    CALCULATOR("EV Tool", Icons.Filled.Calculate, "nav_calculator")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        com.example.util.ErrorLogger.init(applicationContext)

        val database = AppDatabase.getDatabase(applicationContext)
        val repository = RoomPresetRepository(database.presetDao())

        setContent {
            MyApplicationTheme {
                MainApp(repository = repository)
            }
        }
    }
}

@Composable
fun MainApp(repository: RoomPresetRepository) {
    var currentTab by rememberSaveable { mutableStateOf(AppTab.PRESETS) }
    var selectedPresetId by rememberSaveable { mutableIntStateOf(1) }
    val presetViewModel: PresetViewModel = viewModel(
        factory = PresetViewModel.Factory(repository)
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = AppTheme.colors.canvasBg,
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 0.5.dp,
                        color = AppTheme.colors.border,
                        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                    ),
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                color = AppTheme.colors.cardSurface
            ) {
                NavigationBar(
                    modifier = Modifier
                        .navigationBarsPadding()
                        .testTag("main_bottom_nav"),
                    containerColor = Color.Transparent,
                    tonalElevation = 0.dp
                ) {
                    AppTab.entries.forEach { tab ->
                        val isSelected = currentTab == tab
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { currentTab = tab },
                            icon = {
                                Icon(
                                    imageVector = tab.icon,
                                    contentDescription = tab.label,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = tab.label.uppercase(),
                                    fontSize = 9.sp,
                                    letterSpacing = 1.2.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = AppTheme.colors.amberPrimary,
                                selectedTextColor = AppTheme.colors.amberPrimary,
                                indicatorColor = AppTheme.colors.amberGlow,
                                unselectedIconColor = AppTheme.colors.textMuted,
                                unselectedTextColor = AppTheme.colors.textMuted
                            ),
                            modifier = Modifier.testTag(tab.tag)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (currentTab) {
                AppTab.PRESETS -> {
                    PresetsListScreen(
                        viewModel = presetViewModel,
                        onOpenInCamera = { preset ->
                            selectedPresetId = preset.id
                            currentTab = AppTab.COMPANION
                        }
                    )
                }
                AppTab.COMPANION -> {
                    ExpertRawCompanionScreen(
                        initialPresetId = selectedPresetId,
                        onSelectPreset = { id ->
                            selectedPresetId = id
                        }
                    )
                }
                AppTab.FIELD_GUIDE -> {
                    FieldGuideScreen()
                }
                AppTab.CALCULATOR -> {
                    ExposureCalculatorScreen()
                }
            }
        }
    }
}
