package com.example.android_application.data.llm

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import com.example.android_application.util.ModelInitializer
import org.nehuatl.llamacpp.LlamaHelper
import org.nehuatl.llamacpp.LlamaHelper.LLMEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LlmRepositoryImpl @Inject constructor(
    private val contentResolver: ContentResolver,
    private val context: Context
) : LlmRepository {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _llmFlow = MutableSharedFlow<LLMEvent>(
        extraBufferCapacity = 128,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    private val llamaHelper: LlamaHelper by lazy {
        LlamaHelper(contentResolver, scope, _llmFlow)
    }

    private var modelLoaded = false
    private var modelFile: File? = null

    override suspend fun ensureModelLoaded() {
        if (modelLoaded) return

        modelFile = withContext(Dispatchers.IO) {
            ModelInitializer.ensureModel(context)
        }

        val uri = Uri.fromFile(modelFile).toString()

        llamaHelper.load(
            path = uri,
            contextLength = 2048
        ) { }

        _llmFlow.first { event ->
            when (event) {
                is LlamaHelper.LLMEvent.Loaded -> { modelLoaded = true; true }
                is LlamaHelper.LLMEvent.Error -> throw RuntimeException(event.message)
                else -> false
            }
        }
    }

    override suspend fun generateHints(riddle: String): String {
        ensureModelLoaded()

        val prompt = """
            Ты – автор квестов. Придумай 3 подсказки к загадке: «$riddle».
            Каждая следующая подсказка должна быть чуть более явной.
            Ответ: 3 пункта, каждый с новой строки.
        """.trimIndent()

        llamaHelper.predict(prompt)

        val result = StringBuilder()
        _llmFlow.first { event ->
            when (event) {
                is LlamaHelper.LLMEvent.Ongoing -> {
                    result.append(event.word)
                    false
                }
                is LlamaHelper.LLMEvent.Done -> true
                is LLMEvent.Error -> throw RuntimeException(event.message)
                else -> false
            }
        }
        return result.toString()
    }
}