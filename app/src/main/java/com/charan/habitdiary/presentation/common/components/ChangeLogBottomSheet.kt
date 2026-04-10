package com.charan.habitdiary.presentation.common.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.charan.habitdiary.R
import com.charan.habitdiary.utils.getAppVersion

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ChangeLogBottomSheet(
    onDismiss : () -> Unit
) {
    val context = LocalContext.current
    val changelogBlocks = remember(context) {
        runCatching {
            context.assets.open("CHANGELOG.md").bufferedReader().use { it.readText() }
        }.getOrDefault("No changelog available.")
            .toChangeLogBlocks()
    }
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),

    ) {
        Column(
            modifier =
                Modifier.fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
                    .navigationBarsPadding()
        ) {
            Text(
                text = stringResource(R.string.whats_new),
                style = MaterialTheme.typography.headlineMediumEmphasized.copy(fontWeight = FontWeight.SemiBold),
            )

            Text(
                text = stringResource(R.string.version_text, getAppVersion()),
                style = MaterialTheme.typography.labelLarge,
            )
            Spacer(modifier = Modifier.padding(bottom = 30.dp))
            ChangeLogMarkdownContent(
                blocks = changelogBlocks,
                modifier = Modifier.fillMaxWidth()
            )
        }


    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ChangeLogMarkdownContent(
    blocks: List<ChangeLogBlock>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        blocks.forEach { block ->
            when (block) {
                is ChangeLogBlock.Heading2 -> {
                    Text(
                        text = block.text,
                        style = MaterialTheme.typography.titleLargeEmphasized.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        modifier = Modifier.padding(bottom = 15.dp)
                    )
                }
                is ChangeLogBlock.BulletItem -> {
                    Row(modifier = Modifier.padding(bottom = 5.dp)) {
                        Text(
                            text = "\u2022",
                            style = MaterialTheme.typography.bodyLargeEmphasized,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = block.text,
                            style = MaterialTheme.typography.bodyLargeEmphasized
                        )
                    }
                }
                is ChangeLogBlock.Paragraph -> {
                    Text(
                        text = block.text,
                        style = MaterialTheme.typography.bodyLargeEmphasized,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }
        }
    }
}

private sealed interface ChangeLogBlock {
    data class Heading2(val text: String) : ChangeLogBlock
    data class BulletItem(val text: String) : ChangeLogBlock
    data class Paragraph(val text: String) : ChangeLogBlock
}

private fun String.toChangeLogBlocks(): List<ChangeLogBlock> {
    val blocks = mutableListOf<ChangeLogBlock>()
    val paragraphBuffer = mutableListOf<String>()

    fun flushParagraph() {
        if (paragraphBuffer.isNotEmpty()) {
            blocks.add(ChangeLogBlock.Paragraph(paragraphBuffer.joinToString(" ").trim()))
            paragraphBuffer.clear()
        }
    }

    lineSequence().forEach { line ->
        val trimmed = line.trim()
        when {
            trimmed.isEmpty() -> flushParagraph()
            trimmed.startsWith("## ") -> {
                flushParagraph()
                blocks.add(ChangeLogBlock.Heading2(trimmed.removePrefix("## ").trim()))
            }
            trimmed.startsWith("- ") || trimmed.startsWith("* ") -> {
                flushParagraph()
                blocks.add(
                    ChangeLogBlock.BulletItem(
                        trimmed.drop(2).trim()
                    )
                )
            }
            else -> paragraphBuffer.add(trimmed)
        }
    }
    flushParagraph()

    return blocks
}
