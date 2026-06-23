package com.charan.habitdiary.presentation.addhabit

import android.Manifest
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.charan.habitdiary.R
import com.charan.habitdiary.presentation.addhabit.components.FormTextComponent
import com.charan.habitdiary.presentation.addhabit.components.HabitReminderComponent
import com.charan.habitdiary.presentation.addhabit.components.ScheduleHabitComponent
import com.charan.habitdiary.presentation.common.components.ActionButtonRow
import com.charan.habitdiary.presentation.common.components.CustomMediumTopBar
import com.charan.habitdiary.presentation.common.components.DeleteWarningDialog
import com.charan.habitdiary.presentation.common.components.RationaleDialog
import com.charan.habitdiary.presentation.common.components.SelectTimeDialog
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.flow.collectLatest
import com.charan.habitdiary.core.utils.showToast
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalPermissionsApi::class
)
@Composable
fun AddHabitScreen(
    onNavigateBack : (isHabitDeleted : Boolean) -> Unit ,
    habitId : Long? = null
) {
    val viewModel = hiltViewModel<AddHabitViewModel>()
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val notificationPermissionState = rememberPermissionState(
        permission = Manifest.permission.POST_NOTIFICATIONS
    ) {
        if(it) {
            viewModel.onEvent(AddHabitEvent.OnHabitReminderToggle(true))
        }
    }
    LaunchedEffect(Unit) {
        viewModel.onEvent(AddHabitEvent.InitializeHabit(habitId))
        viewModel.effect.collectLatest { effect ->
            when(effect){
                is AddHabitEffect.OnNavigateBack -> {
                    onNavigateBack(effect.isHabitDeleted)

                }
                is AddHabitEffect.ShowToast -> {
                    context.showToast(effect.message)
                }
                AddHabitEffect.RequestNotificationPermission -> {
                    if(notificationPermissionState.status.shouldShowRationale){
                        viewModel.onEvent(AddHabitEvent.TogglePermissionRationale(true))
                    } else {
                        notificationPermissionState.launchPermissionRequest()
                    }

                }
            }
        }
    }

    if(state.showPermissionRationale){
        RationaleDialog(
            title = stringResource(R.string.notification_permission_required),
            message = stringResource(R.string.notification_permission_description),
            onDismissRequest = {
                viewModel.onEvent(AddHabitEvent.TogglePermissionRationale(false))
            },
            onConfirmRequest = {
                viewModel.onEvent(AddHabitEvent.OpenPermissionSettings)
                viewModel.onEvent(AddHabitEvent.TogglePermissionRationale(false))

            }
        )
    }

    if (state.showHabitTimeDialog) {
        SelectTimeDialog(
            onDismiss = {
                viewModel.onEvent(AddHabitEvent.OnToggleHabitTimeDialog(false))
            },
            onTimeSelected = {
                viewModel.onEvent(
                    AddHabitEvent.OnHabitTimeChange(
                        it
                    )
                )
            },
            selectedTime = state.habitTime,
            is24HourFormat = state.is24HourFormat
        )
    }

    if(state.showReminderTimeDialog){
        SelectTimeDialog(
            onDismiss = {
                viewModel.onEvent(AddHabitEvent.OnToggleReminderTimeDialog(false))
            },
            onTimeSelected = {
                viewModel.onEvent(
                    AddHabitEvent.OnHabitReminderTimeChange(
                        it
                    )
                )
            },
            selectedTime = state.habitReminderTime,
            is24HourFormat = state.is24HourFormat
        )
    }

    if(state.showDeleteDialog){
        DeleteWarningDialog(
            onDismiss = {
                viewModel.onEvent(AddHabitEvent.OnToggleDeleteDialog(false))
            },
            onConfirm = {
                viewModel.onEvent(AddHabitEvent.OnDeleteHabit)
            },
        )
    }

    Scaffold(
        topBar = {
            CustomMediumTopBar(
                title = if(state.isEdit) stringResource(R.string.edit_habit)
                else stringResource(R.string.add_habit),
                showBackButton = true,
                onBackClick = {
                    viewModel.onEvent(AddHabitEvent.OnNavigateBack)
                },
                scrollBehavior = scrollBehavior

            )
        },
        bottomBar = {
            ActionButtonRow(
                saveButtonText = stringResource(R.string.save_habit),
                onSave = {
                    viewModel.onEvent(AddHabitEvent.OnSaveHabitClick)
                },
                showDeleteButton = state.isEdit,
                onDelete = {
                    viewModel.onEvent(AddHabitEvent.OnToggleDeleteDialog(true))
                },
                isSaveEnabled = state.habitTitle.isNotEmpty()

            )
        }
    ) { inner ->
        LazyColumn(
            modifier = Modifier
                .padding(inner)
                .padding(16.dp)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        ) {
            item {
                FormTextComponent(
                    title = stringResource(R.string.habit_name),
                    textFieldLabel = stringResource(R.string.enter_habit_name),
                    textValue = state.habitTitle,
                    onTextChange = { viewModel.onEvent(AddHabitEvent.OnHabitNameChange(it)) }
                )

                Spacer(Modifier.height(20.dp))

                FormTextComponent(
                    title = stringResource(R.string.habit_description),
                    textFieldLabel = stringResource(R.string.enter_habit_description),
                    textValue = state.habitDescription,
                    onTextChange = { viewModel.onEvent(AddHabitEvent.OnHabitDescriptionChange(it)) }
                )

                HorizontalDivider(Modifier.padding(vertical = 20.dp))

                ScheduleHabitComponent(
                    selectedTime = state.formatedHabitTime,
                    onTimeClick = {
                        viewModel.onEvent(AddHabitEvent.OnToggleHabitTimeDialog(true))
                    },
                    selectedDays = state.habitFrequency,
                    onDayToggle = { index ->
                        viewModel.onEvent(AddHabitEvent.OnHabitFrequencyChange(index))
                    }
                )

                HorizontalDivider(Modifier.padding(vertical = 20.dp))

                HabitReminderComponent(
                    isReminderEnabled = state.isReminderEnabled,
                    reminderTime = state.formatedReminderTime,
                    onReminderToggle = { enabled ->
                        viewModel.onEvent(AddHabitEvent.OnHabitReminderToggle(enabled))
                    },
                    onSelectReminderTime = {
                        viewModel.onEvent(AddHabitEvent.OnToggleReminderTimeDialog(true))
                    }
                )
            }
        }
    }
}