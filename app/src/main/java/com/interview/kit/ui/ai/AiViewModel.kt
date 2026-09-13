package com.interview.kit.ui.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.interview.kit.domain.model.Post
import com.interview.kit.domain.repository.AiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AiViewModel @Inject constructor(
    private val aiRepository: AiRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AiUiState>(AiUiState.Idle)
    val uiState: StateFlow<AiUiState> = _uiState.asStateFlow()

    private var streamJob: Job? = null

    val isLiveConfigured: Boolean
        get() = aiRepository.isLiveConfigured()

    fun sendPrompt(prompt: String) {
        if (prompt.isBlank()) return
        startStreaming { aiRepository.generateStream(prompt) }
    }

    fun summarizePost(post: Post) {
        startStreaming { aiRepository.summarizePost(post) }
    }

    private fun startStreaming(flowProvider: () -> kotlinx.coroutines.flow.Flow<String>) {
        streamJob?.cancel()
        streamJob = viewModelScope.launch {
            val accumulatedText = StringBuilder()
            flowProvider()
                .onStart {
                    _uiState.value = AiUiState.Streaming("")
                }
                .catch { error ->
                    _uiState.value = AiUiState.Error(error.message ?: "Failed to generate AI response")
                }
                .onCompletion { error ->
                    if (error == null && _uiState.value is AiUiState.Streaming) {
                        _uiState.value = AiUiState.Done(accumulatedText.toString())
                    }
                }
                .collect { chunk ->
                    accumulatedText.append(chunk)
                    _uiState.value = AiUiState.Streaming(accumulatedText.toString())
                }
        }
    }

    fun reset() {
        streamJob?.cancel()
        _uiState.value = AiUiState.Idle
    }
}

sealed interface AiUiState {
    data object Idle : AiUiState
    data class Streaming(val text: String) : AiUiState
    data class Done(val text: String) : AiUiState
    data class Error(val message: String) : AiUiState
}
