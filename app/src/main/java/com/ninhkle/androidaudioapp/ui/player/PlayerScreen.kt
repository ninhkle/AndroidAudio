package com.ninhkle.androidaudioapp.ui.player

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ninhkle.androidaudioapp.common.data.Audio
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first

@Composable
fun PlayerScreen(
    playerViewModel: PlayerViewModel,
) {
    LaunchedEffect(playerViewModel) {
        val mediaController = playerViewModel.mediaControllerFlow.filterNotNull().first()
        if (mediaController.mediaItemCount > 0) {
            val currentMediaItem = mediaController.currentMediaItem ?: return@LaunchedEffect
            val currentAudioFromService = Audio(
                id = currentMediaItem.mediaId.toLong(),
                title = currentMediaItem.mediaMetadata.title?.toString() ?: "Unknown Title",
                artist = currentMediaItem.mediaMetadata.artist?.toString() ?: "Unknown Artist",
                album = currentMediaItem.mediaMetadata.albumTitle?.toString() ?: "Unknown Album",
                duration = mediaController.duration,
                uri = currentMediaItem.localConfiguration?.uri.toString(),
                albumId = 0L // Placeholder, as albumId is not available from MediaItem
            )
            playerViewModel.updateStateFromController(currentAudioFromService, mediaController)
        }
    }

    val state = playerViewModel.state.value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Album art placeholder
        Box(
            modifier = Modifier.size(300.dp).padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            // Add actual album art later
            Text(text = "Album Art", style = MaterialTheme.typography.bodyLarge)
        }

        Spacer(modifier = Modifier.height(32.dp))

        PlayerControls(
            state = state,
            onPlayPause = playerViewModel::playPause,
            onNext = playerViewModel::playNext,
            onPrevious = playerViewModel::playPrevious,
            onSeek = playerViewModel::seekTo,
            modifier = Modifier.fillMaxSize()
        )
    }
}

//@Preview
//@Composable
//fun PreviewPlayerScreen() {
//    val sampleAudio = Audio(
//        id = 1L,
//        title = "Sample Song",
//        artist = "Sample Artist",
//        album = "Sample Album",
//        duration = 240000L,
//        uri = "",
//        albumId = 0L
//    )
//    val samplePlaylist = listOf(sampleAudio)
//    PlayerScreen(
//        audio = sampleAudio,
//        playlist = samplePlaylist,
//    )
//}