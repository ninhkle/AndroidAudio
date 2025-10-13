package com.ninhkle.androidaudioapp.audio_library.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun AudioLibraryScreen() {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text(text = "Audio Library", style = MaterialTheme.typography.headlineMedium)
        Text(text = "List of audio files will be displayed here.", style = MaterialTheme.typography.bodyMedium)
    }
}

@Preview
@Composable
fun PreviewAudioLibraryScreen() {
    AudioLibraryScreen()
}