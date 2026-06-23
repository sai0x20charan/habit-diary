package com.charan.habitdiary.presentation.mediaviewer.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconButtonDefaults.IconButtonWidthOption
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MediaActionButton(
    modifier: Modifier = Modifier,
    onShareClick: () -> Unit,
    onSaveClick: () -> Unit,
    isDownloading : Boolean = false
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp)

    ) {
        MediaActionIcon(
            icon = Icons.Rounded.Share,
            contentDescription = "Share",
            onClick = onShareClick
        )

        if(isDownloading){
            CircularWavyProgressIndicator(
                modifier = Modifier
                    .size(40.dp)
                    .padding(4.dp),
            )
        } else{
            MediaActionIcon(
                icon = Icons.Rounded.Download,
                contentDescription = "Save",
                onClick = onSaveClick
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun MediaActionIcon(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(
            IconButtonDefaults.
            smallContainerSize(IconButtonWidthOption.Wide)
        ),
        shapes = IconButtonDefaults.shapes()
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription
        )
    }
}
