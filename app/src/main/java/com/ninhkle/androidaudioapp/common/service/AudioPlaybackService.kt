package com.ninhkle.androidaudioapp.common.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.CommandButton
import androidx.media3.session.MediaNotification
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.session.SessionResult
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.ninhkle.androidaudioapp.R

@UnstableApi
class AudioPlaybackService : MediaSessionService() {
    private var mediaSession: MediaSession? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

        val notificationProvider = createNotificationProvider()
        setMediaNotificationProvider(notificationProvider)

        val player = ExoPlayer.Builder(this).build()
        mediaSession = MediaSession.Builder(this, player)
            .setCallback(MediaSessionCallback())
            .build()

    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }


    override fun onTaskRemoved(rootIntent: Intent?) {
        val player = mediaSession?.player
        if (player?.playWhenReady == false || player?.mediaItemCount == 0) {
            stopSelf()
        }
    }

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Audio Playback",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Audio Playback Controls"
                setShowBadge(false)
            }

            val notificationManger = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManger.createNotificationChannel(channel)
        }
    }

    private fun createNotificationProvider(): MediaNotification.Provider {
        return object : MediaNotification.Provider {
            override fun createNotification(
                mediaSession: MediaSession,
                customLayout: ImmutableList<CommandButton>,
                actionFactory: MediaNotification.ActionFactory,
                onNotificationPostedCallback: MediaNotification.Provider.Callback
            ): MediaNotification {
                // This is where you build the notification that the service will manage.
                val notification = NotificationCompat.Builder(this@AudioPlaybackService, NOTIFICATION_CHANNEL_ID)
                    .setContentTitle("Now Playing") // This will be updated by the media session
                    .setContentText("...")       // This will also be updated
                    .setSmallIcon(R.drawable.ic_default_music_art)
                    .build()

                return MediaNotification(NOTIFICATION_ID, notification)
            }

            override fun handleCustomCommand(
                session: MediaSession,
                action: String,
                extras: Bundle
            ): Boolean {
                // Handle custom commands if you have any
                return false
            }
        }
    }


    inner class MediaSessionCallback : MediaSession.Callback {
        // Implement media session callback methods as needed
    }

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "audio_player_channel"
        const val NOTIFICATION_ID = 1
    }
}