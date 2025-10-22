package com.ninhkle.androidaudioapp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ninhkle.androidaudioapp.common.navigation.AppNavigation
import com.ninhkle.androidaudioapp.common.navigation.Screen
import com.ninhkle.androidaudioapp.common.theme.AndroidAudioAppTheme
import com.ninhkle.androidaudioapp.common.util.hasStoragePermission
import com.ninhkle.androidaudioapp.ui.common.MusicTopAppBar
import com.ninhkle.androidaudioapp.ui.permission.PermissionScreen
import com.ninhkle.androidaudioapp.ui.permission.PermissionState
import com.ninhkle.androidaudioapp.ui.permission.PermissionUIState
import com.ninhkle.androidaudioapp.ui.player.PlayerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioPlayerApp(
    permissionState: PermissionState = PermissionState.Loading,
    playerViewModel: PlayerViewModel,
    onRequestPermissions: () -> Unit = {}
) {
    val context = LocalContext.current
    val navController = rememberNavController()

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    val canNavigateBack = currentRoute == Screen.AudioPlayer.route

    AndroidAudioAppTheme {
        Scaffold(
            topBar = {
                if (currentRoute == Screen.AudioPlayer.route) {
                    MusicTopAppBar(
                        title = "Now Playing",
                        canNavigateBack = canNavigateBack,
                        onNavigateUp = {
                            navController.navigateUp()
                        }
                    )
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                when (permissionState) {
                    is PermissionState.Loading -> {
                        // Show loading screen while checking permissions
                        PermissionScreen(
                            state = PermissionUIState.Loading,
                            onRequestPermissions = onRequestPermissions
                        )
                    }

                    is PermissionState.Granted -> {
                        // Permission granted, show main app
                        AppNavigation(
                            navController = navController,
                            playerViewModel = playerViewModel
                        )
                    }

                    is PermissionState.Denied -> {
                        // Permissions denied, show rationale
                        PermissionScreen(
                            state = PermissionUIState.Denied(
                                hasStoragePermission = hasStoragePermission(context as android.app.Activity)
                            ),
                            onRequestPermissions = onRequestPermissions
                        )
                    }
                }
            }
        }
    }
}