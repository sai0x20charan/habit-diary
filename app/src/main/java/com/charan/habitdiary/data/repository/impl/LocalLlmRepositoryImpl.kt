package com.charan.habitdiary.data.repository.impl

import android.util.Log
import com.charan.habitdiary.data.ai.LocalAiDataSource
import com.charan.habitdiary.data.model.AiResponse
import com.charan.habitdiary.data.model.AiResponseEvent
import com.charan.habitdiary.data.model.ProcessState
import com.charan.habitdiary.data.repository.LocalLlmRepository
import com.runanywhere.sdk.public.api.DownloadEvent
import com.runanywhere.sdk.public.api.GenerationEvent
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

class LocalLlmRepositoryImpl @Inject constructor(
    private val localAiDataSource: LocalAiDataSource
) : LocalLlmRepository {
    override fun downloadModel(): Flow<ProcessState<Boolean>> = callbackFlow {
        localAiDataSource.downloadModel().collectLatest { state ->
            Log.d("TAG", "downloadModel: $state")
            when (state) {
                is DownloadEvent.Cancelled -> {
                    trySend(ProcessState.Error("Download Cancelled"))
                }
                is DownloadEvent.Completed -> {
                    trySend(ProcessState.Success(true))
                }
                is DownloadEvent.Extracting -> {
                    trySend(ProcessState.Loading())
                }
                is DownloadEvent.Failed -> {
                    trySend(ProcessState.Error(state.error.message ?: "Download Failed"))
                }
                is DownloadEvent.Progress -> {
                    trySend(ProcessState.Loading(
                        progress = state.overallProgress ?: 0.0f
                    ))
                }
                is DownloadEvent.Started -> {
                    trySend(ProcessState.Loading())
                }
                is DownloadEvent.Verifying -> {
                    trySend(ProcessState.Loading())
                }
            }
        }
        awaitClose { this.close() }
    }

    override fun generateResponse(prompt: String): Flow<AiResponseEvent<AiResponse>> = callbackFlow {
        localAiDataSource.loadModel()

        var thinking = ""
        localAiDataSource.generateResponse(prompt).collectLatest { state ->
            Log.d("TAG", "generateResponse: $state")
            when (state) {
                is GenerationEvent.Started -> {
                    trySend(AiResponseEvent.Loading())
                }
                is GenerationEvent.TextDelta -> {
                    trySend(AiResponseEvent.TextGenerated(state.text))
                }
                is GenerationEvent.ReasoningDelta -> {

                }
                is GenerationEvent.Completed -> {
                    trySend(AiResponseEvent.Success(state.result.text))
                }
                is GenerationEvent.Failed -> {
                    trySend(AiResponseEvent.Error(state.error.message ?: "Generation Failed"))
                }
                is GenerationEvent.Cancelled -> {
                    trySend(AiResponseEvent.Error("Generation Cancelled"))
                }
                else -> Unit
            }
        }
        awaitClose { this.close() }
    }
}
