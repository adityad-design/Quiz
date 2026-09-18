package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Achievement

@Composable
fun AchievementBadgesSection(
    achievements: List<Achievement>,
    onViewAllClick: () -> Unit,
    onStartQuizClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedAchievement by remember { mutableStateOf<Achievement?>(null) }
    val unlockedCount = achievements.count { it.isUnlocked }
    val totalCount = achievements.size

    // Sort achievements so high-profile and unlocked achievements are prominent,
    // ensuring 'quiz_master' and 'fast_learner' are featured prominently in the showcase
    val featuredAchievements = remember(achievements) {
        val priorityIds = listOf("quiz_master", "fast_learner", "streak_master", "perfectionist", "first_quiz")
        achievements.sortedWith(
            compareByDescending<Achievement> { priorityIds.contains(it.id) }
                .thenByDescending { it.isUnlocked }
                .thenByDescending { it.progress }
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("achievement_badges_section")
    ) {
        // Section Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF59E0B).copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Achievements",
                        tint = Color(0xFFD97706),
                        modifier = Modifier.size(18.dp)
                    )
                }

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Badges & Milestones",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF10B981).copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                .testTag("badges_unlocked_pill")
                        ) {
                            Text(
                                text = "$unlockedCount / $totalCount",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF059669)
                            )
                        }
                    }
                    Text(
                        text = "Earn badges by beating trivia milestones",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // View All button
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onViewAllClick() }
                    .padding(horizontal = 8.dp, vertical = 6.dp)
                    .testTag("view_all_badges_button"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = "View All",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "View All Badges",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Horizontal Badge Carousel
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("badges_carousel"),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(horizontal = 2.dp, vertical = 4.dp)
        ) {
            items(featuredAchievements, key = { it.id }) { badge ->
                BadgeCard(
                    badge = badge,
                    onClick = { selectedAchievement = badge }
                )
            }
        }
    }

    // Detail Dialog on badge tap
    selectedAchievement?.let { badge ->
        BadgeDetailDialog(
            badge = badge,
            onDismiss = { selectedAchievement = null },
            onStartQuizClick = {
                selectedAchievement = null
                onStartQuizClick()
            }
        )
    }
}

@Composable
private fun BadgeCard(
    badge: Achievement,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isUnlocked = badge.isUnlocked
    val isLight = MaterialTheme.colorScheme.surface.red > 0.5f

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isUnlocked && isLight -> Color(0xFFFFFBEB)
                isUnlocked && !isLight -> Color(0xFF292524)
                else -> MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isUnlocked) 3.dp else 1.dp),
        modifier = modifier
            .width(148.dp)
            .border(
                width = if (isUnlocked) 1.5.dp else 1.dp,
                brush = if (isUnlocked) {
                    Brush.verticalGradient(listOf(Color(0xFFF59E0B), Color(0xFFD97706)))
                } else {
                    Brush.verticalGradient(
                        listOf(
                            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)
                        )
                    )
                },
                shape = RoundedCornerShape(18.dp)
            )
            .clickable { onClick() }
            .testTag("badge_card_${badge.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Medal / Emblem Container
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(
                        if (isUnlocked) {
                            Brush.radialGradient(
                                listOf(Color(0xFFFDE68A), Color(0xFFF59E0B).copy(alpha = 0.4f))
                            )
                        } else {
                            Brush.radialGradient(
                                listOf(
                                    MaterialTheme.colorScheme.surfaceVariant,
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                )
                            )
                        }
                    )
                    .border(
                        width = 1.5.dp,
                        color = if (isUnlocked) Color(0xFFF59E0B) else MaterialTheme.colorScheme.outlineVariant,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = badge.emoji,
                    fontSize = 24.sp
                )

                // Small Status Icon overlay at bottom right
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(if (isUnlocked) Color(0xFF10B981) else Color(0xFF64748B))
                        .border(1.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (isUnlocked) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Unlocked",
                            tint = Color.White,
                            modifier = Modifier.size(11.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Locked",
                            tint = Color.White,
                            modifier = Modifier.size(10.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Badge Title
            Text(
                text = badge.title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (isUnlocked) {
                    if (isLight) Color(0xFF92400E) else Color(0xFFFDE68A)
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(2.dp))

            // Category tag or milestone hint
            Text(
                text = badge.description,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                lineHeight = 12.sp,
                modifier = Modifier.height(24.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Progress Bar & Stats
            if (isUnlocked) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF10B981).copy(alpha = 0.15f))
                        .padding(vertical = 3.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "+${badge.xpReward} XP",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF059669)
                    )
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LinearProgressIndicator(
                        progress = { badge.progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        strokeCap = StrokeCap.Round
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${badge.currentValue} / ${badge.targetValue}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun BadgeDetailDialog(
    badge: Achievement,
    onDismiss: () -> Unit,
    onStartQuizClick: () -> Unit
) {
    val isUnlocked = badge.isUnlocked

    AlertDialog(
        onDismissRequest = onDismiss,
        title = null,
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .testTag("badge_detail_dialog"),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Large Badge Medal Emblem
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .clip(CircleShape)
                        .background(
                            if (isUnlocked) {
                                Brush.radialGradient(
                                    listOf(Color(0xFFFDE68A), Color(0xFFF59E0B).copy(alpha = 0.4f))
                                )
                            } else {
                                Brush.radialGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.surfaceVariant,
                                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                    )
                                )
                            }
                        )
                        .border(
                            width = 2.dp,
                            color = if (isUnlocked) Color(0xFFF59E0B) else MaterialTheme.colorScheme.outlineVariant,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = badge.emoji,
                        fontSize = 38.sp
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Title
                Text(
                    text = badge.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Category pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "${badge.category} Milestone",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Description
                Text(
                    text = badge.description,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Status card
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isUnlocked) {
                            Color(0xFF10B981).copy(alpha = 0.1f)
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        }
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = if (isUnlocked) Icons.Default.Check else Icons.Default.Lock,
                                contentDescription = null,
                                tint = if (isUnlocked) Color(0xFF059669) else Color(0xFF64748B),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = if (isUnlocked) "Badge Unlocked & Claimed!" else "Milestone In Progress",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isUnlocked) Color(0xFF059669) else MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        LinearProgressIndicator(
                            progress = { badge.progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = if (isUnlocked) Color(0xFF10B981) else MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                            strokeCap = StrokeCap.Round
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Progress: ${badge.currentValue} / ${badge.targetValue}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${(badge.progress * 100).toInt()}%",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isUnlocked) Color(0xFF059669) else MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // XP Bonus
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Reward:",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "+${badge.xpReward} XP",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFD97706)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (!isUnlocked) {
                Button(
                    onClick = onStartQuizClick,
                    modifier = Modifier.testTag("dialog_play_now_button"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Play to Unlock")
                }
            } else {
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("dialog_close_button"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Got it!")
                }
            }
        },
        dismissButton = {
            if (!isUnlocked) {
                OutlinedButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Close")
                }
            }
        }
    )
}
