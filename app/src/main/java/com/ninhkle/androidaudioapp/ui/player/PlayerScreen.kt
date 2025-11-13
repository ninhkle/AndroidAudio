package com.ninhkle.androidaudioapp.ui.player

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

@Composable
fun PlayerScreen(
    playerViewModel: PlayerViewModel,
) {
    val state = playerViewModel.state.collectAsState().value
    var controlsVisible by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (controlsVisible) 1f else 0f,
        animationSpec = spring(
            dampingRatio = 0.6f,
            stiffness = 200f
        ),
        label = "Controls Scale"
    )

    val alpha by animateFloatAsState(
        targetValue = if (controlsVisible) 1f else 0f,
        animationSpec = spring(),
        label = "Controls Alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        AlbumArt(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { controlsVisible = true }
                ),
        )
        if (controlsVisible) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { controlsVisible = false }
                    )
            )
        }

        PlayerControls(
            state = state,
            onPlayPause = playerViewModel::playPause,
            onNext = playerViewModel::playNext,
            onPrevious = playerViewModel::playPrevious,
            onSeek = playerViewModel::seekTo,
            modifier = Modifier
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    this.alpha = alpha
                }
                .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.2f))
                .padding(24.dp)
        )
    }
}

@Composable
private fun AlbumArt(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.secondaryContainer
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.MusicNote,
            contentDescription = "Album Art",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onPrimaryContainer
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