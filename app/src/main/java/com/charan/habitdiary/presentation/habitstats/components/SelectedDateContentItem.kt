package com.charan.habitdiary.presentation.habitstats.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.charan.habitdiary.presentation.habits.components.RoundCheckbox
import com.charan.habitdiary.core.utils.DateUtil.toFormattedString
import kotlinx.datetime.LocalDate

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SelectedDateContentItem(
    modifier: Modifier = Modifier,
    date: LocalDate,
    isDone: Boolean,
    hasLog: Boolean,
    onCompleteHabit: () -> Unit = {},
    onCreateJournal: () -> Unit = {}
) {
    Surface(
        modifier = modifier
            .navigationBarsPadding()
            .fillMaxWidth()
            .padding(16.dp)
            .animateContentSize(
                spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            ),
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surfaceContainer,
        tonalElevation = 2.dp
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            BottomSheetDefaults.DragHandle()
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = date.toFormattedString(),
                    style = MaterialTheme.typography.headlineSmallEmphasized,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                HabitToggleButton(
                    isDone = isDone,
                    onClick = onCompleteHabit
                )
                AnimatedVisibility(
                    visible = isDone,
                ) {
                    FilledTonalButton(
                        onClick = {
                            onCreateJournal()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shapes = ButtonDefaults.shapes()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.EditNote,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "View Log",
                                style = MaterialTheme.typography.bodyMediumEmphasized,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HabitToggleButton(
    isDone: Boolean,
    onClick: () -> Unit,
) {
    val animatedContainerColor by animateColorAsState(
        targetValue = if (isDone) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
        animationSpec = tween(300),
        label = "container"
    )

    val animatedContentColor by animateColorAsState(
        targetValue = if (isDone) MaterialTheme.colorScheme.onPrimary  else MaterialTheme.colorScheme.onSecondaryContainer,
        animationSpec = tween(300),
        label = "content"
    )

    Surface(
        onClick = onClick,
        shape = MaterialTheme.shapes.large,
        color = animatedContainerColor,
        contentColor = animatedContentColor,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            RoundCheckbox(
                checked = isDone,
                onCheckedChange = { onClick() },
                fillColor = if (isDone) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = if (isDone) "Habit Completed" else "Mark as Done",
                    style = MaterialTheme.typography.titleMediumEmphasized,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = if (isDone) "Great job, keep it up!" else "Tap to complete this habit",
                    style = MaterialTheme.typography.bodySmallEmphasized,
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewRedesign() {
    Column {
        // Case 1: Nothing done
        SelectedDateContentItem(
            date = LocalDate(2024, 6, 15),
            isDone = false,
            hasLog = false
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Case 2: All done
        SelectedDateContentItem(
            date = LocalDate(2024, 6, 15),
            isDone = true,
            hasLog = true
        )
    }
}