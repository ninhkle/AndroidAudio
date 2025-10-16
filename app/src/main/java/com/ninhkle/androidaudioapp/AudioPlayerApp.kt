package com.ninhkle.androidaudioapp

import androidx.compose.runtime.Composable
import com.ninhkle.androidaudioapp.common.navigation.AppNavigation
import com.ninhkle.androidaudioapp.common.theme.AndroidAudioAppTheme

@Composable
fun AudioPlayerApp() {
    AndroidAudioAppTheme {
        AppNavigation()
    }
}