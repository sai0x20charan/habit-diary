package com.charan.habitdiary.appfunctions

import android.util.Log
import androidx.appfunctions.AppFunctionContext
import androidx.appfunctions.AppFunctionSerializable
import androidx.appfunctions.service.AppFunction
import com.charan.habitdiary.data.local.entity.HabitEntity
import com.charan.habitdiary.data.repository.HabitLocalRepository
import com.charan.habitdiary.utils.DateUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime

class AppFunctions(
    private val habitLocalRepository: HabitLocalRepository
) {

    /**
     * This data class represents the details of the habits
     */
    @AppFunctionSerializable(isDescribedByKDoc = true)
    data class HabitDetails(
        /** id for the habit **/
        val habitId : Long,

        /** name of the habit **/
        val habitName: String,

        /** description of the habit **/
        val habitDescription: String,

        /** time at which the habit has to be done **/
        val habitTime: String
    )

    /**
     * Retrieves the list of habits scheduled for today that are not yet completed.
     *
     * @param appFunctionContext The context of this app function call.
     *
     * @return A list of HabitDetails representing all pending habits for the current day.
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun checkForCurrentPendingHabits(
        appFunctionContext: AppFunctionContext
    ): List<HabitDetails> {
        return withContext(Dispatchers.IO) {
            val pendingHabits =
                habitLocalRepository.getTodayHabits().filter { !it.isDone }

            pendingHabits.map { it.habitEntity.toHabitDetails() }
        }
    }

    /**
     * This function is used to create a habit or update a habit.
     *
     * If the user wants to update or edit a habit, first find the exact habit using [getAllHabits]
     * and then pass the habitId to this function along with the updated details.
     *
     * If the user wants to create a new habit then pass null as habitId.
     *
     * @param habitId The unique identifier of the habit to be updated. If null, a new habit will be created.
     *
     * @param appFunctionContext The context of this app function call.
     *
     * @param habitName The name of the habit.
     *
     * @param habitDescription A detailed description of the habit.
     *
     * @param habitTime The time at which the habit should be performed, in 24-hour format (HH:mm). For example, "07:30" represents 7:30 AM.
     *
     * @param habitFrequency A list of days on which the habit should repeat. Each value must be a valid day of the week in uppercase, such as "MONDAY", "TUESDAY", etc.
     *
     * @param habitReminderTime The time at which a reminder should be triggered before or at the habit time, in 24-hour format (HH:mm). For example, "07:00".
     *
     * @param isReminderEnabled A boolean indicating whether the reminder for this habit is enabled or not.
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun upsertHabit(
        appFunctionContext: AppFunctionContext,
        habitId: Long? = null,
        habitName: String,
        habitDescription: String,
        habitTime: String,
        habitFrequency: List<String>,
        habitReminderTime: String,
        isReminderEnabled : Boolean
    ) {
        withContext(Dispatchers.IO) {

            val parsedHabitTime = LocalTime.parse(habitTime)
            val parsedReminderTime = LocalTime.parse(habitReminderTime)

            val parsedFrequency = habitFrequency.map {
                DayOfWeek.valueOf(it.uppercase())
            }

            val habitEntity = HabitEntity(
                id = habitId ?: 0 ,
                habitName = habitName,
                habitDescription = habitDescription,
                habitTime = parsedHabitTime,
                habitFrequency = parsedFrequency,
                habitReminder = parsedReminderTime,
                isDeleted = false,
                isReminderEnabled = isReminderEnabled,
                createdAt = DateUtil.getCurrentDateTime(),
            )

            habitLocalRepository.upsetHabit(habitEntity)
        }
    }

    /**
     * Retrieves the list of habits scheduled for today that are not yet completed.
     *
     * @param appFunctionContext The context of this app function call.
     *
     * @return A list of HabitDetails representing all habits.
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun getAllHabits(
        appFunctionContext: AppFunctionContext
    ): List<HabitDetails> {
        return withContext(Dispatchers.IO) {
            val pendingHabits =
                habitLocalRepository.getAllHabits().filter { !it.isDeleted }

           pendingHabits.map { it.toHabitDetails() }
        }
    }


    private fun HabitEntity.toHabitDetails(): HabitDetails {
        return HabitDetails(
            habitId = this.id,
            habitName = this.habitName,
            habitDescription = this.habitDescription,
            habitTime = this.habitTime.toString()
        )
    }
}