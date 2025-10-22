package com.ninhkle.androidaudioapp.ui.player

import android.app.Application
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import androidx.annotation.OptIn
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors
import com.ninhkle.androidaudioapp.common.data.Audio
import com.ninhkle.androidaudioapp.common.service.AudioPlaybackService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PlayerState(
    val currentAudio: Audio? = null,
    val isPlaying: Boolean = false,
    val currentPosition: Long = 0,
    val totalDuration : Long = 0,
    val isLoading : Boolean = false,
)

class PlayerViewModel(application: Application) : AndroidViewModel(application) {
    private val _state = mutableStateOf(PlayerState())
    val state : State<PlayerState> = _state

    private var currentPlaylist: List<Audio> = emptyList()
    private var currentIndex: Int = 0

    private val _mediaControllerFLow = MutableStateFlow<MediaController?>(null)
    val mediaControllerFlow = _mediaControllerFLow.asStateFlow()


    init {
        connectToService()
    }

    @OptIn(UnstableApi::class)
    private fun connectToService() {
        val context = getApplication<Application>().applicationContext
        val sessionToken = SessionToken(context, ComponentName(context, AudioPlaybackService::class.java))
        val controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()

        controllerFuture.addListener(
            {
                val mediaController = controllerFuture.get()
                setupControllerListeners(mediaController)

                _mediaControllerFLow.value = mediaController
            },
            MoreExecutors.directExecutor()
        )
    }

    private fun setupControllerListeners(mediaController: MediaController) {
        mediaController?.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                _state.value = _state.value.copy(
                    isLoading = playbackState == Player.STATE_BUFFERING,
                    totalDuration = mediaController?.duration ?: 0L
                )
                if (playbackState == Player.STATE_ENDED) {
                    // Auto play next audio
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _state.value = _state.value.copy(isPlaying = isPlaying)
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                super.onMediaItemTransition(mediaItem, reason)
                if (mediaItem == null) {
                    _state.value = _state.value.copy(currentAudio = null)
                    return
                }
                val newCurrentAudio = Audio(
                    id = mediaItem.mediaId.toLongOrNull() ?: -1L,
                    uri = (mediaItem.requestMetadata.mediaUri ?: Uri.EMPTY).toString(),
                    title = mediaItem.mediaMetadata.title?.toString() ?: "Unknown Title",
                    artist = mediaItem.mediaMetadata.artist?.toString() ?: "Unknown Artist",
                    album = mediaItem.mediaMetadata.albumTitle?.toString() ?: "Unknown Album",
                    duration = mediaController.duration, // Get duration from the controller
                    albumId = 0L
                )
                _state.value = _state.value.copy(currentAudio = newCurrentAudio)
            }
        })
        viewModelScope.launch {
            while (true) {
                if (mediaController?.isPlaying == true) {
                    _state.value = _state.value.copy(
                        currentPosition = mediaController?.currentPosition ?: 0L
                    )
                }
                delay(1000)
            }
        }
    }

    fun setAudio(audio: Audio?, playlist: List<Audio> = emptyList()) {
        val mediaController = _mediaControllerFLow.value
        audio?.let { newAudio ->
            val isSameAudio = mediaController?.currentMediaItem?.mediaId == newAudio.id.toString()
            if (isSameAudio) return

            this.currentPlaylist = playlist.ifEmpty { listOf(newAudio) }

            val mediaItems = this.currentPlaylist.map {
                MediaItem.Builder()
                    .setUri(it.uri)
                    .setMediaId(it.id.toString())
                    .setMediaMetadata(
                        MediaMetadata.Builder()
                            .setTitle(it.title)
                            .setArtist(it.artist)
                            .build()
                    )
                    .build()
            }

            val startIndex = this.currentPlaylist.indexOf(newAudio)

            mediaController?.setMediaItems(mediaItems, startIndex, 0L)
            mediaController?.prepare()
            mediaController?.play()
        }
    }

    fun playPause() {
        val mediaController = _mediaControllerFLow.value
        if (mediaController?.isPlaying == true) {
            mediaController?.pause()
        } else {
            mediaController?.play()
        }
    }

    fun playNext() {
        val mediaController = _mediaControllerFLow.value
        mediaController?.seekToNextMediaItem()
    }

    fun playPrevious() {
        val mediaController = _mediaControllerFLow.value
        mediaController?.seekToPreviousMediaItem()
    }

    fun seekTo(position: Long) {
        val mediaController = _mediaControllerFLow.value
        mediaController?.seekTo(position)
    }

//    fun synchronizeState() {
//        val mediaController = _mediaControllerFLow.value
//        if (mediaController == null) {
//            isSyncRequested = true
//            return
//        }
//        mediaController?.let { controller ->
//            if (controller.mediaItemCount > 0) {
//                val currentMediaItem = controller.currentMediaItem ?: return
//                val currentAudioFromService = Audio(
//                    id = currentMediaItem.mediaId.toLongOrNull() ?: -1L,
//                    uri = (currentMediaItem.requestMetadata.mediaUri ?: Uri.EMPTY).toString(),
//                    title = currentMediaItem.mediaMetadata.title?.toString() ?: "Unknown Title",
//                    artist = currentMediaItem.mediaMetadata.artist?.toString() ?: "Unknown Artist",
//                    album = currentMediaItem.mediaMetadata.albumTitle?.toString() ?: "Unknown Album",
//                    duration = controller.duration,
//                    albumId = 0L // Placeholder, as albumId is not available from MediaItem
//                )
//                _state.value = _state.value.copy(
//                    currentAudio = currentAudioFromService,
//                    isPlaying = controller.isPlaying,
//                    currentPosition = controller.currentPosition,
//                    totalDuration = controller.duration,
//                    isLoading = controller.playbackState == Player.STATE_BUFFERING
//                )
//            }
//        }
//    }

    override fun onCleared() {
        super.onCleared()
        val mediaController = _mediaControllerFLow.value
        mediaController?.release()
    }

    fun updateStateFromController(audio: Audio, controller: Player) {
        _state.value = _state.value.copy(
            currentAudio = audio,
            isPlaying = controller.isPlaying,
            currentPosition = controller.currentPosition,
            totalDuration = controller.duration,
            isLoading = controller.playbackState == Player.STATE_BUFFERING
        )
    }


}