package com.ninhkle.androidaudioapp.common.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.ninhkle.androidaudioapp.common.data.Audio
import com.ninhkle.androidaudioapp.ui.library.AudioLibraryScreen
import com.ninhkle.androidaudioapp.ui.library.AudioLibraryViewModel
import com.ninhkle.androidaudioapp.ui.player.PlayerScreen

fun NavGraphBuilder.audioPlayerGraph(
    navController: NavController,
    audioLibraryViewModel: AudioLibraryViewModel
) {
    composable(route = Screen.AudioLibrary.route) {
        AudioLibraryScreen(
            viewModel = audioLibraryViewModel,
            onNavigateToPlayer = { audio, playlist ->
                // Navigate to player with arguments
                navController.navigate(Screen.AudioPlayer.createRoute(audio.id))
            }
        )
    }

    composable(
        route = Screen.AudioPlayer.route,
        arguments = listOf(
            navArgument(name = "audioId") {
                type = NavType.LongType
                defaultValue = -1L
            }
        )
    ) { backStackEntry ->
        val audioId = backStackEntry.arguments?.getLong("audioId") ?: -1L
        val audioList = audioLibraryViewModel.state.value.audioList
        println("DEBUG: Received audioId: $audioId")
        val audioToPlay = audioList.find { it.id == audioId}
        println("DEBUG: Found audio: ${audioToPlay?.title}")


        PlayerScreen(
            audio = audioToPlay,
            playlist = audioList,
            onBack = { navController.popBackStack() }
        )

    }
}