package com.ninhkle.androidaudioapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.ninhkle.androidaudioapp.common.util.checkAndRequestPermissions
import com.ninhkle.androidaudioapp.common.util.handlePermissionResult
import com.ninhkle.androidaudioapp.ui.permission.PermissionsViewModel


class MainActivity : ComponentActivity() {
    private val permissionsViewModel: PermissionsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val permissionState by permissionsViewModel.permissionState.collectAsState()
            AudioPlayerApp(
                permissionState = permissionState,
                onRequestPermissions = {
                    checkAndRequestPermissions(this) {
                        permissionsViewModel.onPermissionsGranted()
                    }
                }
            )
        }

        // Initial permission check
        checkAndRequestPermissions(this) {
            permissionsViewModel.onPermissionsGranted()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
        deviceId: Int
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)
        handlePermissionResult(
            requestCode = requestCode,
            grantResults = grantResults,
            onPermissionGranted =  { permissionsViewModel.onPermissionsGranted() },
            activity = this
        )
    }
}
