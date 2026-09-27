package com.charan.habitdiary.data.repository

import com.charan.habitdiary.data.model.AiResponse
import com.charan.habitdiary.data.model.AiResponseEvent
import com.charan.habitdiary.data.model.ProcessState
import kotlinx.coroutines.flow.Flow

interface LocalLlmRepository {
    fun downloadModel() : Flow<ProcessState<Boolean>>

    fun generateResponse(prompt : String) : Flow<AiResponseEvent<AiResponse>>
}