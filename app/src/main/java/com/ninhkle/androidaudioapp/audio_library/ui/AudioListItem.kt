package com.ninhkle.androidaudioapp.audio_library.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ninhkle.androidaudioapp.audio_library.data.model.Audio

@Composable
fun AudioListItem(
    audio: Audio,
    modifier: Modifier = Modifier
) {
    Column (modifier = modifier.padding(8.dp)) {
        Text(text = audio.title)
        Text(text = audio.artist)
    }
}

@Preview
@Composable
fun PreviewAudioListItem() {
    AudioListItem(
        audio = Audio(
            id = 1L,
            title = "Sample Song",
            artist = "Sample Artist",
            duration = 180000L,
            uri = ""
        )
    )
}