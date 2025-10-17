package com.ninhkle.androidaudioapp.ui.permission

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed class PermissionState {
    data object Loading: PermissionState()
    data object Granted: PermissionState()
    data object Denied: PermissionState()
}

class PermissionsViewModel : ViewModel() {
    private val _permissionState = MutableStateFlow<PermissionState>(PermissionState.Loading)
    val permissionState: StateFlow<PermissionState> = _permissionState.asStateFlow()

    fun onPermissionsGranted() {
        _permissionState.value = PermissionState.Granted
    }

    fun onPermissionsDenied() {
        _permissionState.value = PermissionState.Denied
    }

    fun resetToLoading() {
        _permissionState.value = PermissionState.Loading
    }
}