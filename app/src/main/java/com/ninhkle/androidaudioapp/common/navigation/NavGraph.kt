package com.ninhkle.androidaudioapp.common.navigation

import android.app.Activity
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.ninhkle.androidaudioapp.ui.library.AudioLibraryScreen
import com.ninhkle.androidaudioapp.ui.library.AudioLibraryViewModel
import com.ninhkle.androidaudioapp.ui.library.AudioLibraryViewModelFactory
import com.ninhkle.androidaudioapp.ui.player.PlayerScreen
import com.ninhkle.androidaudioapp.ui.player.PlayerViewModel

fun NavGraphBuilder.audioPlayerGraph(
    navController: NavController,
    playerViewModel: PlayerViewModel
) {
    composable(route = Screen.AudioLibrary.route) { backStackEntry ->
        val libraryViewModel: AudioLibraryViewModel = viewModel(
            factory = AudioLibraryViewModelFactory(LocalContext.current)
        )
        val audioList = libraryViewModel.state.value.audioList
        AudioLibraryScreen(
            playerViewModel = playerViewModel,
            navController = navController as NavHostController,
            onNavigateToPlayer = { audio, list->
                playerViewModel.setAudio(audio, audioList)

                // Navigate to player with arguments
                navController.navigate(Screen.AudioPlayer.route)
            }
        )
    }

    composable(
        route = Screen.AudioPlayer.route,
        deepLinks = listOf(
            navDeepLink { uriPattern = "android-app://com.ninhkle.androidaudioapp/player" }
        )
    ) { backStackEntry ->
//        val context = LocalContext.current
//        val viewModel: AudioLibraryViewModel = viewModel(
//            factory = AudioLibraryViewModelFactory(context)
//        )
//        val audioId = backStackEntry.arguments?.getLong("audioId") ?: -1L
//        val audioList = viewModel.state.value.audioList
//        println("DEBUG: Received audioId: $audioId")
//        val audioToPlay = audioList.find { it.id == audioId}
//        println("DEBUG: Found audio: ${audioToPlay?.title}")


        PlayerScreen(
            playerViewModel = playerViewModel,
        )

    }
}