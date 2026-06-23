package com.charan.habitdiary.presentation.settings

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.biometric.BiometricManager
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.BugReport
import androidx.compose.material.icons.rounded.Campaign
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.ColorLens
import androidx.compose.material.icons.rounded.Contrast
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Fingerprint
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.FontDownload
import androidx.compose.material.icons.rounded.Upload
import androidx.compose.material.icons.rounded.WorkspacePremium
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MediumFlexibleTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.charan.habitdiary.R
import com.charan.habitdiary.data.repository.impl.BackupRepositoryImpl.Companion.FILE_TYPE
import com.charan.habitdiary.presentation.common.components.ChangeLogBottomSheet
import com.charan.habitdiary.presentation.common.components.CustomListItem
import com.charan.habitdiary.presentation.common.components.CustomMediumTopBar
import com.charan.habitdiary.presentation.common.model.ToastMessage
import com.charan.habitdiary.presentation.settings.components.SectionHeader
import com.charan.habitdiary.presentation.settings.components.SettingsRowItem
import com.charan.habitdiary.presentation.settings.components.SettingsSwitchItem
import com.charan.habitdiary.presentation.settings.components.ThemeOptionButtonGroup
import com.charan.habitdiary.presentation.theme.IndexItem
import com.charan.habitdiary.core.utils.showToast
import kotlinx.coroutines.flow.collectLatest
import com.charan.habitdiary.core.utils.launchFeedbackEmail
import com.charan.habitdiary.core.utils.launchUrl

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsScreen(
    navigateToAboutLibraries : () -> Unit
) {
    val viewModel = hiltViewModel<SettingsViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val context = LocalContext.current
    val createDocument = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument(
            FILE_TYPE
        )
    ) {
        if(it != null){
            viewModel.onEvent(SettingsEvent.BackupData(it))
        }
    }
    val pickedFile =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenDocument()
        ) { uri ->
            if(uri !=null) {
                viewModel.onEvent(SettingsEvent.RestoreBackup(uri))
            }
        }
    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when(effect){
                SettingsEffect.NavigateToLibrariesScreen -> {
                    navigateToAboutLibraries()
                }

                is SettingsEffect.LaunchCreateDocument -> {
                    createDocument.launch(effect.fileName)
                }

                SettingsEffect.OnBack -> {

                }

                is SettingsEffect.ShowToast -> {
                    context.showToast(effect.message)

                }

                SettingsEffect.LaunchOpenDocument -> {
                    pickedFile.launch(arrayOf(FILE_TYPE))
                }

                is SettingsEffect.OpenUrl -> {
                    context.launchUrl(effect.url)
                }

                SettingsEffect.LaunchSendFeedbackEmail ->{
                    context.launchFeedbackEmail()
                }
            }
        }
    }

    if(state.showChangeLog){
        ChangeLogBottomSheet(
            onDismiss = {
                viewModel.onEvent(SettingsEvent.OnToggleChangeLogClick)
            }
        )
    }
    Scaffold(
        topBar = {
            CustomMediumTopBar(
                title = stringResource(R.string.settings),
                scrollBehavior = scrollBehavior
            )

        }
    ) {innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal =  16.dp)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        ) {
            item {
                SectionHeader(
                    title = stringResource(R.string.appearance),
                )
                CustomListItem(
                    indexItem = IndexItem.FIRST,
                    headLineContent = {
                        Text(stringResource(R.string.theme))

                    },
                    supportingContent = {
                        ThemeOptionButtonGroup(
                            selectedTheme = state.selectedThemeOption
                        ) {
                            viewModel.onEvent(SettingsEvent.OnThemeChange(it))
                        }
                    },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Rounded.Contrast,
                            contentDescription = stringResource(R.string.theme)
                        )
                    }

                )
                SettingsSwitchItem(
                    title = stringResource(R.string.use_dynamic_color),
                    index = IndexItem.MIDDLE,
                    isChecked = state.isDynamicColorsEnabled,
                    onCheckedChange = {
                        viewModel.onEvent(SettingsEvent.OnDynamicColorsChange(it))
                    },
                    leadingIcon = Icons.Rounded.ColorLens
                )

                SettingsSwitchItem(
                    title = stringResource(R.string.use_system_font),
                    index = IndexItem.LAST,
                    isChecked = state.isSystemFontEnabled,
                    onCheckedChange = {
                        viewModel.onEvent(SettingsEvent.OnUseSystemFontChange(it))
                    },
                    leadingIcon = Icons.Rounded.FontDownload

                )



            }

            item {
                SectionHeader(
                    stringResource(R.string.general)
                )

                SettingsSwitchItem(
                    title = stringResource(R.string.biometric_lock),
                    index = IndexItem.FIRST,
                    isChecked = state.isBiometricLockEnabled,
                    onCheckedChange = {
                        viewModel.onEvent(SettingsEvent.OnBiometricLockChange(it))
                    },
                    leadingIcon = Icons.Rounded.Fingerprint
                )

                SettingsSwitchItem(
                    title = stringResource(R.string.hour_format_24),
                    index = IndexItem.LAST,
                    isChecked = state.is24HourFormat,
                    onCheckedChange = {
                        viewModel.onEvent(SettingsEvent.OnTimeFormatChange(it))
                    },
                    leadingIcon = Icons.Rounded.AccessTime
                )
            }

            item {
                SectionHeader(
                    stringResource(R.string.backup_restore)
                )
                CustomListItem(
                    indexItem = IndexItem.FIRST,
                    headLineContent = {
                        Text(stringResource(R.string.export_data))
                    },
                    onClick = {
                        viewModel.onEvent(SettingsEvent.OnExportDataClick)
                    },
                    trailingContent = {
                        if(state.isExporting){
                            ContainedLoadingIndicator(
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Rounded.Upload,
                            contentDescription = stringResource(R.string.export_data)
                        )
                    }

                )
                CustomListItem(
                    indexItem = IndexItem.LAST,
                    headLineContent = {
                        Text(stringResource(R.string.import_data))
                    },
                    onClick = {
                        viewModel.onEvent(SettingsEvent.OnImportDataClick)
                    },
                    trailingContent = {
                        if(state.isImporting){
                            ContainedLoadingIndicator(
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Rounded.Download,
                            contentDescription = stringResource(R.string.import_data)
                        )
                    }
                )
            }

            item {
                SectionHeader(
                    stringResource(R.string.support)
                )
                CustomListItem(
                    indexItem = IndexItem.FIRST,
                    headLineContent = {
                        Text(stringResource(R.string.send_feedback))
                    },
                    onClick = {
                        viewModel.onEvent(
                            SettingsEvent.OnSendFeedbackClick
                        )
                    },

                    leadingContent = {
                        Icon(
                            imageVector = Icons.Rounded.Email,
                            contentDescription = stringResource(R.string.send_feedback)
                        )
                    }
                )

                CustomListItem(
                    indexItem = IndexItem.LAST,
                    headLineContent = {
                        Text(stringResource(R.string.rate_app))
                    },
                    onClick = {
                        viewModel.onEvent(SettingsEvent.OnRateAppClick)
                    },
                    leadingContent = {
                        Icon(
                            painter = painterResource(R.drawable.google_play),
                            contentDescription = stringResource(R.string.rate_app),
                            modifier = Modifier.size(IconButtonDefaults.mediumIconSize)
                        )
                    }
                )
            }

            item {
                SectionHeader(
                    stringResource(R.string.about)
                )
                CustomListItem(
                    indexItem = IndexItem.FIRST,
                    headLineContent = {
                        Text(stringResource(R.string.open_source_libraries))
                    },
                    onClick = {
                        viewModel.onEvent(SettingsEvent.OnAboutLibrariesClick)
                    },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Rounded.WorkspacePremium,
                            contentDescription = stringResource(R.string.open_source_libraries)
                        )
                    }
                )

                CustomListItem(
                    indexItem = IndexItem.MIDDLE,
                    headLineContent = {
                        Text(stringResource(R.string.source_code))
                    },
                    onClick = {
                        viewModel.onEvent(
                            SettingsEvent.OnOpenSourceCodeClick
                        )
                    },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Rounded.Folder,
                            contentDescription = stringResource(R.string.source_code)
                        )
                    }
                )

                CustomListItem(
                    indexItem = IndexItem.MIDDLE,
                    headLineContent = {
                        Text(stringResource(R.string.whats_new))
                    },
                    onClick = {
                        viewModel.onEvent(
                            SettingsEvent.OnToggleChangeLogClick
                        )
                    },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Rounded.Campaign,
                            contentDescription = stringResource(R.string.whats_new)
                        )
                    }
                )

                CustomListItem(
                    indexItem = IndexItem.LAST,
                    headLineContent = {
                        Text(stringResource(R.string.app_version))
                    },
                    trailingContent = {
                        Text(state.appVersion)
                    },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Rounded.Code,
                            contentDescription = stringResource(R.string.app_version)
                        )
                    },
                    modifier = Modifier.padding(bottom = 5.dp)
                )
            }

        }
    }

}
