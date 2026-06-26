package com.charan.habitdiary.presentation.addhabit.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FormTextComponent(
    title: String,
    textFieldLabel: String,
    textValue: String,
    onTextChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMediumEmphasized
        )
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = textValue,
            onValueChange = onTextChange,
            label = { Text(textFieldLabel) },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
