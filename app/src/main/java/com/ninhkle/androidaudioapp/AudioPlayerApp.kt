package com.ninhkle.androidaudioapp

import androidx.compose.runtime.Composable
import com.ninhkle.androidaudioapp.core.theme.AndroidAudioAppTheme

@Composable
fun AudioPlayerApp() {
    AndroidAudioAppTheme {
        AppNavigation()
    }
}