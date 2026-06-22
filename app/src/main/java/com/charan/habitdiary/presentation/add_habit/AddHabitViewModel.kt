package com.charan.habitdiary.presentation.add_habit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charan.habitdiary.data.repository.DataStoreRepository
import com.charan.habitdiary.data.repository.HabitRepository
import com.charan.habitdiary.notification.NotificationScheduler
import com.charan.habitdiary.presentation.mapper.toHabitEntity
import com.charan.habitdiary.utils.DateUtil.toFormattedString
import com.charan.habitdiary.utils.PermissionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime
import javax.inject.Inject

@HiltViewModel
class AddHabitViewModel @Inject constructor(
    private val habitRepository: HabitRepository,
    private val permissionManager : PermissionManager,
    private val notificationScheduler: NotificationScheduler,
    private val dataStore : DataStoreRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AddHabitState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<AddHabitEffect>()
    val effect = _effect.asSharedFlow()

    init {
        observeTimeChanges()
        observeIs24HourFormat()
    }


    fun onEvent(event: AddHabitEvent) {
        when (event) {
            is AddHabitEvent.OnHabitNameChange -> {
                updateHabitName(event.habitName)

            }

            is AddHabitEvent.OnHabitDescriptionChange -> {
                updateHabitDescription(event.habitDescription)
            }

            is AddHabitEvent.OnHabitTimeChange -> {
                updateHabitTime(event.time)
            }

            is AddHabitEvent.OnHabitReminderTimeChange -> {
                updateHabitReminderTime(event.time)
            }

            is AddHabitEvent.OnHabitFrequencyChange -> {
                toggleHabitFrequency(event.dayOfWeek)
            }

            is AddHabitEvent.OnToggleHabitTimeDialog -> {
                toggleHabitTimeDialog(event.showHabitTimeDialog)
            }

            is AddHabitEvent.OnToggleReminderTimeDialog -> {
                toggleReminderTimeDialog(event.showReminderTimeDialog)
            }

            is AddHabitEvent.OnHabitReminderToggle -> {
                toggleReminder(event.isEnabled)
            }

            AddHabitEvent.OnSaveHabitClick -> {
                saveHabit()
            }

            is AddHabitEvent.InitializeHabit -> {
                initializeHabit(event.habitId)
            }

            is AddHabitEvent.TogglePermissionRationale -> {
                togglePermissionRationale(event.showPermissionRationale)
            }

            AddHabitEvent.OpenPermissionSettings -> {
                permissionManager.openSettingsPermissionScreen()
            }

            AddHabitEvent.OnNavigateBack -> {
                sendEffect(AddHabitEffect.OnNavigateBack())
            }

            AddHabitEvent.OnDeleteHabit -> {
                deleteHabit()
            }

            is AddHabitEvent.OnToggleDeleteDialog -> {
                showDeleteDialog(event.showDeleteDialog)
            }
        }
    }

    private fun showDeleteDialog(show: Boolean) {
        _state.update { it.copy(showDeleteDialog = show) }
    }

    private fun deleteHabit() = viewModelScope.launch {
        habitRepository.deleteHabit(
            _state.value.habitId ?: return@launch
        )
        _state.update {
            it.copy(showDeleteDialog = false)
        }
        sendEffect(AddHabitEffect.OnNavigateBack(true))
    }

    private fun togglePermissionRationale(showRationale: Boolean) {
        _state.update { it.copy(showPermissionRationale = showRationale) }
    }


    private fun updateHabitName(name: String) {
        _state.update { it.copy(habitTitle = name) }
    }

    private fun updateHabitDescription(description: String) {
        _state.update { it.copy(habitDescription = description) }
    }

    private fun updateHabitTime(time : LocalTime) {
        _state.update {
            it.copy(
                showHabitTimeDialog = false,
                habitTime = time,
            )
        }
    }

    private fun updateHabitReminderTime(time : LocalTime) {
        _state.update {
            it.copy(
                showReminderTimeDialog = false,
                habitReminderTime = time,
            )
        }
    }

    private fun initializeHabit(habitId : Long?) = viewModelScope.launch{
        if(habitId!=null){
            val habit = habitRepository.getHabitWithId(habitId)
            val habitTime = habit.habitTime
            val reminderTime = habit.habitReminder
            _state.update {
                it.copy(
                    habitTitle = habit.habitName,
                    habitDescription = habit.habitDescription,
                    habitTime = habitTime,
                    habitFrequency = habit.habitFrequency,
                    habitReminderTime = reminderTime ?: LocalTime(8,0),
                    isReminderEnabled = checkReminderStatus(habit.isReminderEnabled),
                    habitId = habit.id,
                    isEdit = true
                )
            }
        }
    }

    private fun checkReminderStatus(isReminderEnable : Boolean) : Boolean {
        return isReminderEnable && permissionManager.isNotificationPermissionGranted()
    }

    private fun toggleHabitFrequency(frequency: DayOfWeek) {
        val updatedFrequencies = state.value.habitFrequency.toMutableList().apply {
            if (contains(frequency)) remove(frequency) else add(frequency)
        }
        _state.update { it.copy(habitFrequency = updatedFrequencies) }
    }

    private fun toggleHabitTimeDialog(show: Boolean) {
        _state.update { it.copy(showHabitTimeDialog = show) }
    }

    private fun toggleReminderTimeDialog(show: Boolean) {
        _state.update { it.copy(showReminderTimeDialog = show) }
    }

    private fun toggleReminder(enabled: Boolean) {
        if(permissionManager.isNotificationPermissionGranted()) {
            _state.update { it.copy(isReminderEnabled = enabled) }
        } else{
            sendEffect(AddHabitEffect.RequestNotificationPermission)

        }
    }


    private fun saveHabit() = viewModelScope.launch {
        val id = habitRepository.upsetHabit(_state.value.toHabitEntity())
        notificationScheduler.scheduleReminder(
            habitId = id,
            time = _state.value.habitReminderTime ?: LocalTime(8,0),
            isReminderEnabled = _state.value.isReminderEnabled,
            frequency = _state.value.habitFrequency,
        )
        sendEffect(AddHabitEffect.OnNavigateBack())
    }

    private fun observeTimeChanges() = viewModelScope.launch{
        _state
            .map { Triple(it.habitTime, it.habitReminderTime, it.is24HourFormat) }
            .distinctUntilChanged()
            .collectLatest { (habitTime, reminderTime,is24HourFormat) ->
                _state.update {
                    it.copy(
                        formatedHabitTime = habitTime.toFormattedString(is24HourFormat),
                        formatedReminderTime = reminderTime.toFormattedString(is24HourFormat)
                    )

                }
            }

    }

    private fun sendEffect(effect : AddHabitEffect) = viewModelScope.launch {
        _effect.emit(effect)
    }

    private fun observeIs24HourFormat() = viewModelScope.launch {
        dataStore.getIs24HourFormat.collectLatest { is24HourFormat ->
            _state.update {
                it.copy(
                    is24HourFormat = is24HourFormat
                )
            }
        }
    }

}
