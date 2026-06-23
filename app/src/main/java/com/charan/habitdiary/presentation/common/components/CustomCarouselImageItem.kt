package com.charan.habitdiary.presentation.common.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.video.VideoFrameDecoder
import com.charan.habitdiary.R
import com.charan.habitdiary.presentation.mediaviewer.components.MiniVideoPlayer
import com.charan.habitdiary.presentation.mediaviewer.components.VideoViewer
import com.charan.habitdiary.core.utils.isVideo
import com.skydoves.cloudy.cloudy

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CustomCarouselImageItem(
    mediaPaths: List<String>,
    onRemoveClick: (String) -> Unit,
    isEdit: Boolean = false,
    modifier: Modifier = Modifier,
    onImageOpen: (String) -> Unit = {}
) {
    if (mediaPaths.isEmpty()) return

    val imageLoader = rememberMediaImageLoader()

    HorizontalMultiBrowseCarousel(
        state = rememberCarouselState { mediaPaths.size },
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp),
        itemSpacing = 8.dp,
        preferredItemWidth = 200.dp,
    ) { index ->
        val item = mediaPaths[index]
        val isVideo = item.isVideo()

        Card(
            modifier = Modifier
                .height(200.dp)
                .maskClip(MaterialTheme.shapes.large),
            shape = MaterialTheme.shapes.large,
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                AsyncImage(
                    model = item,
                    imageLoader = imageLoader,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .cloudy(radius = 50)
                        .alpha(0.6f),
                    contentScale = ContentScale.Crop
                )
                if (isVideo) {
                    MiniVideoPlayer(
                        videoPath = item,
                        onVideoClick = {
                            onImageOpen(item)
                        }
                    )
                } else {
                    AsyncImage(
                        model = item,
                        imageLoader = imageLoader,
                        contentDescription = stringResource(R.string.media_preview),
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable { onImageOpen(item) },
                        contentScale = ContentScale.Fit
                    )
                }
                if (isEdit) {
                    FilledTonalIconButton(
                        onClick = { onRemoveClick(item) },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .size(26.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = stringResource(R.string.remove_button),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
@Composable
private fun rememberMediaImageLoader(): ImageLoader {
    val context = LocalContext.current
    return remember {
        ImageLoader.Builder(context)
            .components {
                add(VideoFrameDecoder.Factory())
            }
            .build()
    }
}



@Preview(showBackground = true)
@Composable
fun CustomCarouselImageItemPreview() {
    val sampleImages = listOf(
        "https://picsum.photos/200/300",
        "https://picsum.photos/300/300",
        "https://picsum.photos/400/300"
    )

    // Preview Context
    Box(modifier = Modifier.padding(vertical = 20.dp)) {
        CustomCarouselImageItem(
            mediaPaths = sampleImages,
            onRemoveClick = {},
            isEdit = true
        )
    }
}