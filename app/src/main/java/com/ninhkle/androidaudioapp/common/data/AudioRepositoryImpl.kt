package com.ninhkle.androidaudioapp.common.data

class AudioRepositoryImpl(
    private val localAudioDataSource: LocalAudioDataSource
) : AudioRepository {
    override suspend fun getAudioFiles(): List<Audio> {
        return localAudioDataSource.getAudioFiles()
    }
}