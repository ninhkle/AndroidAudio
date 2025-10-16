package com.ninhkle.androidaudioapp.common.navigation

sealed class Screen(val route: String) {
    object AudioLibrary : Screen("audio_library")
    object AudioPlayer : Screen("audio_player?audioId={audioId}") {
        fun createRoute(audioId: Long) = "audio_player?audioId=$audioId"
    }
}