package com.ninhkle.androidaudioapp.common.data

interface AudioRepository {
    suspend fun getAudioFiles(): List<Audio>
}