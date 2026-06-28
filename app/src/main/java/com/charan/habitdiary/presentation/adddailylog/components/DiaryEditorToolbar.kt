package com.charan.habitdiary.presentation.adddailylog.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.TextFormat
import androidx.compose.material.icons.outlined.FormatBold
import androidx.compose.material.icons.outlined.FormatItalic
import androidx.compose.material.icons.outlined.FormatUnderlined
import androidx.compose.material.icons.rounded.AttachFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.charan.habitdiary.R

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DiaryEditorToolbar(
    modifier: Modifier = Modifier,
    onSaveLog: () -> Unit,
    onDeleteLogClick: () -> Unit,
    onBoldTextClick: () -> Unit,
    onItalicTextClick: () -> Unit,
    onUnderlineTextClick: () -> Unit,
    isBoldTextSelected: Boolean,
    isItalicTextSelected: Boolean,
    isUnderlineTextSelected: Boolean,
    isSaveEnabled: Boolean = true,
    showDeleteButton: Boolean = false,
    onAttachMedia: () -> Unit = {},
    isEditingToolsExpanded : Boolean = false,
    onExpandEditingToolsClick : () -> Unit = { }
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.padding(start = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AnimatedVisibility(visible = showDeleteButton) {
                    IconButton(
                        onClick = onDeleteLogClick,
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        ),
                        shapes = IconButtonDefaults.shapes(),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(R.string.delete)
                        )
                    }
                }
                IconButton(
                    onClick = onAttachMedia,
                    shapes = IconButtonDefaults.shapes()
                ) {
                    Icon(
                        imageVector = Icons.Rounded.AttachFile,
                        contentDescription = stringResource(R.string.attach_media),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            VerticalDivider(
                modifier = Modifier.height(22.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )

            NoteTextEditingControls(
                isExpanded = isEditingToolsExpanded,
                isBoldSelected = isBoldTextSelected,
                isItalicSelected = isItalicTextSelected,
                isUnderlineSelected = isUnderlineTextSelected,
                onExpandClick = { onExpandEditingToolsClick() },
                onBoldClick = onBoldTextClick,
                onItalicClick = onItalicTextClick,
                onUnderlineClick = onUnderlineTextClick,
                modifier = Modifier.weight(1f)
                    .horizontalScroll(rememberScrollState())

            )

            VerticalDivider(
                modifier = Modifier.height(22.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )

            Box(modifier = Modifier.padding(horizontal = 6.dp)) {
                Button(
                    onClick = onSaveLog,
                    enabled = isSaveEnabled,
                    shapes = ButtonDefaults.shapes(),
                ) {
                    Text(text = stringResource(R.string.save))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun NoteTextEditingControls(
    modifier: Modifier = Modifier,
    isExpanded: Boolean,
    isBoldSelected: Boolean = false,
    isItalicSelected: Boolean = false,
    isUnderlineSelected: Boolean = false,
    onExpandClick: () -> Unit,
    onBoldClick: () -> Unit,
    onItalicClick: () -> Unit,
    onUnderlineClick: () -> Unit
) {
    val editingOptions = listOf(
        EditingOption(Icons.Outlined.FormatBold, isBoldSelected, onBoldClick),
        EditingOption(Icons.Outlined.FormatItalic, isItalicSelected, onItalicClick),
        EditingOption(Icons.Outlined.FormatUnderlined, isUnderlineSelected, onUnderlineClick)
    )

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {

        IconButton(
            onClick = onExpandClick,
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = if (isExpanded)
                    MaterialTheme.colorScheme.secondaryContainer
                else
                    Color.Transparent,
                contentColor = if (isExpanded)
                    MaterialTheme.colorScheme.onSecondaryContainer
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            ),
            shapes = IconButtonDefaults.shapes(),

        ) {
            Icon(Icons.Default.TextFormat, contentDescription = stringResource(R.string.text_formatting))
        }

        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn(tween(180)) + expandHorizontally(tween(200)),
            exit = fadeOut(tween(120)) + shrinkHorizontally(tween(160))
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                editingOptions.forEach { option ->
                        FilledIconButton(
                            onClick = option.onClick,
                            modifier = Modifier.size(IconButtonDefaults.extraSmallContainerSize()),
                            shapes = IconButtonDefaults.shapes(),

                            colors = if(option.isSelected) {
                                IconButtonDefaults.filledIconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                )
                            } else {
                                IconButtonDefaults.outlinedIconButtonColors()
                            }
                        ) {
                            Icon(option.icon, contentDescription = null)
                        }

                }
            }
        }
    }
}
data class EditingOption(
    val icon: ImageVector,
    val isSelected: Boolean,
    val onClick: () -> Unit
)