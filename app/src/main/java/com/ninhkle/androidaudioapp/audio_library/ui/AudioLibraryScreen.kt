package com.ninhkle.androidaudioapp.audio_library.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ninhkle.androidaudioapp.audio_library.data.model.Audio

@Composable
fun AudioLibraryScreen() {
    val sampleAudioList = listOf(
        Audio(
            id = 1,
            title = "Bohemian Rhapsody",
            artist = "Queen",
            duration = 354000, // 5:54 in milliseconds
            uri = ""
        ),
        Audio(
            id = 2,
            title = "Sweet Child O' Mine",
            artist = "Guns N' Roses",
            duration = 356000,
            uri = ""
        ),
        Audio(
            id = 3,
            title = "Hotel California",
            artist = "Eagles",
            duration = 391000,
            uri = ""
        ),
        Audio(
            id = 4,
            title = "Smells Like Teen Spirit",
            artist = "Nirvana",
            duration = 301000,
            uri = ""
        ),
        Audio(
            id = 5,
            title = "Billie Jean",
            artist = "Michael Jackson",
            duration = 294000,
            uri = ""
        )
    )

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text(text = "Audio Library",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        AudioList(audioList = sampleAudioList)
    }
}

@Composable
fun AudioList(audioList: List<Audio>) {
    LazyColumn {
        items(
            items = audioList,
            key = { audio -> audio.id }
        ) { audio ->
            AudioListItem(audio = audio)
            HorizontalDivider()
        }
    }
}

@Preview
@Composable
fun PreviewAudioLibraryScreen() {
    AudioLibraryScreen()
}