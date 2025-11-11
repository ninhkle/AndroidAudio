package com.ninhkle.androidaudioapp.ui.player

import android.app.Application
import android.content.ComponentName
import android.net.Uri
import androidx.annotation.OptIn
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
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

class PlayerViewModel() : ViewModel() {
    private val _state = MutableStateFlow(PlayerState())
    val state  = _state.asStateFlow()

    private var currentPlaylist: List<Audio> = emptyList()
    private var currentIndex: Int = 0

    private var mediaController: MediaController? = null

    fun setMediaController(controller: MediaController) {
        if (this.mediaController != null) return

        this.mediaController = controller
        synchronizeStateWithController(controller)
        setupControllerListeners(controller)
    }

//    @OptIn(UnstableApi::class)
//    private fun connectToService() {
//        val context = getApplication<Application>().applicationContext
//        val sessionToken = SessionToken(context, ComponentName(context, AudioPlaybackService::class.java))
//        val controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
//
//        controllerFuture.addListener(
//            {
//                val mediaController = controllerFuture.get()
//                synchronizeStateWithController(mediaController)
//
//                setupControllerListeners(mediaController)
//
//                _mediaControllerFLow.value = mediaController
//            },
//            MoreExecutors.directExecutor()
//        )
//    }

    private fun setupControllerListeners(
        mediaController: MediaController,
        enablePositionUpdates: Boolean = true
    ) {
        mediaController.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                _state.value = _state.value.copy(
                    isLoading = playbackState == Player.STATE_BUFFERING,
                    totalDuration = mediaController.duration
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
        if (enablePositionUpdates) {
            viewModelScope.launch {
                while (true) {
                    if (mediaController.isPlaying) {
                        _state.value = _state.value.copy(
                            currentPosition = mediaController.currentPosition
                        )
                    }
                    delay(1000)
                }
            }
        }
    }

    fun setMediaController(
        controller: MediaController,
        enablePositionUpdatesInTest: Boolean = true
    ) {
        if (this.mediaController != null) return

        this.mediaController = controller
        synchronizeStateWithController(controller)
        setupControllerListeners(controller, enablePositionUpdatesInTest) // <-- PASS IT HERE
    }

    fun setAudio(audio: Audio?, playlist: List<Audio> = emptyList()) {
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
        if (mediaController?.isPlaying == true) {
            mediaController?.pause()
        } else {
            mediaController?.play()
        }
    }

    fun playNext() {
        mediaController?.seekToNextMediaItem()
    }

    fun playPrevious() {
        mediaController?.seekToPreviousMediaItem()
    }

    fun seekTo(position: Long) {
        mediaController?.seekTo(position)
    }

    private fun synchronizeStateWithController(controller: MediaController) {
        // If there's no media item, there's nothing to sync.
        val currentMediaItem = controller.currentMediaItem ?: return

        // Reconstruct the Audio object from the MediaItem
        val currentAudio = Audio(
            id = currentMediaItem.mediaId.toLongOrNull() ?: -1L,
            uri = (currentMediaItem.requestMetadata.mediaUri ?: Uri.EMPTY).toString(),
            title = currentMediaItem.mediaMetadata.title?.toString() ?: "Unknown Title",
            artist = currentMediaItem.mediaMetadata.artist?.toString() ?: "Unknown Artist",
            album = currentMediaItem.mediaMetadata.albumTitle?.toString() ?: "Unknown Album",
            duration = controller.duration.coerceAtLeast(0), // Ensure duration isn't negative
            albumId = 0L // This is a placeholder, as expected
        )

        // Update the state with all the current details from the controller
        _state.value = _state.value.copy(
            currentAudio = currentAudio,
            isPlaying = controller.isPlaying,
            currentPosition = controller.currentPosition,
            totalDuration = controller.duration.coerceAtLeast(0),
            isLoading = controller.playbackState == Player.STATE_BUFFERING
        )
    }

    override fun onCleared() {
        super.onCleared()
//        val mediaController = _mediaControllerFLow.value
//        mediaController?.release()
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