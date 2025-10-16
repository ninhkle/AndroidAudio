package com.ninhkle.androidaudioapp.ui.library

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AudioLibraryViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class <T>): T {
        if (modelClass.isAssignableFrom(AudioLibraryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AudioLibraryViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}