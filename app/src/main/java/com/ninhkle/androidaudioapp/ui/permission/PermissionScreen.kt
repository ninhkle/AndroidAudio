package com.ninhkle.androidaudioapp.ui.permission

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

sealed class PermissionUIState {
    data object Loading : PermissionUIState()
    data class Denied(val hasStoragePermission: Boolean) : PermissionUIState()
}

@Composable
fun PermissionScreen(
    state: PermissionUIState,
    onRequestPermissions: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when (state) {
                is PermissionUIState.Loading -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Checking permissions...",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                is PermissionUIState.Denied -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Permissions Required",
                            style = MaterialTheme.typography.headlineMedium,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "This app needs permissions to:\n\n" +
                                    "• Access your audio files to play music\n" +
                                    "• Show notifications for music controls\n" +
                                    "• Play music in the background\n\n" +
                                    "Without these permissions, the app won't be able to find or play your music.",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Start,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        Button(
                            onClick = onRequestPermissions,
                            modifier = Modifier.fillMaxWidth(0.8f)
                        ) {
                            Text("Grant Permissions")
                        }

                        if (!state.hasStoragePermission) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Storage permission is required to find your music files",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}