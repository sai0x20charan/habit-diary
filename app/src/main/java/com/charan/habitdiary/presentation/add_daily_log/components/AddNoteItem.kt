package com.charan.habitdiary.presentation.add_daily_log.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TextFormat
import androidx.compose.material.icons.outlined.FormatBold
import androidx.compose.material.icons.outlined.FormatItalic
import androidx.compose.material.icons.outlined.FormatUnderlined
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.mohamedrejeb.richeditor.model.RichTextState
import com.mohamedrejeb.richeditor.ui.BasicRichTextEditor

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AddNoteItem(
    value: String,
    onValueChange: (String) -> Unit,
    state: RichTextState,
) {
    LaunchedEffect(value) {
        val currentMarkdown = state.toMarkdown()
        if (value.isNotBlank() && value != currentMarkdown) {
            state.setMarkdown(value)
        }
    }
    LaunchedEffect(state.annotatedString) {
        val markdown = state.toMarkdown()
        if (markdown != value) {
            onValueChange(markdown)
        }
    }

        BasicRichTextEditor(
            state = state,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 5.dp),
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onSurface
            ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            decorationBox = { innerTextField ->
                Box {
                    if (state.annotatedString.isEmpty()) {
                        Text(
                            text = "Add a note...",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    innerTextField()
                }
            }
        )
}
