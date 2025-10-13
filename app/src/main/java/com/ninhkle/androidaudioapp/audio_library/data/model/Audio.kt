package com.ninhkle.androidaudioapp.audio_library.data.model

data class Audio(
    val id: Long,
    val title: String,
    val artist: String,
    val duration: Long,
    val uri: String
)