package com.ninhkle.androidaudioapp.ui.player

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.annotation.OptIn
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.ninhkle.androidaudioapp.common.data.Audio
import com.ninhkle.androidaudioapp.common.service.AudioPlaybackService
import kotlinx.coroutines.launch

data class PlayerState(
    val currentAudio: Audio? = null,
    val isPlaying: Boolean = false,
    val currentPosition: Long = 0,
    val totalDuration : Long = 0,
    val isLoading : Boolean = false,
)

class PlayerViewModel(application: Application) : AndroidViewModel(application) {
    private val exoPlayer : ExoPlayer = ExoPlayer.Builder(getApplication()).build()

    private val _state = mutableStateOf(PlayerState())
    val state : State<PlayerState> = _state

    private var currentPlaylist: List<Audio> = emptyList()
    private var currentIndex: Int = 0

    init {
        setupPlayerListeners()
    }

    private fun setupPlayerListeners() {
        exoPlayer.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_BUFFERING -> {
                        _state.value = _state.value.copy(isLoading = true)
                    }
                    Player.STATE_READY -> {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            totalDuration = exoPlayer.duration
                        )
                    }
                    Player.STATE_ENDED -> {
                        playNext()
                    }
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _state.value = _state.value.copy(isPlaying = isPlaying)
            }

            override fun onPositionDiscontinuity(
                oldPosition: Player.PositionInfo,
                newPosition: Player.PositionInfo,
                reason: Int
            ) {
                updateProgress()
            }
        })

        viewModelScope.launch {
            while (true) {
                updateProgress()
                kotlinx.coroutines.delay(1000)
            }
        }
    }

    fun playAudio(audio: Audio, playlist: List<Audio> = emptyList()) {
        startPlaybackService()

        currentPlaylist = if (playlist.isEmpty()) listOf(audio) else playlist
        currentIndex = currentPlaylist.indexOf(audio)

        val mediaItem = MediaItem.fromUri(audio.uri)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.play()

        _state.value = _state.value.copy(
            currentAudio = audio,
            isPlaying = true
        )
    }

    fun playPause() {
        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
        } else {
            exoPlayer.play()
        }
    }

    fun playNext() {
        if (currentPlaylist.isNotEmpty()) {
            val nextIndex = (currentIndex + 1) % currentPlaylist.size
            playAudio(currentPlaylist[nextIndex], currentPlaylist)
        }
    }

    fun playPrevious() {
        if (currentPlaylist.isNotEmpty()) {
            val prevIndex = if (currentIndex - 1 < 0) {
                currentPlaylist.size - 1
            } else {
                currentIndex - 1
            }
            playAudio(currentPlaylist[prevIndex], currentPlaylist)
        }
    }

    fun seekTo(position: Long) {
        exoPlayer.seekTo(position)
    }

    fun setAudio(audio: Audio?, playlist: List<Audio> = emptyList()) {
        audio?.let {
            playAudio(it, playlist)
        }
    }

    private fun updateProgress() {
        _state.value = _state.value.copy(
            currentPosition = exoPlayer.currentPosition,
            totalDuration = exoPlayer.duration
        )
    }

    override fun onCleared() {
        super.onCleared()
        stopPlaybackService()
        exoPlayer.release()
    }

    @OptIn(UnstableApi::class)
    private fun startPlaybackService() {
        val context = getApplication<Application>().applicationContext
        val intent = Intent(context, AudioPlaybackService::class.java)
        context.startService(intent)
    }

    @OptIn(UnstableApi::class)
    private fun stopPlaybackService() {
        val context = getApplication<Application>().applicationContext
        val intent = Intent(context, AudioPlaybackService::class.java)
        context.stopService(intent)
    }

}