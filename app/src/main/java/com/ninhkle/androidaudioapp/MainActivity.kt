package com.ninhkle.androidaudioapp

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.ninhkle.androidaudioapp.audio_player.AudioPlayerComposable
import com.ninhkle.androidaudioapp.ui.theme.AndroidAudioAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidAudioAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val videoUri = Uri.parse(
                        "https://storage.googleapis.com/exoplayer-test-media-0/BigBuckBunny/BigBuckBunny_320x180.mp4"
                    )
                    AudioPlayerComposable(mediaUri = videoUri)
                }
            }
        }
    }
}

//@Composable
//fun Greeting(name: String, modifier: Modifier = Modifier) {
//    Text(
//        text = "Hello $name!",
//        modifier = modifier
//    )
//}
//
//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    AndroidAudioAppTheme {
//        Greeting("Android")
//    }
//}