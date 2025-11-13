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
        playerViewModel.initializeController(context)
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