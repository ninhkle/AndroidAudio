package com.ninhkle.androidaudioapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.ninhkle.androidaudioapp.audio_player.AudioPlayerComposable
import com.ninhkle.androidaudioapp.audio_player.fetchMediaFile
import com.ninhkle.androidaudioapp.core.theme.AndroidAudioAppTheme
import com.ninhkle.androidaudioapp.core.util.checkAndRequestPermissions
import com.ninhkle.androidaudioapp.core.util.handlePermissionResult

class MainActivity : ComponentActivity() {
    private val tag = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(tag, "onCreate called")
        enableEdgeToEdge()
        checkAndRequestPermissions(this) {
            Log.d(tag, "Permission granted callback")
            loadContent()
        }
    }

    private fun loadContent() {
        Log.d(tag, "Loading content")
        setContent {
            AndroidAudioAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val mediaItem = fetchMediaFile(this)
                    Log.d(tag, "Media items fetched: ${mediaItem.size}")
                    AudioPlayerComposable(mediaItems = mediaItem, modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
        deviceId: Int
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)
        Log.d(tag, "onRequestPermissionsResult called")
        handlePermissionResult(requestCode, grantResults, {
            Log.d(tag, "Permission result callback")
            loadContent()
        }, this )
    }
}
