package com.ninhkle.androidaudioapp.ui.player

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ninhkle.androidaudioapp.common.data.Audio

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    audio: Audio?,
    playlist: List<Audio> = emptyList(),
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: PlayerViewModel = viewModel()
    LaunchedEffect(audio) {
        audio?.let {
            viewModel.setAudio(it, playlist)
        }
    }
    val state = viewModel.state.value

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Now Playing") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
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
                onPlayPause = viewModel::playPause,
                onNext = viewModel::playNext,
                onPrevious = viewModel::playPrevious,
                onSeek = viewModel::seekTo,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Preview
@Composable
fun PreviewPlayerScreen() {
    val sampleAudio = Audio(
        id = 1L,
        title = "Sample Song",
        artist = "Sample Artist",
        album = "Sample Album",
        duration = 240000L,
        uri = "",
        albumId = 0L
    )
    val samplePlaylist = listOf(sampleAudio)
    PlayerScreen(
        audio = sampleAudio,
        playlist = samplePlaylist,
        onBack = {}
    )
}