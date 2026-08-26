package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.game.CurrentScreen
import com.example.game.GameViewModel
import com.example.ui.screens.GameplayScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LevelSelectScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.ShopScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.StatsScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val viewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val currentScreen by viewModel.currentScreen.collectAsState()

                // Back press handling
                BackHandler(enabled = currentScreen != CurrentScreen.HOME && currentScreen != CurrentScreen.SPLASH) {
                    when (currentScreen) {
                        CurrentScreen.GAMEPLAY -> viewModel.navigateTo(CurrentScreen.LEVEL_SELECT)
                        CurrentScreen.LEVEL_SELECT,
                        CurrentScreen.SHOP,
                        CurrentScreen.STATS,
                        CurrentScreen.SETTINGS -> viewModel.navigateTo(CurrentScreen.HOME)
                        else -> {}
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF0F172A)
                ) {
                    AnimatedContent(
                        targetState = currentScreen,
                        transitionSpec = {
                            fadeIn() togetherWith fadeOut()
                        },
                        label = "ScreenTransition"
                    ) { targetScreen ->
                        when (targetScreen) {
                            CurrentScreen.SPLASH -> SplashScreen(viewModel = viewModel)
                            CurrentScreen.HOME -> HomeScreen(viewModel = viewModel)
                            CurrentScreen.LEVEL_SELECT -> LevelSelectScreen(viewModel = viewModel)
                            CurrentScreen.GAMEPLAY -> GameplayScreen(viewModel = viewModel)
                            CurrentScreen.SHOP -> ShopScreen(viewModel = viewModel)
                            CurrentScreen.STATS -> StatsScreen(viewModel = viewModel)
                            CurrentScreen.SETTINGS -> SettingsScreen(viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}

