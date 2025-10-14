package com.ninhkle.androidaudioapp.common.data

import android.annotation.SuppressLint

data class Audio(
    val id: Long,
    val title: String,
    val artist: String,
    val album : String,
    val duration: Long,
    val uri: String,
    val albumId : Long
) {
    @SuppressLint("DefaultLocale")
    fun formattedDuration(): String {
        val minutes = (duration / 1000) / 60
        val seconds = (duration / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}