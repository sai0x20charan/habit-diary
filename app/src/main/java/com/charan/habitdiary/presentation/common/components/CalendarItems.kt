package com.charan.habitdiary.presentation.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.charan.habitdiary.utils.DateUtil.toLocale
import kotlinx.datetime.DayOfWeek
import java.time.format.TextStyle

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CalendarDayItem(
    date: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    hasContent: Boolean = false,
    isToday: Boolean,
    isCurrentMonth: Boolean = true,
    isHabitDone: Boolean = false
) {
    val textColor = when {
        isSelected -> MaterialTheme.colorScheme.onPrimaryContainer
        !isCurrentMonth -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
        isHabitDone -> MaterialTheme.colorScheme.onSecondaryContainer
        else -> MaterialTheme.colorScheme.onSurface
    }
    val backgroundModifier =
        Modifier
            .size(35.dp)
            .padding(3.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .then(
                when {
                    isSelected -> Modifier.background(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f)
                    )

                    isHabitDone -> Modifier.background(
                        MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.9f)
                    )

                    else -> Modifier
                }
            )
            .then(
                if (isToday && !isSelected) {
                    Modifier.border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                        shape = CircleShape
                    )
                } else {
                    Modifier
                }
            )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = backgroundModifier,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = date,
                style = MaterialTheme.typography.titleSmallEmphasized,
                color = textColor
            )
        }

        if (hasContent && !isSelected) {
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            )
        } else {
            Spacer(modifier = Modifier.size(4.dp))
        }
    }
}








@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CalendarHeaderItem(
    dayOfWeek : List<DayOfWeek>
) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)) {
        for (dayOfWeek in dayOfWeek) {
            Text(
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                style = MaterialTheme.typography.labelSmallEmphasized,
                text = dayOfWeek.toLocale(TextStyle.SHORT_STANDALONE),
            )
        }
    }

}
