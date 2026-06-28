package com.charan.habitdiary.presentation.mediaviewer.components

import android.view.TextureView
import androidx.annotation.OptIn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeOff
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.VolumeOff
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util.getStringForTime
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import androidx.media3.ui.compose.PlayerSurface
import androidx.media3.ui.compose.SURFACE_TYPE_TEXTURE_VIEW
import androidx.media3.ui.compose.SurfaceType
import androidx.media3.ui.compose.material3.Player
import androidx.media3.ui.compose.material3.buttons.MuteButton
import androidx.media3.ui.compose.material3.buttons.PlayPauseButton
import androidx.media3.ui.compose.state.rememberMuteButtonState
import androidx.media3.ui.compose.state.rememberPlayPauseButtonState
import androidx.media3.ui.compose.state.rememberProgressStateWithTickInterval
import com.charan.habitdiary.core.utils.toFormatTimeMs
import androidx.compose.ui.res.stringResource
import com.charan.habitdiary.R

@OptIn(UnstableApi::class, ExperimentalMaterial3Api::class)
@Composable
fun VideoViewer(
    showControls : Boolean = true,
    videoPath : String,
    controlsPadding : PaddingValues = PaddingValues(0.dp)
) {
    val context = LocalContext.current
    val player = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(videoPath.toUri())
            setMediaItem(mediaItem)
            prepare()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            player.clearVideoSurface()
            player.release()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Player(
            player = player,
            showControls = false
        )

        if(showControls) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
                    .padding(controlsPadding)
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PlayPauseButton(
                        player = player,
                        modifier = Modifier,
                    )
                    VideoTimeText(player)
                    MuteButton(
                        player = player
                    )

                }

                VideoScrubber(player)
            }
        }
    }
}
@OptIn(UnstableApi::class)
@Composable
private fun VideoTimeText(
    player: Player
) {
    val timeState = rememberProgressStateWithTickInterval(player)

    Text(
        text = "${timeState.currentPositionMs.toFormatTimeMs()} / ${timeState.durationMs.toFormatTimeMs()}",
        modifier = Modifier.padding(8.dp)
    )
}


@Composable
@OptIn(UnstableApi::class)
private fun VideoScrubber(
    player: Player
) {
    val timeState = rememberProgressStateWithTickInterval(player)
    Slider(
        value = timeState.currentPositionMs.toFloat(),
        valueRange = 0f..timeState.durationMs.coerceAtLeast(1L).toFloat(),
        onValueChange = { newPos ->
            player.seekTo(newPos.toLong())
        },
        modifier = Modifier.padding(10.dp)
    )


}


@OptIn(
    UnstableApi::class
)
@ExperimentalMaterial3ExpressiveApi
@Composable
fun MiniVideoPlayer(
    videoPath : String,
    onVideoClick : () -> Unit
) {
    val context = LocalContext.current
    val player = remember {
        ExoPlayer.Builder(context)
            .build().apply {
            val mediaItem = MediaItem.fromUri(videoPath.toUri())
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = false
            mute()
        }
    }
    val muteState = rememberMuteButtonState(player)
    val pausePlayState = rememberPlayPauseButtonState(player)

    DisposableEffect(Unit) {
        onDispose {
            player.clearVideoSurface()
            player.release()
        }
    }
    Box(
        modifier = Modifier
    ) {
        Player(
            player = player,
            modifier = Modifier
                .fillMaxSize()
                .clickable(true) {
                    onVideoClick()
                },
            showControls = false,

        )
        FilledTonalIconButton(
            onClick = {
                pausePlayState.onClick()
            },
            modifier = Modifier
                .align(Alignment.Center)
                .size(30.dp)
            ,
            colors = IconButtonDefaults.filledTonalIconButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.5f)
            ),
            shapes = IconButtonDefaults.shapes()
        ) {
            Icon(
                imageVector = if(pausePlayState.showPlay) Icons.Rounded.PlayArrow else Icons.Rounded.Pause,
                contentDescription = stringResource(R.string.play_or_pause_video)
            )
        }
        FilledIconButton(
            onClick = {
                muteState.onClick()
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(8.dp)
                .size(IconButtonDefaults.smallIconSize)


            ,
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            shapes = IconButtonDefaults.shapes(),

        ) {
            Icon(
                imageVector = if(muteState.showMuted) Icons.AutoMirrored.Rounded.VolumeOff else Icons.AutoMirrored.Rounded.VolumeUp,
                contentDescription = stringResource(R.string.toggle_mute),
                modifier = Modifier.padding(4.dp)
            )
        }

    }
}
