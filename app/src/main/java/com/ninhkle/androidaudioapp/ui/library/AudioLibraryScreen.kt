package com.ninhkle.androidaudioapp.ui.library

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.ninhkle.androidaudioapp.common.data.Audio
import com.ninhkle.androidaudioapp.common.navigation.Screen
import com.ninhkle.androidaudioapp.ui.common.AudioItemShimmer
import com.ninhkle.androidaudioapp.ui.player.MiniPlayer
import com.ninhkle.androidaudioapp.ui.player.PlayerViewModel


@Composable
fun AudioLibraryScreen(
    playerViewModel: PlayerViewModel,
    navController: NavHostController,
    onNavigateToPlayer: (Audio, List<Audio>) -> Unit
) {
    val context = LocalContext.current
    val viewModel: AudioLibraryViewModel = viewModel(
        factory = AudioLibraryViewModelFactory(context)
    )
    val state = viewModel.state.value

    val playerState = playerViewModel.state.collectAsState().value

    Box(
        modifier = Modifier.fillMaxSize().background(color = MaterialTheme.colorScheme.background)
    )
    {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row (
                modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Audio Library",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(4.dp),
                )
                IconButton(onClick = { viewModel.refresh() }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                }
            }

            Spacer(modifier = Modifier.padding(16.dp))

            when {
                state.isLoading -> {
                    LazyColumn {
                        items(10) {
                            AudioItemShimmer()
                            HorizontalDivider()
                        }
                    }
                }

                state.error != null -> {
                    Column {
                        Text("Error loading music")
                        Text(state.error)
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
        // Mini Player
        val currentAudio = playerState.currentAudio
        if (currentAudio != null) {
            MiniPlayer(
                state = playerState,
                onPlayPause = playerViewModel::playPause,
                onNext = playerViewModel::playNext,
                onExpand = {
                    navController.navigate(Screen.AudioPlayer.route)
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(64.dp)
            )
        }
    }
}

@Composable
fun AudioList(
    audioList: List<Audio>,
    onPlayClick: (Audio) -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 64.dp)
    ) {
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

//@Preview
//@Composable
//fun PreviewAudioLibraryScreen() {
//    AudioLibraryScreen( onNavigateToPlayer = { _, _ -> })
//}