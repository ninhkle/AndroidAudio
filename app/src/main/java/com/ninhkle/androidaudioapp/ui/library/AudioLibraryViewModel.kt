package com.ninhkle.androidaudioapp.ui.library

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ninhkle.androidaudioapp.common.data.Audio
import com.ninhkle.androidaudioapp.common.data.AudioRepository
import com.ninhkle.androidaudioapp.common.data.AudioRepositoryImpl
import com.ninhkle.androidaudioapp.common.data.LocalAudioDataSource
import kotlinx.coroutines.launch

data class AudioLibraryState(
    val audioList: List<Audio> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class AudioLibraryViewModel(context: Context) : ViewModel() {
    private val repository = AudioRepositoryImpl(
        localAudioDataSource = LocalAudioDataSource(context.contentResolver)
    )
    private val _state = mutableStateOf(AudioLibraryState())
    val state = _state

    init {
        loadAudioFile()
    }

    private fun loadAudioFile() {
        viewModelScope.launch {
            _state.value = AudioLibraryState(isLoading = true, error = null)
            try {
                val audioFiles = repository.getAudioFiles()
                _state.value = _state.value.copy(
                    audioList = audioFiles,
                    isLoading = false,
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = "Failed to load audio files: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    fun onAudioItemClick(audio: Audio) {
        // Handle item click, e.g., navigate to detail screen
        println("Clicked on: ${audio.title} by ${audio.artist}")
    }

    fun onPlayClick(audio: Audio) {
        // Handle play button click, e.g., start playback
        println("Playing: ${audio.title} by ${audio.artist}")
    }

    fun refresh() {
        loadAudioFile()
    }

    companion object {
        fun create(context: Context): AudioLibraryViewModel {
            return AudioLibraryViewModel(context)
        }
    }
}