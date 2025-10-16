package com.ninhkle.androidaudioapp.ui.library

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ninhkle.androidaudioapp.common.data.Audio


@Composable
fun AudioLibraryScreen(
    viewModel: AudioLibraryViewModel,
    onNavigateToPlayer: (Audio, List<Audio>) -> Unit
) {
    val state = viewModel.state.value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row {
            Text(
                text = "Audio Library",
                style = MaterialTheme.typography.headlineMedium
            )
            IconButton(onClick = { viewModel.refresh() }) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh")
            }
        }

        Spacer(modifier = Modifier.padding(16.dp))

        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            state.error != null -> {
                Column {
                    Text("Error loading music")
                    Text(state.error ?: "Unknown error")
                    Button(onClick = { viewModel.refresh() }) {
                        Text("Retry")
                    }
                }
            }

            state.audioList.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No audio files found")
                }
            }

            else -> {
                AudioList(
                    audioList = state.audioList,
                    onPlayClick = { audio ->
                        onNavigateToPlayer(audio, state.audioList)
                    }
                )
            }
        }

    }
}

@Composable
fun AudioList(
    audioList: List<Audio>,
    onPlayClick: (Audio) -> Unit = {}
) {
    LazyColumn {
        items(
            items = audioList,
            key = { audio -> audio.id }
        ) { audio ->
            AudioListItem(
                audio = audio,
                onPlayClick = { onPlayClick(audio) }
            )
            HorizontalDivider()
        }
    }
}

@Preview
@Composable
fun PreviewAudioLibraryScreen() {
    val context = LocalContext.current
    val viewModel: AudioLibraryViewModel = remember {
        AudioLibraryViewModel.create(context)
    }
    AudioLibraryScreen(viewModel, onNavigateToPlayer = { _, _ -> })
}