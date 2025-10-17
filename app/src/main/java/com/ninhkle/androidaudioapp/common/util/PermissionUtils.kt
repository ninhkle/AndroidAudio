package com.ninhkle.androidaudioapp.common.util

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

private const val REQUEST_ALL_PERMISSIONS = 1
private const val tag = "PermissionUtils"

// Define all required permissions
private fun getRequiredPermissions(): Array<String> {
    val permissions = mutableListOf<String>()

    // Always required permissions
    permissions.add(Manifest.permission.INTERNET)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        permissions.add(Manifest.permission.FOREGROUND_SERVICE)
    }

    // Storage permissions based on Android version
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        // Android 13+ (API 33+)
        permissions.add(Manifest.permission.READ_MEDIA_AUDIO)
        permissions.add(Manifest.permission.POST_NOTIFICATIONS)
    } else {
        // Below Android 13
        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    return permissions.toTypedArray()
}

fun checkAndRequestPermissions(activity : Activity, onPermissionGranted: () -> Unit) {
    val requiredPermissions = getRequiredPermissions()

    // Check if all permissions granted
    if (hasAllRequiredPermissions(activity, requiredPermissions)) {
        onPermissionGranted()
        return
    }

    // Filter out already granted permissions
    val permissionsToRequest = requiredPermissions.filter { permission ->
        ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED
    }.toTypedArray()

    if (permissionsToRequest.isEmpty()) {
        onPermissionGranted()
        return
    }

    // Check if we should show permission rationale for any permission
    val shouldShowRationale = permissionsToRequest.any { permission ->
        ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
    }

    if (shouldShowRationale) {
        showPermissionRationale(activity, onPermissionGranted, permissionsToRequest)
    } else {
        // Request permissions directly
        ActivityCompat.requestPermissions(
            activity,
            permissionsToRequest,
            REQUEST_ALL_PERMISSIONS
        )
    }
}

fun handlePermissionResult(
    requestCode: Int,
    grantResults: IntArray,
    onPermissionGranted: () -> Unit,
    activity: Activity
) {
    if (requestCode == REQUEST_ALL_PERMISSIONS) {
        val allGranted = grantResults.all { it == PackageManager.PERMISSION_GRANTED }

        if (allGranted) {
            // ALL permissions granted
            onPermissionGranted()
        } else {
            // Some permissions denied
            Log.d(tag, "Some permissions were denied")
            showPermissionDeniedDialog(activity, onPermissionGranted)
        }
    }
}

private fun hasAllRequiredPermissions(activity: Activity, permissions: Array<String>): Boolean {
    return permissions.all { permission ->
        ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED
    }
}

private fun showPermissionRationale(
    activity: Activity,
    onPermissionGranted: () -> Unit,
    permissionsToRequest: Array<String>
) {
    AlertDialog.Builder(activity)
        .setTitle("Permissions Required")
        .setMessage("This app needs the following permissions to function properly:\n\n" +
                "• Internet access (for online features)\n" +
                "• Audio file access (to play your music)\n" +
                "• Notifications (for music controls)\n" +
                "• Background playback (to keep music playing)\n\n" +
                "Please grant these permissions to enjoy all features.")
        .setPositiveButton("OK") { _, _ ->
            ActivityCompat.requestPermissions(
                activity,
                permissionsToRequest,
                REQUEST_ALL_PERMISSIONS
            )
        }
        .setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
            showAppFunctionalityLimitedMessage(activity, onPermissionGranted)
        }
        .setCancelable(false)
        .show()
}

private fun showPermissionDeniedDialog(activity: Activity, onPermissionGranted: () -> Unit) {
    AlertDialog.Builder(activity)
        .setTitle("Permissions Required")
        .setMessage("Some permissions were denied. The app may not function properly. " +
                "You can grant the permissions in Settings.")
        .setPositiveButton("Settings") { _, _ ->
            // Open app settings
            openAppSetting(activity)
        }
        .setNegativeButton("Continue Anyway") { _, _ ->
            // User wants to continue with limited functionality
            onPermissionGranted()
        }
        .setCancelable(false)
        .show()
}

private fun showAppFunctionalityLimitedMessage(activity: Activity, onPermissionGranted: () -> Unit) {
    AlertDialog.Builder(activity)
        .setTitle("Limited Functionality")
        .setMessage("The app will have limited functionality without all permissions. " +
                "You can grant permissions later in Settings.")
        .setPositiveButton("Continue") { _, _ ->
            // Continue with limited functionality
            onPermissionGranted()
        }
        .setNegativeButton("Exit") { _, _ ->
            activity.finish()
        }
        .setCancelable(false)
        .show()
}

private fun openAppSetting(activity: Activity) {
    try {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", activity.packageName, null)
        intent.data = uri
        activity.startActivity(intent)
    } catch (e: Exception) {
        Log.e(tag, "Failed to open app settings", e)
        // Fallback: open general settings
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        activity.startActivity(intent)
    }
}

// Individual permission check helpers (optional, for specific features)
fun hasStoragePermission(activity: Activity): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.READ_MEDIA_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    } else {
        ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }
}

fun hasNotificationPermission(activity: Activity): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    } else {
        true
    }
}
