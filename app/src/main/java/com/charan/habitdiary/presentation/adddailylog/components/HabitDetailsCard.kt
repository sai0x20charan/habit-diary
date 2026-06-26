package com.charan.habitdiary.presentation.adddailylog.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.charan.habitdiary.R

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HabitDetailsCard(
    habitTitle: String,
    modifier: Modifier = Modifier,
    onClick : () -> Unit = { }
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),

        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f),
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
        onClick = {
            onClick()

        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Text(
                text = stringResource(R.string.for_your_completed_habit),
                style = MaterialTheme.typography.labelLargeEmphasized,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Text(
                text = habitTitle,
                style = MaterialTheme.typography.headlineMediumEmphasized.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview
@Composable
fun HabitDetailsCardPreview() {
    HabitDetailsCard(habitTitle = "Morning Jog")
}