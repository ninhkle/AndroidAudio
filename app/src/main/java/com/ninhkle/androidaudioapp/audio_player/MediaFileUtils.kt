package com.ninhkle.androidaudioapp.audio_player

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata

fun fetchMediaFile(context: Context) : List<MediaItem> {
    val mediaItems = mutableListOf<MediaItem>()
    val tag = "fetchMediaFile"

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (ContextCompat.checkSelfPermission(
                context, Manifest.permission.READ_MEDIA_AUDIO
            ) != PackageManager.PERMISSION_GRANTED) {
            Log.d(tag, "Permission not granted. Requesting permission.")
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.READ_MEDIA_AUDIO),
                1
            )
            return mediaItems
        }
    } else {
        if (ContextCompat.checkSelfPermission(
                context, Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED) {
            Log.d(tag, "Permission not granted. Requesting permission.")
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                1
            )
            return mediaItems
        }
    }



    Log.d(tag, "Permission granted. Fetching media files.")
    // Define the projection to specify which columns to retrieve
    val projection = arrayOf(
        MediaStore.MediaColumns._ID,
        MediaStore.MediaColumns.DISPLAY_NAME,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.ALBUM,
        MediaStore.Audio.Media.ARTIST,
    )

    // Define the selection criteria (optional)
    val selection = null
    val selectionArgs = null

    // Define the sort order (optional)
    val sortOrder = "${MediaStore.MediaColumns.DATE_ADDED} DESC"

    // Query the MediaStore to retrieve audio files
    val queryUri = MediaStore.Files.getContentUri("external")
    val cursor: Cursor? = context.contentResolver.query(
        queryUri,
        projection,
        selection,
        selectionArgs,
        sortOrder
    )

    cursor?.use {
        val idColumn = it.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
        val nameColumn = it.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
        val titleColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
        val albumColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
        val artistColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)

        while (it.moveToNext()) {
            val id = it.getLong(idColumn)
            val name = it.getString(nameColumn)
            val title = it.getString(titleColumn)
            val album = it.getString(albumColumn)
            val artist = it.getString(artistColumn)
            val contentUri = Uri.withAppendedPath(queryUri, id.toString())


            Log.d(tag, "Found media file: $name (ID: $id)")

           val mediaMetaData = MediaMetadata.Builder()
               .setTitle(title)
               .setAlbumTitle(album)
               .setArtist(artist)
               .build()

            val mediaItem = MediaItem.Builder()
                .setUri(contentUri)
                .setMediaMetadata(mediaMetaData)
                .build()

            logMediaMetadata(mediaMetaData)
            mediaItems.add(mediaItem)
        }
    }

    Log.d(tag, "Total media files found: ${mediaItems.size}")
    return mediaItems

}

fun getAlbumArtUri(context: Context, albumId: Long): Uri? {
    val albumArtUri = Uri.parse("content://media/external/audio/albumart")
    return Uri.withAppendedPath(albumArtUri, albumId.toString())
}

fun logMediaMetadata(mediaMetadata: MediaMetadata) {
    val tag = "MediaMetadata"
    Log.d(tag, "Title: ${mediaMetadata.title}")
    Log.d(tag, "Album: ${mediaMetadata.albumTitle}")
    Log.d(tag, "Artist: ${mediaMetadata.artist}")
    Log.d(tag, "Description: ${mediaMetadata.description}")
    // Log more metadata fields as needed
}