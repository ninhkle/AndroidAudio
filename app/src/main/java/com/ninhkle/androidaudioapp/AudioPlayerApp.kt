package com.ninhkle.androidaudioapp

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.ninhkle.androidaudioapp.common.navigation.AppNavigation
import com.ninhkle.androidaudioapp.common.theme.AndroidAudioAppTheme
import com.ninhkle.androidaudioapp.common.util.hasStoragePermission
import com.ninhkle.androidaudioapp.ui.permission.PermissionScreen
import com.ninhkle.androidaudioapp.ui.permission.PermissionState
import com.ninhkle.androidaudioapp.ui.permission.PermissionUIState

@Composable
fun AudioPlayerApp(
    permissionState: PermissionState = PermissionState.Loading,
    onRequestPermissions: () -> Unit = {}
) {
    val context = LocalContext.current

    AndroidAudioAppTheme {
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
                AppNavigation()
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
        AppNavigation()
    }
}