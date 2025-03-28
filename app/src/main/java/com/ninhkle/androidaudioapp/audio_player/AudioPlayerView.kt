package com.ninhkle.androidaudioapp.audio_player

import android.net.Uri
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

@Composable
fun AudioPlayerComposable(mediaUris: List<Uri>, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            mediaUris.forEach { uri ->
                addMediaItem(MediaItem.fromUri(uri))
            }
            prepare()
            playWhenReady = true
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
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

@Preview
@Composable
fun AudioPlayerPreview() {
    val videoUri = Uri.parse(
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
    )
    val videoUris = List(1) { videoUri } // Wrapping the single URI in a list for the preview
    AudioPlayerComposable(mediaUris = videoUris)
}