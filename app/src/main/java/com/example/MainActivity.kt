package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.AchievementsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.QuizScreen
import com.example.ui.screens.ResultsScreen
import com.example.ui.screens.ReviewScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.StatisticsScreen
import com.example.ui.theme.QuizRushTheme
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.QuizViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val quizViewModel: QuizViewModel = viewModel()
            val uiState by quizViewModel.uiState.collectAsStateWithLifecycle()
            val snackbarHostState = remember { SnackbarHostState() }

            LaunchedEffect(uiState.snackbarMessage) {
                uiState.snackbarMessage?.let { msg ->
                    snackbarHostState.showSnackbar(
                        message = msg,
                        duration = SnackbarDuration.Short
                    )
                }
            }

            QuizRushTheme(
                mode = uiState.activeMode,
                themeSetting = uiState.themeSetting
            ) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = { SnackbarHost(snackbarHostState) }
                ) { innerPadding ->
                    Crossfade(
                        targetState = uiState.currentScreen,
                        label = "screen_crossfade"
                    ) { screen ->
                        val screenModifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)

                        when (screen) {
                            AppScreen.SPLASH -> SplashScreen(modifier = screenModifier)
                            AppScreen.HOME -> HomeScreen(
                                uiState = uiState,
                                viewModel = quizViewModel,
                                modifier = screenModifier
                            )
                            AppScreen.QUIZ -> QuizScreen(
                                uiState = uiState,
                                viewModel = quizViewModel,
                                modifier = screenModifier
                            )
                            AppScreen.RESULTS -> ResultsScreen(
                                uiState = uiState,
                                viewModel = quizViewModel,
                                modifier = screenModifier
                            )
                            AppScreen.REVIEW -> ReviewScreen(
                                uiState = uiState,
                                viewModel = quizViewModel,
                                modifier = screenModifier
                            )
                            AppScreen.STATISTICS -> StatisticsScreen(
                                uiState = uiState,
                                viewModel = quizViewModel,
                                modifier = screenModifier
                            )
                            AppScreen.ACHIEVEMENTS -> AchievementsScreen(
                                uiState = uiState,
                                viewModel = quizViewModel,
                                modifier = screenModifier
                            )
                            AppScreen.SETTINGS -> SettingsScreen(
                                uiState = uiState,
                                viewModel = quizViewModel,
                                modifier = screenModifier
                            )
                        }
                    }
                }
            }
        }
    }
}
