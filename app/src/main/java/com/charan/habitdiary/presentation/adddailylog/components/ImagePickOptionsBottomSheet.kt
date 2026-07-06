package com.charan.habitdiary.presentation.adddailylog.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Camera
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.Videocam
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.charan.habitdiary.R
import com.charan.habitdiary.presentation.common.components.CustomListItem
import com.charan.habitdiary.presentation.theme.indexItemFor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImagePickOptionsBottomSheet(
    onImageFromGalleryClick : () -> Unit,
    onImageFromCameraClick : () -> Unit,
    onDismissRequest : () ->Unit,
    onCaptureVideo : () -> Unit = {},
    sheetState: SheetState
) {
    val imagePickOptions = listOf(
        ImagePickOptions(
            title = stringResource(R.string.take_a_photo),
            icon = Icons.Rounded.Camera,
            onClick = onImageFromCameraClick
        ),
        ImagePickOptions(
            title = stringResource(R.string.record_a_video),
            icon = Icons.Rounded.Videocam,
            onClick = onCaptureVideo
        ),
        ImagePickOptions(
            title = stringResource(R.string.choose_from_gallery),
            icon = Icons.Rounded.Image,
            onClick = onImageFromGalleryClick
        ),
    )

    ModalBottomSheet(
        onDismissRequest = {
            onDismissRequest()
        },
        sheetState = sheetState

    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            imagePickOptions.forEachIndexed { index, option ->
                CustomListItem(
                    headLineContent = {
                        Text(option.title)
                    },
                    indexItem = imagePickOptions.indexItemFor(index),
                    onClick = {
                        option.onClick()
                    },
                    leadingContent = {
                        Icon(
                            imageVector = option.icon,
                            contentDescription = option.title,
                        )
                    }

                )
            }


        }
    }
}

data class ImagePickOptions(
    val title : String,
    val icon : ImageVector,
    val onClick : () -> Unit
)