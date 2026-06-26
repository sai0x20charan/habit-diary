package com.charan.habitdiary.presentation.settings.aboutlibraries

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.charan.habitdiary.R
import com.charan.habitdiary.core.utils.launchUrl
import com.charan.habitdiary.presentation.common.components.CustomListItem
import com.charan.habitdiary.presentation.common.components.CustomMediumTopBar
import com.charan.habitdiary.presentation.common.components.toScreenContentPadding
import com.charan.habitdiary.presentation.settings.aboutlibraries.LicenseBadge
import com.charan.habitdiary.presentation.theme.IndexItem
import com.charan.habitdiary.presentation.theme.indexItemFor
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.ui.compose.LibraryDefaults
import com.mikepenz.aboutlibraries.ui.compose.android.produceLibraries
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import com.mikepenz.aboutlibraries.ui.compose.m3.style.m3LibrariesStyle
import com.mikepenz.aboutlibraries.ui.compose.style.LibrariesStyle
import com.mikepenz.aboutlibraries.ui.compose.style.librariesStyle
import com.mikepenz.aboutlibraries.ui.compose.variant.LibraryBadges
import com.mikepenz.aboutlibraries.ui.compose.variant.LibraryDetailMode
import org.w3c.dom.Text

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AboutLibrariesScreen(
    onBack : () -> Unit
) {
    val libraries by produceLibraries(R.raw.aboutlibraries)
    val scroll = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val libraryDefaultStyle = LibraryDefaults.m3LibrariesStyle()

    val sheetState = rememberBottomSheetState(
        initialValue = SheetValue.Hidden,
    )
    var sheetLibrary by remember { mutableStateOf<Library?>(null) }
    var dialogLibrary by remember { mutableStateOf<Library?>(null) }

    Scaffold(
        modifier = Modifier.nestedScroll(scroll.nestedScrollConnection),
        topBar = {
            CustomMediumTopBar(
                title =  "Libraries",
                showBackButton = true,
                onBackClick = {
                    onBack()
                },
                scrollBehavior = scroll
            )
        }
    ) { innerPadding ->

        LibrariesContainer(
            modifier = Modifier.fillMaxSize(),
            contentPadding = innerPadding.toScreenContentPadding(),
            sheetState = sheetState,
            sheetLibrary = sheetLibrary,
            onSheetLibraryChange = { sheetLibrary = it },
            dialogLibrary = dialogLibrary,
            onDialogLibraryChange = { dialogLibrary = it },
            libraries = libraries,
            detailMode = LibraryDetailMode.Sheet,
            libraryRow = { index, library, expanded, toggle, style ->
                val developers = library.developers.mapNotNull { it.name }.joinToString(", ")
                val version = library.artifactVersion
                val licenses = library.licenses

                CustomListItem(
                    indexItem = libraries?.libraries?.indexItemFor(index) ?: IndexItem.MIDDLE,
                    headLineContent = {
                        Row(modifier = Modifier.padding(bottom = 6.dp),verticalAlignment = Alignment.Top) {
                            Text(
                                text = library.name,
                                style = style.textStyles.nameTextStyle,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f),
                            )
                            if (!version.isNullOrBlank()) {
                                Spacer(Modifier.width(style.dimensions.rowElementSpacing))
                                Text(
                                    text = version,
                                    style = style.textStyles.versionTextStyle.copy(color = style.colors.rowSubtleContent),
                                    maxLines = 1,
                                )
                            }
                        }


                    },
                    supportingContent = {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            if (developers.isNotEmpty()) {
                                Text(
                                    text = developers,
                                    style = libraryDefaultStyle.textStyles.authorTextStyle,
                                    maxLines = 1,
                                    color = style.colors.rowSubtleContent
                                )
                            }
                            if(licenses.isNotEmpty()){
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp),
                                ) {
                                    library.licenses.forEach { license ->
                                        LicenseBadge(
                                            text = license.name,
                                            style = libraryDefaultStyle
                                        )
                                    }
                                }


                            }
                        }
                    },
                    onClick = toggle,
                )

            }


        )
    }
}

@Composable
fun LicenseBadge(
    text: String,
    style : LibrariesStyle
) {
    val container = (style.colors.actionFilledContainer).copy(alpha = 0.22f)
    Box(
        modifier = Modifier
            .clip(style.shapes.licenseTokenShape)
            .background(container)
            .padding(horizontal = 10.dp, vertical = 3.dp),
    ) {
        Text(text = text, style = style.textStyles.licenseTextStyle.copy(color = MaterialTheme.colorScheme.onPrimaryContainer), maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}