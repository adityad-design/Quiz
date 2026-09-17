package com.example.ui.screens

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.QuizMode
import com.example.ui.components.LivesIndicator
import com.example.ui.components.TimerIndicator
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.QuizUiState
import com.example.ui.viewmodel.QuizViewModel

@Composable
fun QuizScreen(
    uiState: QuizUiState,
    viewModel: QuizViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as? Activity

    // Handle system back button during quiz
    BackHandler {
        viewModel.pauseQuiz()
    }

    val currentQ = uiState.questions.getOrNull(uiState.currentQuestionIndex)
    val totalQuestions = uiState.questions.size
    val progress = if (totalQuestions > 0) {
        (uiState.currentQuestionIndex.toFloat() / totalQuestions)
    } else 0f

    val isKids = uiState.activeMode == QuizMode.KIDS

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("quiz_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Header: Close / Pause, Question Number, Lives, Timer
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = { viewModel.pauseQuiz() },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .testTag("quiz_pause_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Pause,
                        contentDescription = "Pause Quiz",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Question Progress Badge
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Question ${uiState.currentQuestionIndex + 1} of $totalQuestions",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${uiState.activeMode.emoji} ${uiState.currentDifficulty.title}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LivesIndicator(
                        livesRemaining = uiState.livesRemaining,
                        canReviveWithAd = !uiState.hasUsedRevivalAd,
                        onWatchAdClicked = {
                            if (activity != null) {
                                viewModel.reviveWithRewardedAd(activity)
                            }
                        }
                    )

                    TimerIndicator(
                        secondsRemaining = uiState.secondsRemaining,
                        totalSeconds = 60
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Progress bar
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .testTag("quiz_progress_bar"),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                strokeCap = StrokeCap.Round
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Score & Streak Ribbon
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Score: ${uiState.currentScore}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (uiState.currentStreak >= 2) {
                    Text(
                        text = "🔥 ${uiState.currentStreak} In A Row! (+${if (uiState.currentStreak >= 5) "50%" else "25%"})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD97706)
                    )
                } else {
                    Text(
                        text = currentQ?.question?.category ?: "Trivia",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Scrollable Question & Options Container
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Question Card
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                            RoundedCornerShape(20.dp)
                        )
                        .testTag("question_card")
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.primaryContainer)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = currentQ?.question?.category ?: "General",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }

                            Text(
                                text = "60s Limit",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = currentQ?.question?.text ?: "Loading question...",
                            fontSize = if (isKids) 20.sp else 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = if (isKids) 28.sp else 24.sp,
                            modifier = Modifier.testTag("question_text")
                        )
                    }
                }

                // Answer Options (A, B, C, D)
                val options = currentQ?.shuffledOptions ?: emptyList()
                val selectedIdx = currentQ?.selectedIndex
                val correctIdx = currentQ?.correctShuffledIndex
                val isRevealed = uiState.isAnswerRevealed

                options.forEachIndexed { idx, optionText ->
                    val optionLetter = ('A' + idx).toString()

                    val isSelected = selectedIdx == idx
                    val isCorrectAnswer = correctIdx == idx

                    val backgroundColor = when {
                        !isRevealed -> MaterialTheme.colorScheme.surface
                        isSelected && isCorrectAnswer -> Color(0xFF10B981) // Emerald Green
                        isSelected && !isCorrectAnswer -> Color(0xFFEF4444) // Ruby Red
                        isCorrectAnswer -> Color(0xFF10B981).copy(alpha = 0.15f)
                        else -> MaterialTheme.colorScheme.surface
                    }

                    val borderColor = when {
                        !isRevealed && isSelected -> MaterialTheme.colorScheme.primary
                        isRevealed && isCorrectAnswer -> Color(0xFF10B981)
                        isRevealed && isSelected && !isCorrectAnswer -> Color(0xFFEF4444)
                        else -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    }

                    val textColor = when {
                        isRevealed && isSelected -> Color.White
                        isRevealed && isCorrectAnswer -> Color(0xFF047857)
                        else -> MaterialTheme.colorScheme.onSurface
                    }

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = backgroundColor),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(2.dp, borderColor, RoundedCornerShape(16.dp))
                            .clickable(enabled = !isRevealed && !uiState.isQuizPaused) {
                                viewModel.selectAnswer(idx)
                            }
                            .testTag("option_button_$idx")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isRevealed && isSelected -> Color.White.copy(alpha = 0.3f)
                                            else -> MaterialTheme.colorScheme.surfaceVariant
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = optionLetter,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isRevealed && isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Text(
                                text = optionText,
                                fontSize = 15.sp,
                                fontWeight = if (isSelected || (isRevealed && isCorrectAnswer)) FontWeight.Bold else FontWeight.Medium,
                                color = textColor,
                                modifier = Modifier.weight(1f)
                            )

                            if (isRevealed) {
                                if (isCorrectAnswer) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Correct Answer",
                                        tint = if (isSelected) Color.White else Color(0xFF10B981),
                                        modifier = Modifier.size(24.dp)
                                    )
                                } else if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Incorrect Answer",
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Explanation hint when revealed
                AnimatedVisibility(
                    visible = uiState.isAnswerRevealed && currentQ?.question?.explanation?.isNotEmpty() == true,
                    enter = fadeIn() + slideInVertically()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF0284C7).copy(alpha = 0.12f))
                            .border(1.dp, Color(0xFF0284C7).copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "💡 ${currentQ?.question?.explanation}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Revival Dialog (when out of lives, offer 1 rewarded ad)
        if (uiState.showRevivalDialog) {
            AlertDialog(
                onDismissRequest = { /* Must choose an action */ },
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "💔 Out of Lives!")
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "You've run out of lives on Question ${uiState.currentQuestionIndex + 1}.",
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Watch a quick sponsor ad to get +1 extra life and continue this quiz! (Available once per quiz)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (activity != null) {
                                viewModel.reviveWithRewardedAd(activity)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                        modifier = Modifier.testTag("revive_with_ad_button")
                    ) {
                        Text("🎬 Watch Ad (+1 Life)", fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = { viewModel.declineRevival() },
                        modifier = Modifier.testTag("decline_revival_button")
                    ) {
                        Text("End Quiz")
                    }
                }
            )
        }

        // Pause Dialog
        if (uiState.isQuizPaused) {
            AlertDialog(
                onDismissRequest = { viewModel.resumeQuiz() },
                title = { Text(text = "⏸️ Quiz Paused") },
                text = {
                    Text(
                        text = "Take a breather! Your 60s timer is frozen. When you're ready, tap Resume.",
                        fontSize = 14.sp
                    )
                },
                confirmButton = {
                    Button(
                        onClick = { viewModel.resumeQuiz() },
                        modifier = Modifier.testTag("resume_quiz_button")
                    ) {
                        Text("Resume")
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = { viewModel.navigateTo(AppScreen.HOME) },
                        modifier = Modifier.testTag("quit_to_home_button")
                    ) {
                        Text("Quit to Home")
                    }
                }
            )
        }
    }
}
