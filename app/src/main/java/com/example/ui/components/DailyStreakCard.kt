package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserStats
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class DayStreakInfo(
    val dayLetter: String,
    val dayName: String,
    val dayNumber: String,
    val dateStr: String,
    val isToday: Boolean,
    val isPast: Boolean,
    val isPlayed: Boolean,
    val isFuture: Boolean
)

fun calculateCurrentWeekDays(
    playedDates: Set<String>,
    referenceDate: Date = Date()
): List<DayStreakInfo> {
    val cal = Calendar.getInstance(Locale.getDefault())
    cal.time = referenceDate
    cal.firstDayOfWeek = Calendar.MONDAY

    val currentDayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
    val daysFromMonday = (currentDayOfWeek - Calendar.MONDAY + 7) % 7
    cal.add(Calendar.DAY_OF_YEAR, -daysFromMonday)

    val todayCal = Calendar.getInstance(Locale.getDefault())
    todayCal.time = referenceDate
    val todayYear = todayCal.get(Calendar.YEAR)
    val todayDayOfYear = todayCal.get(Calendar.DAY_OF_YEAR)

    val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.US)
    val nameFormat = SimpleDateFormat("EEE", Locale.getDefault())
    val numberFormat = SimpleDateFormat("d", Locale.getDefault())

    val result = mutableListOf<DayStreakInfo>()
    for (i in 0..6) {
        val date = cal.time
        val dateStr = dateFormat.format(date)
        val isToday = cal.get(Calendar.YEAR) == todayYear && cal.get(Calendar.DAY_OF_YEAR) == todayDayOfYear
        val isPast = cal.before(todayCal) && !isToday
        val isFuture = cal.after(todayCal) && !isToday
        val isPlayed = playedDates.contains(dateStr)

        val dayName = nameFormat.format(date)
        val dayLetter = dayName.take(1)

        result.add(
            DayStreakInfo(
                dayLetter = dayLetter,
                dayName = dayName,
                dayNumber = numberFormat.format(date),
                dateStr = dateStr,
                isToday = isToday,
                isPast = isPast,
                isPlayed = isPlayed,
                isFuture = isFuture
            )
        )
        cal.add(Calendar.DAY_OF_YEAR, 1)
    }
    return result
}

