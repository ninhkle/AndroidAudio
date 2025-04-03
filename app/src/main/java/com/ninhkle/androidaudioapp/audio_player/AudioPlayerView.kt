package com.ninhkle.androidaudioapp.audio_player

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil.compose.rememberAsyncImagePainter

@Composable
fun AudioPlayerComposable(mediaItems: List<MediaItem>, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            mediaItems.forEach { mediaItem ->
                addMediaItem(mediaItem)
            }
            prepare()
            playWhenReady = true
        }
    }

    var currentMediaItem by remember { mutableStateOf(mediaItems.firstOrNull()) }

    DisposableEffect(Unit) {
        val listener = object : Player.Listener {
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                currentMediaItem = mediaItem
            }
        }
        exoPlayer.addListener(listener)
        onDispose {
            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        currentMediaItem?.let { mediaItem ->
            MediaInfoComposable(
                title = mediaItem.mediaMetadata.title.toString(),
                albumArtUri = mediaItem.mediaMetadata.artworkUri
            )
        }
        AndroidView(
            factory = {
                PlayerView(context).apply {
                    player = exoPlayer
                    // Customize PlayerView Attributes here
                }
            },
            modifier = modifier.fillMaxSize()
        )
    }
}

@Composable
fun MediaInfoComposable(title: String, albumArtUri: Uri?, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(16.dp)) {
        albumArtUri?.let {
            Image(
                painter = rememberAsyncImagePainter(it),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth().height(200.dp),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Preview
@Composable
fun AudioPlayerPreview() {
    val videoUri = Uri.parse(
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
    )
    val mediaItem = MediaItem.Builder()
        .setUri(videoUri)
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setTitle("Big Buck Bunny")
                .build()
        )
        .build()
    val mediaItems = listOf(mediaItem)
    AudioPlayerComposable(mediaItems = mediaItems)
}