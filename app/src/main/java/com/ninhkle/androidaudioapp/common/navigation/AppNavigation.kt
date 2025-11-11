package com.ninhkle.androidaudioapp.common.navigation

import android.content.ComponentName
import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.google.common.util.concurrent.MoreExecutors
import com.ninhkle.androidaudioapp.common.service.AudioPlaybackService
import com.ninhkle.androidaudioapp.ui.player.PlayerViewModel

@OptIn(UnstableApi::class)
@Composable
fun AppNavigation(
    navController: NavHostController,
    playerViewModel: PlayerViewModel
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        val sessionToken = SessionToken(context,
            ComponentName(context, AudioPlaybackService::class.java)
        )
        val controllerFuture =
            MediaController.Builder(context, sessionToken).buildAsync()

        controllerFuture.addListener(
            {
                // When the controller is ready...
                val mediaController = controllerFuture.get()
                // ...give it to the ViewModel.
                playerViewModel.setMediaController(mediaController)
            },
            MoreExecutors.directExecutor()
        )
    }

    NavHost(
        navController = navController,
        startDestination = Screen.AudioLibrary.route
    ) {
        audioPlayerGraph(
            navController = navController,
            playerViewModel = playerViewModel
        )
    }
}