@Composable
fun DailyStreakCard(
    stats: UserStats,
    onPlayClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val streakCount = stats.effectiveDailyStreak
    val todayDateStr = SimpleDateFormat("yyyyMMdd", Locale.US).format(Date())
    val isPlayedToday = stats.lastPlayedDate == todayDateStr ||
            stats.lastDailyDate == todayDateStr ||
            stats.playedDates.contains(todayDateStr)

    val weekDays = calculateCurrentWeekDays(stats.playedDates)

    val infiniteTransition = rememberInfiniteTransition(label = "flame_pulse")
    val flameScale by infiniteTransition.animateFloat(
        initialValue = if (streakCount > 0) 0.94f else 1.0f,
        targetValue = if (streakCount > 0) 1.08f else 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flame_scale"
    )

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = if (isPlayedToday) 1.5.dp else 1.dp,
                color = if (isPlayedToday) Color(0xFFF59E0B)
                else if (streakCount > 0) Color(0xFFFB923C).copy(alpha = 0.6f)
                else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(20.dp)
            )
            .testTag("daily_streak_component")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Top Row: Flame Icon + Streak Count + Status Chip
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Visual Flame Icon with fiery gradient and animated pulse
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .scale(if (streakCount > 0 && !isPlayedToday) flameScale else 1f)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = if (streakCount > 0) listOf(
                                        Color(0xFFFFEA00),
                                        Color(0xFFFF9100),
                                        Color(0xFFFF3D00)
                                    ) else listOf(
                                        Color(0xFFFED7AA),
                                        Color(0xFFFB923C)
                                    )
                                )
                            )
                            .testTag("streak_flame_icon"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Whatshot,
                            contentDescription = "Daily Streak Flame",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Column {
                        Row(
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "$streakCount",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (streakCount > 0) Color(0xFFEA580C) else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.testTag("streak_count_text")
                            )
                            Text(
                                text = if (streakCount == 1) "Day Streak" else "Days Streak",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(bottom = 3.dp)
                            )
                        }

                        Text(
                            text = when {
                                isPlayedToday -> "Streak protected for today! 🎉"
                                streakCount > 0 -> "Play today to keep streak burning! 🔥"
                                else -> "Play today to start your streak! 🎯"
                            },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = when {
                                isPlayedToday -> Color(0xFF059669)
                                streakCount > 0 -> Color(0xFFD97706)
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            modifier = Modifier.testTag("streak_status_text")
                        )
                    }
                }

                // Milestone / Status Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isPlayedToday) Color(0xFF10B981).copy(alpha = 0.12f)
                            else if (streakCount > 0) Color(0xFFF59E0B).copy(alpha = 0.15f)
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (isPlayedToday) "Active ✓"
                        else if (streakCount >= 5) "1.5x XP 🔥"
                        else if (streakCount >= 3) "1.25x XP ⚡"
                        else "Daily 📅",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isPlayedToday) Color(0xFF059669)
                        else if (streakCount > 0) Color(0xFFD97706)
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 7-Day Week Tracker Row (M T W T F S S)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
                    .padding(vertical = 10.dp, horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                weekDays.forEach { day ->
                    DayStreakNode(
                        day = day,
                        modifier = Modifier.testTag("streak_day_${day.dateStr}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Bottom Engagement Section: Call-To-Action or Secured Status
            if (!isPlayedToday) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (streakCount > 0) "Don't break your streak!" else "Start daily momentum",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (streakCount >= 3) "Maintain your 1.25x XP multiplier!" else "Earn +250 XP bonus for daily challenge",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Button(
                        onClick = onPlayClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFEA580C)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("play_streak_quiz_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Play Quiz",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF10B981).copy(alpha = 0.1f))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Streak Secured",
                        tint = Color(0xFF059669),
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Great job! Day ${streakCount} completed today. Come back tomorrow!",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF047857)
                    )
                }
            }
        }
    }
}

@Composable
private fun DayStreakNode(
    day: DayStreakInfo,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
    ) {
        // Day Letter (M, T, W, T, F, S, S)
        Text(
            text = day.dayLetter,
            fontSize = 11.sp,
            fontWeight = if (day.isToday) FontWeight.Bold else FontWeight.Medium,
            color = if (day.isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Circle Badge
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(
                    when {
                        day.isPlayed -> Brush.linearGradient(
                            listOf(Color(0xFFFF9100), Color(0xFFFF3D00))
                        )
                        day.isToday -> Brush.linearGradient(
                            listOf(Color(0xFFFEF3C7), Color(0xFFFDE68A))
                        )
                        else -> Brush.linearGradient(
                            listOf(Color.Transparent, Color.Transparent)
                        )
                    }
                )
                .border(
                    width = when {
                        day.isToday && day.isPlayed -> 2.dp
                        day.isToday -> 2.dp
                        day.isPlayed -> 1.dp
                        else -> 1.dp
                    },
                    color = when {
                        day.isToday && day.isPlayed -> Color(0xFFF59E0B)
                        day.isToday -> Color(0xFFEA580C)
                        day.isPlayed -> Color(0xFFFF9800)
                        else -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                    },
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            when {
                day.isPlayed -> {
                    Icon(
                        imageVector = Icons.Default.Whatshot,
                        contentDescription = "${day.dayName} Completed",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
                day.isToday -> {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = "Today",
                        tint = Color(0xFFEA580C),
                        modifier = Modifier.size(16.dp)
                    )
                }
                day.isPast -> {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.outlineVariant)
                    )
                }
                else -> {
                    Text(
                        text = day.dayNumber,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        }

        // Mini status label
        Text(
            text = if (day.isToday) "Today" else day.dayNumber,
            fontSize = 9.sp,
            fontWeight = if (day.isToday) FontWeight.Bold else FontWeight.Normal,
            color = if (day.isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
    }
}
