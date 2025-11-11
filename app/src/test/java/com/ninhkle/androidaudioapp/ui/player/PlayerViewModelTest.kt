package com.ninhkle.androidaudioapp.ui.player

import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import app.cash.turbine.test
import com.ninhkle.androidaudioapp.MainCoroutineRule // We still use this for proper dispatcher setup
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [Config.OLDEST_SDK])
@OptIn(ExperimentalCoroutinesApi::class)
class PlayerViewModelTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: PlayerViewModel
    private lateinit var mockMediaController: MediaController

    @Before
    fun setUp() {
        viewModel = PlayerViewModel()
        mockMediaController = mockk(relaxed = true)
    }

    @Test
    fun `setMediaController synchronizes initial state correctly`() = runTest {
        // ARRANGE
        val fakeUri = Uri.parse("file://initial/song")
        val initialMediaItem = MediaItem.Builder()
            .setMediaId("1")
            .setMediaMetadata(MediaMetadata.Builder().setTitle("Initial Song").build())
            .setRequestMetadata(MediaItem.RequestMetadata.Builder().setMediaUri(fakeUri).build())
            .build()

        every { mockMediaController.currentMediaItem } returns initialMediaItem
        every { mockMediaController.isPlaying } returns true
        every { mockMediaController.currentPosition } returns 50000L
        every { mockMediaController.duration } returns 300000L

        // ACT
        viewModel.setMediaController(mockMediaController, enablePositionUpdatesInTest = false)

        // ASSERT
        val currentState = viewModel.state.value
        assertEquals("Initial Song", currentState.currentAudio?.title)
        assertEquals(true, currentState.isPlaying)
        assertEquals("file://initial/song", currentState.currentAudio?.uri.toString())
    }

    @Test
    fun `onIsPlayingChanged listener updates isPlaying state`() = runTest {
        // ARRANGE
        val mediaItemForThisTest = MediaItem.Builder()
            .setMediaId("0")
            .setRequestMetadata(MediaItem.RequestMetadata.Builder().setMediaUri(Uri.EMPTY).build())
            .build()
        every { mockMediaController.currentMediaItem } returns mediaItemForThisTest

        val playerListenerSlot = slot<Player.Listener>()
        every { mockMediaController.addListener(capture(playerListenerSlot)) } answers {}

        // ACT
        viewModel.setMediaController(mockMediaController, enablePositionUpdatesInTest = false)

        // ASSERT
        viewModel.state.test {
            var currentState = awaitItem()
            assertEquals(false, currentState.isPlaying)

            playerListenerSlot.captured.onIsPlayingChanged(true)
            currentState = awaitItem()
            assertEquals(true, currentState.isPlaying)

            playerListenerSlot.captured.onIsPlayingChanged(false)
            currentState = awaitItem()
            assertEquals(false, currentState.isPlaying)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `playPause delegates correctly to mediaController`() = runTest {
        // ARRANGE
        val mediaItemForThisTest = MediaItem.Builder()
            .setMediaId("0")
            .setRequestMetadata(MediaItem.RequestMetadata.Builder().setMediaUri(Uri.EMPTY).build())
            .build()
        every { mockMediaController.currentMediaItem } returns mediaItemForThisTest

        // ACT
        viewModel.setMediaController(mockMediaController, enablePositionUpdatesInTest = false)

        // ASSERT
        every { mockMediaController.isPlaying } returns true
        viewModel.playPause()
        verify { mockMediaController.pause() }

        every { mockMediaController.isPlaying } returns false
        viewModel.playPause()
        verify { mockMediaController.play() }
    }
}
