package com.ninhkle.androidaudioapp.common.util

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

private const val REQUEST_READ_MEDIA_AUDIO = 1
private const val tag = "PermissionUtils"

fun checkAndRequestPermissions(activity: Activity, onPermissionGranted: () -> Unit) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.READ_MEDIA_AUDIO),
                REQUEST_READ_MEDIA_AUDIO
            )
        } else {
            onPermissionGranted()
        }
    } else {
        if (ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_READ_MEDIA_AUDIO
            )
        } else {
            // Permission is already granted, proceed with the action
            onPermissionGranted()
        }
    }
}

fun handlePermissionResult(
    requestCode: Int,
    grantResults: IntArray,
    onPermissionGranted: () -> Unit,
    activity: Activity
    ) {
    if (requestCode == REQUEST_READ_MEDIA_AUDIO) {
        if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            // Permission was granted
            onPermissionGranted()
        } else {
            // Permission denied, you can show a message to the user or handle it accordingly
            Log.d(tag, "Permission denied for reading external storage. handlePermissionResult()")
            AlertDialog.Builder(activity)
                .setTitle("Permission Required")
                .setMessage("This app needs access to your external storage to fetch media files. Please grant the permission.")
                .setPositiveButton("OK") { _, _ ->
                    checkAndRequestPermissions(activity, onPermissionGranted)
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }
}