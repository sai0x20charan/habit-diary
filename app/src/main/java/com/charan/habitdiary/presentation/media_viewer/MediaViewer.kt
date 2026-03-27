package com.charan.habitdiary.presentation.media_viewer

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
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
import com.charan.habitdiary.presentation.media_viewer.components.MediaActionButton
import com.charan.habitdiary.utils.isVideo
import com.charan.habitdiary.utils.showToast
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import me.saket.telephoto.zoomable.coil3.ZoomableAsyncImage
import kotlin.math.abs

@SuppressLint("LocalContextGetResourceValueCall")
@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class,
    ExperimentalPermissionsApi::class
)
@Composable
fun ImageViewerScreen(
    allImages: List<String>,
    currentImage: String,
    onBack: () -> Unit
) {
    val viewModel = hiltViewModel<MediaViewerViewModel>()
    val state = viewModel.state.collectAsStateWithLifecycle()
    val pageState = rememberPagerState(pageCount = { allImages.size })
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val storagePermission = rememberPermissionState(Manifest.permission.WRITE_EXTERNAL_STORAGE){ isGranted ->
        if(isGranted){
            viewModel.onEvent(
                MediaViewerEvents.DownloadMedia(
                    filePath = allImages[pageState.currentPage]
                )
            )
        }
    }


    LaunchedEffect(currentImage) {
        pageState.scrollToPage(allImages.indexOf(currentImage))
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

                        val chooser = Intent.createChooser(shareIntent, "Share via")
                        context.startActivity(chooser)
                    }

                is MediaViewerEffect.RequestStoragePermission -> {
                    if(storagePermission.status.shouldShowRationale){
                        viewModel.onEvent(MediaViewerEvents.ToggleStoragePermissionRationale(true))

                    } else{
                        storagePermission.launchPermissionRequest()
                    }
                }
            }

        }
    }

    if(state.value.showPermissionDialog){
        RationaleDialog(
            title = stringResource(R.string.storage_permission_required),
            message = stringResource(R.string.storage_permission_description),
            onDismissRequest = {
                viewModel.onEvent(MediaViewerEvents.ToggleStoragePermissionRationale(false))
            },
            onConfirmRequest = {
                viewModel.onEvent(MediaViewerEvents.ToggleStoragePermissionRationale(false))
                viewModel.onEvent(MediaViewerEvents.OpenSettingsForPermission)
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = { BackButton(onBackClick = onBack) },
                actions = {
                    MediaActionButton(
                        onShareClick = {
                            viewModel.onEvent(MediaViewerEvents.ShareMedia(
                                filePath = allImages[pageState.currentPage],
                            ))


                        },
                        onSaveClick = {
                            viewModel.onEvent(MediaViewerEvents.DownloadMedia(
                                filePath = allImages[pageState.currentPage],
                            ))

                        },
                        isDownloading = state.value.isDownloading
                    )
                }
            )
        },
    ) { padding ->
        HorizontalPager(
            state = pageState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) { pageIndex ->
            val imageUrl = allImages[pageIndex]
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
                        videoPath = imageUrl
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
