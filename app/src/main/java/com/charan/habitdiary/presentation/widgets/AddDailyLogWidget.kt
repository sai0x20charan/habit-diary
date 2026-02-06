package com.charan.habitdiary.presentation.widgets

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalSize
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.charan.habitdiary.DeepLinkHandler
import com.charan.habitdiary.R
import com.charan.habitdiary.utils.DateUtil

class AddDailyLogWidget : GlanceAppWidget() {

    override val sizeMode = SizeMode.Responsive(
        setOf(
            DpSize(100.dp, 100.dp),
            DpSize(200.dp, 100.dp),
            DpSize(200.dp, 200.dp),
            DpSize(250.dp, 250.dp),
            DpSize(100.dp, 200.dp)
        )
    )

    override suspend fun provideGlance(
        context: Context,
        id: GlanceId
    ) {
        provideContent {
            AddDailyLogWidgetContent()
        }
    }
}

class AddDailyLogWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = AddDailyLogWidget()
}

@Composable
private fun AddDailyLogWidgetContent() {
    val size = LocalSize.current

    val isHeightOneCell = size.height < 140.dp
    val isWidthTwoCells = size.width <= 180.dp

    val openDailyLog = Intent(
        Intent.ACTION_VIEW,
        "${DeepLinkHandler.BASE_URL}${DeepLinkHandler.DAILYLOG_URI}".toUri()
    )

    val captureImage = Intent(
        Intent.ACTION_VIEW,
        "${DeepLinkHandler.BASE_URL}${DeepLinkHandler.DAILYLOG_URI}?${DeepLinkHandler.CAPTURE_IMAGE_QUERY}=true".toUri()
    )

    val captureVideo = Intent(
        Intent.ACTION_VIEW,
        "${DeepLinkHandler.BASE_URL}${DeepLinkHandler.DAILYLOG_URI}?${DeepLinkHandler.CAPTURE_VIDEO_QUERY}=true".toUri()
    )

    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .appWidgetBackground()
            .background(GlanceTheme.colors.background)
            .then(
                if (!isHeightOneCell) {
                    GlanceModifier.padding(16.dp)
                } else {
                    GlanceModifier.padding(horizontal = 8.dp, vertical = 4.dp)
                }
            ),
        verticalAlignment = Alignment.Top,
        horizontalAlignment = Alignment.Start
    ) {

        Column(modifier = GlanceModifier.fillMaxWidth()) {

            if (!isHeightOneCell) {
                Text(
                    text = DateUtil.getTodayDayAndDate().uppercase(),
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = GlanceTheme.colors.primary
                    )
                )
            }

            if (!isWidthTwoCells && !isHeightOneCell) {
                Spacer(GlanceModifier.height(4.dp))
                Text(
                    text = "How are you feeling?",
                    style = TextStyle(
                        fontWeight = FontWeight.Medium,
                        fontSize = 20.sp,
                        color = GlanceTheme.colors.onSurface
                    )
                )
            }
        }

        if (!isHeightOneCell) {
            Spacer(GlanceModifier.defaultWeight())
        } else {
            Spacer(GlanceModifier.height(6.dp))
        }

        Row(
            modifier = GlanceModifier
                .fillMaxWidth()
                .background(GlanceTheme.colors.outline)
                .cornerRadius(25.dp)
                .padding(1.dp)
        ) {
            Row(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .background(GlanceTheme.colors.surface)
                    .cornerRadius(24.dp)
                    .clickable(actionStartActivity(openDailyLog))
                    .padding(if (isHeightOneCell) 10.dp else 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment =
                    if (isHeightOneCell) Alignment.CenterHorizontally
                    else if (!isWidthTwoCells) Alignment.CenterHorizontally
                    else Alignment.Start
            ) {

                Text(
                    text = "Tap to write entry...",
                    modifier = GlanceModifier.defaultWeight(),
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurface,
                        fontSize = if (isHeightOneCell) 12.sp else 14.sp
                    ),
                    maxLines = 1
                )

                Image(
                    provider = ImageProvider(R.drawable.outline_ink_pen_24),
                    contentDescription = null,
                    modifier = GlanceModifier.size(
                        if (isHeightOneCell) 18.dp else 20.dp
                    ),
                    colorFilter = ColorFilter.tint(
                        GlanceTheme.colors.onPrimaryContainer
                    )
                )
            }
        }

        if (!isHeightOneCell) {
            Spacer(GlanceModifier.defaultWeight())
        } else {
            Spacer(GlanceModifier.height(6.dp))
        }

        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            AdaptiveButton(
                modifier = GlanceModifier.defaultWeight(),
                text = "Photo",
                onClickAction = { actionStartActivity(captureImage) },
                isWidthTwoCells = isWidthTwoCells,
                isHeightOneCell = isHeightOneCell,
                icon = ImageProvider(R.drawable.rounded_add_a_photo_24)
            )

            Spacer(GlanceModifier.width(12.dp))

            AdaptiveButton(
                modifier = GlanceModifier.defaultWeight(),
                text = "Video",
                onClickAction = { actionStartActivity(captureVideo) },
                isWidthTwoCells = isWidthTwoCells,
                isHeightOneCell = isHeightOneCell,
                icon = ImageProvider(R.drawable.outline_video_call_24)
            )
        }
    }
}

@Composable
private fun AdaptiveButton(
    modifier: GlanceModifier,
    text: String,
    onClickAction: () -> Unit,
    isWidthTwoCells: Boolean,
    isHeightOneCell: Boolean,
    icon: ImageProvider
) {

    val showText = !isWidthTwoCells
    val height = when {
        isHeightOneCell && showText -> 40.dp
        isHeightOneCell && !showText -> 36.dp
        isWidthTwoCells -> 40.dp
        else -> 48.dp
    }

    val padding = if (isHeightOneCell) 6.dp else 12.dp

    Box(
        modifier = modifier
            .height(height)
            .background(GlanceTheme.colors.secondaryContainer)
            .cornerRadius(height / 2)
            .clickable(onClickAction),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                provider = icon,
                contentDescription = null,
                modifier = GlanceModifier.size(18.dp),
                colorFilter = ColorFilter.tint(
                    GlanceTheme.colors.onSecondaryContainer
                )
            )

            if (showText) {
                Spacer(GlanceModifier.width(8.dp))
                Text(
                    text = text,
                    style = TextStyle(
                        color = GlanceTheme.colors.onSecondaryContainer,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                )
            }
        }
    }
}

