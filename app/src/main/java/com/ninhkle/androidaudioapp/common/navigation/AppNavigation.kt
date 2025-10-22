package com.ninhkle.androidaudioapp.common.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.ninhkle.androidaudioapp.common.data.Audio
import com.ninhkle.androidaudioapp.ui.library.AudioLibraryScreen
import com.ninhkle.androidaudioapp.ui.library.AudioLibraryViewModel
import com.ninhkle.androidaudioapp.ui.player.PlayerViewModel

@Composable
fun AppNavigation(
    navController: NavHostController,
    playerViewModel: PlayerViewModel
) {

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