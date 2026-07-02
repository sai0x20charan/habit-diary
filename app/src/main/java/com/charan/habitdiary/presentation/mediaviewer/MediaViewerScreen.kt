package com.charan.habitdiary.presentation.mediaviewer

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.List
import androidx.compose.material.icons.rounded.OpenInNew
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.charan.habitdiary.R
import com.charan.habitdiary.presentation.common.components.BackButton
import com.charan.habitdiary.presentation.common.components.RationaleDialog
import com.charan.habitdiary.presentation.common.model.ToastMessage
import com.charan.habitdiary.presentation.common.model.MediaItemUIModel
import com.charan.habitdiary.presentation.mediaviewer.components.MediaActionButton
import com.charan.habitdiary.presentation.mediaviewer.components.VideoViewer
import com.charan.habitdiary.core.utils.isVideo
import com.charan.habitdiary.core.utils.showToast
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import me.saket.telephoto.zoomable.coil3.ZoomableAsyncImage
import kotlin.math.abs

@SuppressLint("LocalContextGetResourceValueCall")
@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class,
    ExperimentalPermissionsApi::class, ExperimentalMaterial3ExpressiveApi::class
)
@Composable
fun MediaViewerScreen(
    allImages: List<MediaItemUIModel>,
    currentImage: MediaItemUIModel,
    onBack: () -> Unit,
    showLogEntryButton: Boolean = true,
    onNavigateToDailyLog: ((Long, LocalDate?) -> Unit)? = null
) {
    val viewModel = hiltViewModel<MediaViewerViewModel, MediaViewerViewModel.Factory>(
        creationCallback = { factory ->
            factory.create(allImages, currentImage)
        }
    )
    val state by viewModel.state.collectAsStateWithLifecycle()
    val pageState = rememberPagerState(pageCount = { state.images.size}, initialPage = state.currentIndex)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val storagePermission = rememberPermissionState(Manifest.permission.WRITE_EXTERNAL_STORAGE){ isGranted ->
        if(isGranted){
            state.images.getOrNull(pageState.currentPage)?.let { mediaItem ->
                viewModel.onEvent(
                    MediaViewerEvent.DownloadMedia(
                        filePath = mediaItem.mediaPath
                    )
                )
            }
        }
    }
    LaunchedEffect(pageState.currentPage) {
        if (state.images.isNotEmpty() && pageState.currentPage != state.currentIndex) {
            viewModel.onEvent(MediaViewerEvent.OnIndexChange(pageState.currentPage))
        }
    }
    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when(effect){
                is MediaViewerEffect.ShowToast -> {
                    context.showToast(effect.message)
                }

                is MediaViewerEffect.ShareMedia -> {
                        val shareIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_STREAM, effect.filePath)
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            type = context.contentResolver.getType(effect.filePath)
                        }

                        val chooser = Intent.createChooser(shareIntent, context.getString(R.string.share_via))
                        context.startActivity(chooser)
                    }

                is MediaViewerEffect.RequestStoragePermission -> {
                    if(storagePermission.status.shouldShowRationale){
                        viewModel.onEvent(MediaViewerEvent.ToggleStoragePermissionRationale(true))

                    } else{
                        storagePermission.launchPermissionRequest()
                    }
                }
            }

        }
    }

    if(state.showPermissionDialog){
        RationaleDialog(
            title = stringResource(R.string.storage_permission_required),
            message = stringResource(R.string.storage_permission_description),
            onDismissRequest = {
                viewModel.onEvent(MediaViewerEvent.ToggleStoragePermissionRationale(false))
            },
            onConfirmRequest = {
                viewModel.onEvent(MediaViewerEvent.ToggleStoragePermissionRationale(false))
                viewModel.onEvent(MediaViewerEvent.OpenSettingsForPermission)
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = { BackButton(onBackClick = onBack) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                ,
                contentAlignment = Alignment.BottomCenter
            ) {
                HorizontalFloatingToolbar(
                    expanded = true,
                    modifier = Modifier
                        .navigationBarsPadding()
                        .padding(bottom = FloatingToolbarDefaults.ContentPadding.calculateBottomPadding()),
                    colors = FloatingToolbarDefaults.standardFloatingToolbarColors(),
                    trailingContent = {
                        if (showLogEntryButton) {
                            state.images.getOrNull(pageState.currentPage)?.let { currentMediaItem ->
                                if (currentMediaItem.logId != null && onNavigateToDailyLog != null) {
                                    FilledTonalButton(
                                        onClick = {
                                            onNavigateToDailyLog(
                                                currentMediaItem.logId,
                                                currentMediaItem.logDate
                                            )
                                        },
                                    ) {
                                        Text(stringResource(R.string.show_entry))
                                    }
                                }
                            }
                        }
                    }


                ) {
                    MediaActionButton(
                        onShareClick = {
                            state.images.getOrNull(pageState.currentPage)?.let { mediaItem ->
                                viewModel.onEvent(MediaViewerEvent.ShareMedia(
                                    filePath = mediaItem.mediaPath,
                                ))
                            }
                        },
                        onSaveClick = {
                            state.images.getOrNull(pageState.currentPage)?.let { mediaItem ->
                                viewModel.onEvent(MediaViewerEvent.DownloadMedia(
                                    filePath = mediaItem.mediaPath
                                ))
                            }
                        },
                        isDownloading = state.isDownloading
                    )
                }
            }
        }
    ) { padding ->
        HorizontalPager(
            state = pageState,
            modifier = Modifier
                .fillMaxSize()
        ) { pageIndex ->
            val imageUrl = state.images[pageIndex].mediaPath
            val offsetY = remember { Animatable(0f) }
            val configuration = LocalWindowInfo.current.containerSize
            val density = LocalDensity.current
            val dragLimit = with(density) { configuration.height.dp.toPx()} /11

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectVerticalDragGestures(
                            onVerticalDrag = { change, dragAmount ->
                                change.consume()
                                val progress = abs(offsetY.value) / dragLimit
                                val resistance = (1f - progress).coerceIn(0.25f, 1f)
                                val resistedDrag = dragAmount * resistance

                                val newOffset = offsetY.value + resistedDrag
                                val clampedOffset = newOffset.coerceIn(-dragLimit, dragLimit)

                                scope.launch {
                                    offsetY.snapTo(clampedOffset)
                                }
                            },
                            onDragEnd = {
                                scope.launch {
                                    if (abs(offsetY.value) > with(density) { 150.dp.toPx() }) {
                                        onBack()
                                    } else {
                                        offsetY.animateTo(
                                            0f,
                                            spring(stiffness = Spring.StiffnessMediumLow)
                                        )
                                    }
                                }
                            }
                        )
                    }
                    .graphicsLayer {
                        translationY = offsetY.value
                    }
            ) {
                if(imageUrl.isVideo()){
                    VideoViewer(
                        showControls = true,
                        videoPath = imageUrl,
                        controlsPadding = PaddingValues(
                            bottom = padding.calculateBottomPadding()
                        )
                    )
                } else {
                    ZoomableAsyncImage(
                        model = imageUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
    }
}
