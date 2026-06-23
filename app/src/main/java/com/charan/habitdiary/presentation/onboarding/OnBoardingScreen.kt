package com.charan.habitdiary.presentation.onboarding


import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.charan.habitdiary.R

import kotlinx.coroutines.flow.distinctUntilChanged

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun OnBoardingScreen(
    onNavigateToHomeScreen: () -> Unit
) {

    val viewModel = hiltViewModel<OnBoardingViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState(pageCount = { state.onBoardingPage.size })

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                OnBoardingEffect.NavigateToHome -> {
                    onNavigateToHomeScreen()
                }
                is OnBoardingEffect.OnScrollToPage -> {
                    pagerState.animateScrollToPage(effect.page)
                }
            }
        }
    }
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .distinctUntilChanged()
            .collect { page ->
                viewModel.onEvent(OnBoardingEvent.PageChanged(page))
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            OnBoardingPageItem(
                pageData = state.onBoardingPage[page],
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth().navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PagerIndicator(currentPage = state.currentPage, size = state.onBoardingPage.size)

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    viewModel.onEvent(OnBoardingEvent.NextPage)
                },
                modifier = Modifier
                    .fillMaxWidth(),
                shapes = ButtonDefaults.shapes(
                    shape = MaterialTheme.shapes.extraLargeIncreased
                ),

            ) {
                Text(
                    text = if (state.currentPage == state.onBoardingPage.lastIndex)
                        stringResource(R.string.getting_started)
                    else
                        stringResource(R.string.next),
                    style = MaterialTheme.typography.labelLargeEmphasized
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun OnBoardingPageItem(pageData: OnBoardingPage) {
    val infiniteTransition = rememberInfiniteTransition(label = "infinite")
    val progress = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            tween(9500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "progress"
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.size(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .graphicsLayer {
                        rotationZ = progress.value
                    }
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        shape = MaterialShapes.Cookie9Sided.toShape()
                    )
            )
            Image(
                painter = painterResource(id = pageData.imageRes),
                contentDescription = null,
                modifier = Modifier
                    .size(130.dp),
                contentScale = ContentScale.Fit
            )
        }


        Spacer(modifier = Modifier.height(48.dp))
        Text(
            text = stringResource(pageData.title),
            style = MaterialTheme.typography.headlineMediumEmphasized,
            textAlign = TextAlign.Center,
            modifier = Modifier
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(pageData.description),
            style = MaterialTheme.typography.bodyLargeEmphasized,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .padding(horizontal = 12.dp)
        )
    }
}

@Composable
fun PagerIndicator(
    currentPage: Int,
    size: Int
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(20.dp)
    ) {
        repeat(size) { index ->
            val isSelected = currentPage == index
            val width by animateDpAsState(
                targetValue = if (isSelected) 32.dp else 12.dp,
                animationSpec = spring(
                    dampingRatio = 0.7f,
                    stiffness = 300f
                ),
                label = "indicatorWidth"
            )

            val color = if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }

            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .height(12.dp)
                    .width(width)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OnBoardingScreenPreview() {
    MaterialTheme {
        OnBoardingScreen(
            onNavigateToHomeScreen = {}
        )
    }
}