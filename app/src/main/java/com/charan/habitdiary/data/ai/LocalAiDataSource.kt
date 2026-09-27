package com.charan.habitdiary.data.ai

import ai.runanywhere.proto.v1.InferenceFramework
import ai.runanywhere.proto.v1.ModelCategory
import ai.runanywhere.proto.v1.ModelFileDescriptor
import android.content.Context
import android.util.Log
import com.runanywhere.sdk.llm.llamacpp.LlamaCPP
import com.runanywhere.sdk.public.RunAnywhere
import com.runanywhere.sdk.public.api.DownloadEvent
import com.runanywhere.sdk.public.api.GenerationEvent
import com.runanywhere.sdk.public.api.ImageInput
import com.runanywhere.sdk.public.api.LlmOptions
import com.runanywhere.sdk.public.api.LoadedModel
import com.runanywhere.sdk.public.api.ModelRegistration
import com.runanywhere.sdk.public.api.llm
import com.runanywhere.sdk.public.api.models
import com.runanywhere.sdk.public.api.vlm
import com.runanywhere.sdk.public.extensions.Models.isDownloadedOnDisk
import com.runanywhere.sdk.public.types.RAModelInfo
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalAiDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    companion object {
        private const val TAG = "LocalAiDataSource"
        const val MODEL_ID = "smolvlm2-500m-video-instruct-q8_0"
        const val MODEL_NAME = "SmolVLM2 500M Video Instruct Q8_0"

        private const val WEIGHTS_URL =
            "https://huggingface.co/ggml-org/SmolVLM2-500M-Video-Instruct-GGUF/resolve/main/SmolVLM2-500M-Video-Instruct-Q8_0.gguf"
        private const val WEIGHTS_FILENAME = "SmolVLM2-500M-Video-Instruct-Q8_0.gguf"
        private const val WEIGHTS_SIZE_BYTES = 436_808_704L

        private const val MMPROJ_URL =
            "https://huggingface.co/ggml-org/SmolVLM2-500M-Video-Instruct-GGUF/resolve/main/mmproj-SmolVLM2-500M-Video-Instruct-Q8_0.gguf"
        private const val MMPROJ_FILENAME = "mmproj-SmolVLM2-500M-Video-Instruct-Q8_0.gguf"
        private const val MMPROJ_SIZE_BYTES = 108_785_184L

        private const val DOWNLOAD_BYTES = 545_593_888L // WEIGHTS_SIZE_BYTES + MMPROJ_SIZE_BYTES
        private const val MEMORY_BYTES = 800_000_000L
    }

    suspend fun initialize() {
        LlamaCPP.register()
        if (!RunAnywhere.isInitialized) {
            RunAnywhere.initialize(context = context)
        }
        registerModel()
    }

    suspend fun registerModel(): RAModelInfo {
        val existing = RunAnywhere.models.get(MODEL_ID)
        if (existing != null) {
            Log.d(TAG, "Model $MODEL_ID already registered")
            return existing
        }

        Log.d(TAG, "Registering model $MODEL_ID")
        return RunAnywhere.models.register(
            ModelRegistration.multiFile(
                id = MODEL_ID,
                name = MODEL_NAME,
                framework = InferenceFramework.INFERENCE_FRAMEWORK_LLAMA_CPP,
                category = ModelCategory.MODEL_CATEGORY_MULTIMODAL,
                memoryBytes = MEMORY_BYTES,
                downloadBytes = DOWNLOAD_BYTES,
                files = listOf(
                    ModelFileDescriptor(
                        url = WEIGHTS_URL,
                        filename = WEIGHTS_FILENAME,
                        size_bytes = WEIGHTS_SIZE_BYTES,
                    ),
                    ModelFileDescriptor(
                        url = MMPROJ_URL,
                        filename = MMPROJ_FILENAME,
                        size_bytes = MMPROJ_SIZE_BYTES,
                    ),
                ),
            ),
        )
    }

    suspend fun isModelDownloaded(): Boolean {
        val model = RunAnywhere.models.get(MODEL_ID) ?: return false
        return model.isDownloadedOnDisk
    }

    fun downloadModel(): Flow<DownloadEvent> {
        return RunAnywhere.models.download(MODEL_ID)
    }

    suspend fun installModel(): Boolean {
        initialize()
        if (isModelDownloaded()) {
            Log.d(TAG, "Model $MODEL_ID is already installed on disk")
            return true
        }

        Log.d(TAG, "Starting download and installation of $MODEL_ID")
        var isSuccess = false
        downloadModel().collect { event ->
            when (event) {
                is DownloadEvent.Started -> {
                    Log.d(TAG, "Download started for $MODEL_ID")
                }
                is DownloadEvent.Progress -> {
                    val progressPercent = if (event.bytesTotal > 0) {
                        (event.bytesDone * 100 / event.bytesTotal)
                    } else null
                    Log.d(TAG, "Download progress: ${event.file ?: ""} $progressPercent% (${event.bytesDone}/${event.bytesTotal})")
                }
                is DownloadEvent.Verifying -> {
                    Log.d(TAG, "Verifying $MODEL_ID files...")
                }
                is DownloadEvent.Extracting -> {
                    Log.d(TAG, "Extracting $MODEL_ID: ${event.percent}%")
                }
                is DownloadEvent.Completed -> {
                    Log.d(TAG, "Successfully installed $MODEL_ID")
                    isSuccess = true
                }
                is DownloadEvent.Failed -> {
                    Log.e(TAG, "Failed to install $MODEL_ID: ${event.error.message}", event.error)
                    isSuccess = false
                }
                is DownloadEvent.Cancelled -> {
                    Log.w(TAG, "Download cancelled for $MODEL_ID")
                    isSuccess = false
                }
            }
        }
        return isSuccess
    }

    suspend fun loadModel(): LoadedModel {
        initialize()
        return RunAnywhere.models.load(MODEL_ID)
    }

    fun generateResponse(prompt: String): Flow<GenerationEvent> {
        return RunAnywhere.llm.generateStream(prompt, LlmOptions(model = MODEL_ID))
    }

    fun generateVisionResponse(image: ImageInput, prompt: String): Flow<GenerationEvent> {
        return RunAnywhere.vlm.generateStream(image, prompt, LlmOptions(model = MODEL_ID))
    }
}
