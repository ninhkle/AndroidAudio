# Android Audio Player

A modern music player for Android built with Jetpack Compose and Media3.

## How It's Made
### Tech Stack: Kotlin, Jetpack Compose, Media3 (ExoPlayer), MVVM Architecture, Foreground Service

The app uses a single `AudioPlaybackService` for all playback, with `PlayerViewModel` acting as a controller. The player state persists across navigation and survives configuration changes. For the source of songs, the model `Audio` and its compliment `AudioRepository` and `LocalAudioDataSource` pulls songs from local storage as a List of audio. The main ViewModels in this app, `AudioLibraryViewModel` and `PlayerViewModel`, which are responsible for populating the Library audio list and controlling the PlaybackService to handle audio controls. For permission handling, we have a simple `PermissionViewModel` to handle permission StateFlow to grant the app permission to access local storage, foreground service and push notification.

The navigation is mainly handled by Jetpack Compose NavGraph/NavHost. This allows the app to have a single source of navigation logic and handles all app navigation.

The `AudioPlaybackService` handles all playback, including MediaSession (for audio persisting in the background/foreground) and Nofication (for control through notification). 


## Lesson Learned

The main challenge for this app was learning how to implement a feature that survives through all configuration of the app (navigating out back to library screen, running in background, resuming back into the screen). At first this was solved through connecting the playbackservice and playerviewmodel, making playbackservice the main operations while making the playerviewmodel as the controller of playbackservice. I also made it so that playerviewmodel is created at root level of the app so that it can survive throughout the app lifecycle. This challenge become much more difficult once we account for push notification. I implemented StateFlow on the mediacontroller and LaunchedEffect on playerscreen so that, when navigating back to the playerscreen through push notification, the media controller survives the process.
