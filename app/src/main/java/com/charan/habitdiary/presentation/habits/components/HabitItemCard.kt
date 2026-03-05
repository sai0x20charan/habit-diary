package com.charan.habitdiary.presentation.habits.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.charan.habitdiary.R
import com.charan.habitdiary.utils.DateUtil.toLocale
import kotlinx.datetime.DayOfWeek
import java.time.format.TextStyle

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HabitItemCard(
    title: String,
    description: String,
    time: String = "",
    reminder: String = "",
    isCompleted: Boolean,
    onCompletedChange: (Boolean) -> Unit,
    onClick: () -> Unit,
    habitDays: List<DayOfWeek>
) {
    val daysText = when {
        habitDays.size == 7 -> stringResource(R.string.daily)
        habitDays.containsAll(
            listOf(
                DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                DayOfWeek.THURSDAY, DayOfWeek.FRIDAY
            )
        ) &&
                habitDays.all { it <= DayOfWeek.FRIDAY } -> stringResource(R.string.weekdays)

        habitDays.containsAll(listOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)) &&
                habitDays.all { it >= DayOfWeek.SATURDAY } -> stringResource(R.string.weekends)

        habitDays.isEmpty() -> stringResource(R.string.no_repeat)
        else -> habitDays.joinToString(", ") {
            it.toLocale(TextStyle.SHORT_STANDALONE)
        }
    }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .then(
                if(isCompleted) {
                    Modifier.border(
                        width = 0.5.dp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                        shape = CardDefaults.shape
                    )
                } else{
                    Modifier
                }
            ),
        onClick = onClick,
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = if (isCompleted) 1.dp else 5.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 12.dp)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLargeEmphasized.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (description.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodyMediumEmphasized,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                RoundCheckbox(
                    checked = isCompleted,
                    onCheckedChange = onCompletedChange
                )
            }

            if (time.isNotEmpty() || reminder.isNotEmpty() || daysText.isNotEmpty()) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (time.isNotEmpty()) {
                        HabitChip(
                            icon = Icons.Rounded.Schedule,
                            text = time,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            onColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    if (reminder.isNotEmpty()) {
                        HabitChip(
                            icon = Icons.Rounded.Notifications,
                            text = reminder,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            onColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    HabitChip(
                        icon = Icons.Rounded.Repeat,
                        text = daysText,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        onColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun HabitChip(
    icon: ImageVector,
    text: String,
    color: Color,
    onColor: Color
) {
    Surface(
        color = color.copy(alpha = 0.4f),
        shape = CircleShape,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = onColor.copy(alpha = 0.8f),
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmallEmphasized.copy(fontWeight = FontWeight.Medium),
                color = onColor.copy(alpha = 0.9f)
            )
        }
    }
}

@Composable
fun RoundCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    strokeColor: Color = MaterialTheme.colorScheme.outline,
    fillColor: Color = MaterialTheme.colorScheme.primary
) {
    val targetBaseColor = if (checked) fillColor.copy(alpha = 0.35f) else strokeColor
    val animatedBaseColor by animateColorAsState(
        targetValue = targetBaseColor,
        animationSpec = tween(300),
        label = "baseColor"
    )
    val innerCircleScale by animateFloatAsState(
        targetValue = if (checked) 1f else 0f,
        animationSpec = tween(400, delayMillis = 50),
        label = "innerScale"
    )

    Box(
        modifier = modifier
            .size(32.dp)
            .clip(CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onCheckedChange(!checked) }
            .padding(4.dp)
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val strokeWidth = 2.dp.toPx()
            drawCircle(
                color = animatedBaseColor,
                style = if (!checked) Stroke(width = strokeWidth) else Fill
            )
            if (innerCircleScale > 0f) {
                drawCircle(
                    color = fillColor,
                    radius = (size.minDimension * 0.32f) * innerCircleScale
                )
            }
        }
    }
}

@Preview
@Composable
fun HabitItemCardPreview() {
    MaterialTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            HabitItemCard(
                title = "Morning Jog",
                description = "Jog for 30 minutes in the park to get some fresh air",
                time = "6:00 AM",
                reminder = "5:45 AM",
                isCompleted = false,
                onCompletedChange = {},
                onClick = {},
                habitDays = listOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY)
            )

            HabitItemCard(
                title = "Read Book",
                description = "",
                time = "9:00 PM",
                isCompleted = true,
                onCompletedChange = {},
                onClick = {},
                habitDays = listOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)
            )
        }
    }
}