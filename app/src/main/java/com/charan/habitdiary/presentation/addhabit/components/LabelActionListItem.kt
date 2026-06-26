package com.charan.habitdiary.presentation.addhabit.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LabelActionListItem(
    label : String,
    icon : @Composable () -> Unit,
    tailingContent : @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Row (
        modifier = Modifier.then(modifier).fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon()
        Spacer(Modifier.width(5.dp))
        Text(
            text = label,
            modifier = Modifier.weight(1f)
        )
        tailingContent()
    }
}