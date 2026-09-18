package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Difficulty
import com.example.data.model.QuizMode
import com.example.ui.components.AchievementBadgesSection
import com.example.ui.components.AdMobBanner
import com.example.ui.components.DailyStreakCard
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.QuizUiState
import com.example.ui.viewmodel.QuizViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    uiState: QuizUiState,
    viewModel: QuizViewModel,
    modifier: Modifier = Modifier
) {
    val isKids = uiState.activeMode == QuizMode.KIDS
    val todayFormatted = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date())

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("home_screen")
    ) {
        // Top App Bar
        Surface(
            tonalElevation = 2.dp,
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_app_icon),
                            contentDescription = "QuizRush Icon",
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                        )
                        Column {
                            Text(
                                text = "QuizRush",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isKids) "👦 Kids Mode" else "🧑 Adults Mode",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Daily Streak Chip
                        if (uiState.stats.effectiveDailyStreak > 0) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color(0xFFEA580C).copy(alpha = 0.15f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                    .testTag("top_streak_chip")
                            ) {
                                Text(
                                    text = "🔥 ${uiState.stats.effectiveDailyStreak}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFEA580C)
                                )
                            }
                        }

                        // XP Chip
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFFFFB703).copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "⚡ ${uiState.stats.totalXp} XP",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFD97706)
                            )
                        }

                        IconButton(
                            onClick = { viewModel.navigateTo(AppScreen.STATISTICS) },
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("stats_icon_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.QueryStats,
                                contentDescription = "Statistics",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        IconButton(
                            onClick = { viewModel.navigateTo(AppScreen.ACHIEVEMENTS) },
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("achievements_icon_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = "Achievements",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        IconButton(
                            onClick = { viewModel.navigateTo(AppScreen.HISTORY) },
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("history_icon_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = "Quiz History",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        IconButton(
                            onClick = { viewModel.navigateTo(AppScreen.SETTINGS) },
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("settings_icon_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Mode Selector Toggle (👦 Kids vs 🧑 Adults)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Kids Tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isKids) MaterialTheme.colorScheme.primary else Color.Transparent
                            )
                            .clickable { viewModel.setQuizMode(QuizMode.KIDS) }
                            .padding(vertical = 8.dp)
                            .testTag("mode_tab_kids"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "👦 Kids (${uiState.totalKidsQuestions})",
                            fontSize = 14.sp,
                            fontWeight = if (isKids) FontWeight.Bold else FontWeight.Medium,
                            color = if (isKids) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Adults Tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (!isKids) MaterialTheme.colorScheme.primary else Color.Transparent
                            )
                            .clickable { viewModel.setQuizMode(QuizMode.ADULTS) }
                            .padding(vertical = 8.dp)
                            .testTag("mode_tab_adults"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🧑 Adults (${uiState.totalAdultsQuestions})",
                            fontSize = 14.sp,
                            fontWeight = if (!isKids) FontWeight.Bold else FontWeight.Medium,
                            color = if (!isKids) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Main Scrollable Content
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }

            // Mode Hero Banner Card
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isKids) Color(0xFFFEF3C7) else Color(0xFF1E293B)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("hero_banner_card")
                ) {
                    Column {
                        Image(
                            painter = painterResource(
                                id = if (isKids) R.drawable.img_kids_banner else R.drawable.img_adults_banner
                            ),
                            contentDescription = if (isKids) "Kids Quiz Banner" else "Adults Quiz Banner",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp),
                            contentScale = ContentScale.Crop
                        )

                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = if (isKids) "✨ Explorer Quest" else "🧠 Cognitive Arena",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isKids) Color(0xFF92400E) else Color(0xFFF1F5F9)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (isKids)
                                    "Animals, Shapes, Simple Science, Countries & Fun Math!"
                                else
                                    "Percentages, Indian History & Polity, Tech, World GK & Logic!",
                                fontSize = 12.sp,
                                color = if (isKids) Color(0xFF78350F) else Color(0xFF94A3B8)
                            )
                        }
                    }
                }
            }

            // Daily Streak Card (Consecutive days played & flame icon tracker)
            item {
                DailyStreakCard(
                    stats = uiState.stats,
                    onPlayClick = { viewModel.startQuiz(Difficulty.DAILY) }
                )
            }

            // Daily Challenge Card
            item {
                ElevatedCard(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.startQuiz(Difficulty.DAILY) }
                        .testTag("daily_challenge_card")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "🌟 Daily Challenge",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFFEF4444).copy(alpha = 0.2f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "+250 XP",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFDC2626)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$todayFormatted • 10 Fresh Daily Questions",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                            if (uiState.stats.dailyChallengeStreak > 0) {
                                Text(
                                    text = "🔥 ${uiState.stats.dailyChallengeStreak} Day Streak!",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFFD97706)
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Play Daily Challenge",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            }

            // Badges & Milestones Showcase
            item {
                AchievementBadgesSection(
                    achievements = uiState.achievements,
                    onViewAllClick = { viewModel.navigateTo(AppScreen.ACHIEVEMENTS) },
                    onStartQuizClick = { viewModel.startQuiz(Difficulty.MEDIUM) }
                )
            }

            // Section Header: Choose Difficulty
            item {
                Text(
                    text = "Select Difficulty",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Difficulty Cards (Easy, Medium, Hard)
            item {
                DifficultyCard(
                    difficulty = Difficulty.EASY,
                    bestScore = uiState.stats.bestScores["${uiState.activeMode.name}_EASY"] ?: 0,
                    onClick = { viewModel.startQuiz(Difficulty.EASY) }
                )
            }

            item {
                DifficultyCard(
                    difficulty = Difficulty.MEDIUM,
                    bestScore = uiState.stats.bestScores["${uiState.activeMode.name}_MEDIUM"] ?: 0,
                    onClick = { viewModel.startQuiz(Difficulty.MEDIUM) }
                )
            }

            item {
                DifficultyCard(
                    difficulty = Difficulty.HARD,
                    bestScore = uiState.stats.bestScores["${uiState.activeMode.name}_HARD"] ?: 0,
                    onClick = { viewModel.startQuiz(Difficulty.HARD) }
                )
            }

            // Quick Stats Row
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    StatPill("Quizzes", "${uiState.stats.totalQuizzes}")
                    StatPill("Best Streak", "${uiState.stats.bestStreak} 🔥")
                    StatPill("Accuracy", "%.0f%%".format(uiState.stats.overallAccuracy))
                }
            }

            // Quiz History Card (Access last 5 completed games)
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                            RoundedCornerShape(16.dp)
                        )
                        .clickable { viewModel.navigateTo(AppScreen.HISTORY) }
                        .testTag("home_quiz_history_card")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF6366F1).copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.History,
                                    contentDescription = null,
                                    tint = Color(0xFF6366F1),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Quiz History",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (uiState.recentGames.isNotEmpty()) {
                                        "${uiState.recentGames.size} of 5 recent games recorded"
                                    } else {
                                        "View your last 5 completed games & scores"
                                    },
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Text(
                            text = "View →",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(10.dp)) }
        }

        // Bottom AdMob Banner View (Home Screen Banner)
        AdMobBanner(
            adUnitId = viewModel.adMobManager.bannerAdUnitId,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun DifficultyCard(
    difficulty: Difficulty,
    bestScore: Int,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag("difficulty_card_${difficulty.name.lowercase()}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = difficulty.badge, fontSize = 22.sp)
                }

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = difficulty.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "10 Qs • 60s • 3 ❤️",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = if (bestScore > 0) "Best: $bestScore pts" else difficulty.description,
                        fontSize = 12.sp,
                        color = if (bestScore > 0) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = if (bestScore > 0) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Start ${difficulty.title}",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun StatPill(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
