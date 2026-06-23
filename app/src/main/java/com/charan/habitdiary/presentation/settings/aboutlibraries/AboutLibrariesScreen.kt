package com.charan.habitdiary.presentation.settings.aboutlibraries

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MediumFlexibleTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import com.charan.habitdiary.R
import com.charan.habitdiary.presentation.common.components.BackButton
import com.charan.habitdiary.presentation.common.components.CustomMediumTopBar
import com.mikepenz.aboutlibraries.ui.compose.android.produceLibraries
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AboutLibrariesScreen(
    onBack : () -> Unit
) {
    val libraries by produceLibraries(R.raw.aboutlibraries)
    val scroll = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(
        topBar = {
            CustomMediumTopBar(
                title =  "About Libraries",
                showBackButton = true,
                onBackClick = {
                    onBack()
                },
                scrollBehavior = scroll

            )
        }
    ) {innerPadding->
        LibrariesContainer(
            modifier = Modifier.padding(innerPadding).nestedScroll(scroll.nestedScrollConnection),
            libraries = libraries
        )

    }

}