package com.charan.habitdiary.presentation.adddailylog.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.charan.habitdiary.R

@Composable
fun DateTimeRow(
    date: String,
    time: String,
    onDateClick: () -> Unit,
    onTimeClick: () -> Unit,
) {
    val dateTimeItems = listOf(
        LabelValue(
            label = stringResource(R.string.date),
            value = date,
            icon = Icons.Rounded.CalendarMonth,
            onClick = onDateClick
        ),
        LabelValue(
            label = stringResource(R.string.time),
            value = time,
            icon = Icons.Rounded.AccessTime,
            onClick = onTimeClick
        )
    )

    Row(
        modifier = Modifier
            .padding(vertical = 20.dp),
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp)
    ) {
        dateTimeItems.forEach { item ->
            InfoCard(
                label = item.label,
                value = item.value,
                icon = item.icon,
                onClick = item.onClick,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun InfoCard(
    label: String,
    value: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        modifier = modifier,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label
            )
            Column(modifier = Modifier.padding(start = 8.dp)) {
                Text(
                    label,
                    style = MaterialTheme.typography.labelSmallEmphasized.copy(
                        fontWeight = FontWeight.W400
                    )
                )
                Text(
                    value,
                    style = MaterialTheme.typography.labelMediumEmphasized.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}


data class LabelValue(
    val label : String,
    val icon : ImageVector,
    val onClick : () ->Unit,
    val value : String
)

@Preview
@Composable
fun DateTimeRowPreview() {
    DateTimeRow(
        date = "Jan 14, 2023",
        time = "10:30 AM",
        onDateClick = { },
        onTimeClick = { }
    )
}


