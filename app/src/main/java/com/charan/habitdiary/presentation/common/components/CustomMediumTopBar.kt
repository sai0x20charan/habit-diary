package com.charan.habitdiary.presentation.common.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumFlexibleTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CustomMediumTopBar(
    title : String,
    subTitle : String = "",
    scrollBehavior: TopAppBarScrollBehavior,
    showBackButton : Boolean = false,
    onBackClick : () -> Unit = { },
    actions: @Composable RowScope.() -> Unit = {},

) {
    MediumFlexibleTopAppBar(
        title = {
            Text(title)
        },
        subtitle = {
            if(subTitle.isNotEmpty()){
                Text(subTitle)
            }

        },
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            scrolledContainerColor = MaterialTheme.colorScheme.surface
        ),
        navigationIcon = {
            if(showBackButton){
                    BackButton {
                        onBackClick()
                    }

            }
        },
        actions = actions
    )
}