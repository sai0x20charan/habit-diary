import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.*
import androidx.compose.material3.carousel.HorizontalUncontainedCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.charan.habitdiary.presentation.common.components.CustomCarouselImageItem
import kotlinx.coroutines.flow.merge

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DayLogEntryItem(
    time: String,
    note: String,
    mediaPath: List<String> = emptyList(),
    habitName: String = "",
    onClick: () -> Unit = {},
    onImageClick : (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = time,
            style = MaterialTheme.typography.labelLargeEmphasized.copy(
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            ),
            modifier = Modifier.width(65.dp).padding(top = 5.dp)
        )

        LogEntryCard(
            note = note,
            mediaPath = mediaPath,
            habitName = habitName,
            onClick = onClick,
            modifier = Modifier.weight(1f),
            onImageClick = {
                onImageClick(it)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LogEntryCard(
    note: String,
    mediaPath: List<String> = emptyList(),
    habitName: String = "",
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onImageClick : (String) -> Unit
) {
    ElevatedCard(
        onClick = onClick,
        modifier = modifier
            .then(
                if (!(habitName.isEmpty())) {
                    Modifier.border(
                        width = 0.5.dp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        shape = CardDefaults.shape
                    )
                } else {
                    Modifier
                }
            )
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            if(habitName.isNotEmpty()){
                Text(
                    text = stringResource(com.charan.habitdiary.R.string.habit_completed),
                    style = MaterialTheme.typography.labelSmallEmphasized.copy(
                        color = MaterialTheme.colorScheme.primary
                    )
                )
                Text(
                    text = habitName,
                    style = MaterialTheme.typography.titleMediumEmphasized.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
            if (mediaPath.isNotEmpty()) {
                CustomCarouselImageItem(
                    mediaPaths = mediaPath,
                    onRemoveClick = {},
                    onImageOpen = {
                        onImageClick(it)
                    }
                )

            }
            if (note.isNotEmpty()) {
                Text(
                    text = note,
                    style = MaterialTheme.typography.bodyLargeEmphasized.copy(
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = MaterialTheme.typography.bodyLargeEmphasized.lineHeight * 1.2f
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CarouselImageItem(
    mediaPath : List<String>
) {
    HorizontalUncontainedCarousel(
        state = rememberCarouselState { mediaPath.count() },
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(MaterialTheme.shapes.medium),
        itemWidth = 186.dp,
        itemSpacing = 8.dp,
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) { index->
        val item = mediaPath[index]
        AsyncImage(
            model = item,
            contentDescription = "Log Entry Image",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.surfaceContainerHighest),
            contentScale = ContentScale.Fit
        )

    }

}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DayLogEntryItemPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceContainerLow)
                .padding(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DayLogEntryItem(
                time = "09:30 AM",
                note = "Completed 30 minutes of running followed by 15 push-ups. Feeling energized and ready to tackle the day!",
                mediaPath = listOf( "https://picsum.photos/400/200","https://picsum.photos/400/200","https://picsum.photos/400/200"),

                habitName = "Morning Workout",
                onImageClick = {}
            )

            DayLogEntryItem(
                time = "02:15 PM",
                note = "Quick meditation session during lunch break. Really helped clear my mind.",

                habitName = "Mindfulness",
                onImageClick = {}
            )

            DayLogEntryItem(
                time = "08:45 PM",
                note = "",

                habitName = "Evening Stretch",
                onImageClick = {}
            )

            DayLogEntryItem(
                time = "11:20 AM",
                note = "Had a great conversation with the team about the new project direction.",
                onImageClick = {}

            )
        }
    }
}