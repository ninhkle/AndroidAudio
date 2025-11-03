package com.ninhkle.androidaudioapp.ui.player

import android.app.Application
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import app.cash.turbine.test
import com.ninhkle.androidaudioapp.common.data.Audio
import com.ninhkle.androidaudioapp.ui.player.PlayerViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment


@RunWith(RobolectricTestRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
class PlayerViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: PlayerViewModel

    private lateinit var mockApplication: Application
    private lateinit var mockMediaController: MediaController

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockApplication = RuntimeEnvironment.getApplication()
        mockMediaController = mockk(relaxed = true)
        viewModel = PlayerViewModel(mockApplication)
    }


    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onMediaItemTransition updates state with new audio from metadata`() = runTest {
        val playerListenerSlot = slot<Player.Listener>()
        every { mockMediaController.addListener(capture(playerListenerSlot)) } answers {}

        viewModel.setupControllerListeners(mockMediaController)

        val fakeMediaItem = MediaItem.Builder()
            .setMediaId("123")
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle("Test Title")
                    .setArtist("Test Artist")
                    .build()
            )
            .build()

        every { mockMediaController.duration } returns 180000L

        viewModel.state.test {
            val initialState: PlayerState = awaitItem()
            assertEquals(null, initialState.currentAudio)

            playerListenerSlot.captured.onMediaItemTransition(
                fakeMediaItem,
                Player.MEDIA_ITEM_TRANSITION_REASON_AUTO
            )
            val newState = awaitItem()
            assertEquals("123", newState.currentAudio?.id)
            assertEquals("Test Title", newState.currentAudio?.title)
            assertEquals("Test Artist", newState.currentAudio?.artist)
            assertEquals(180000L, newState.currentAudio?.duration)

            cancelAndIgnoreRemainingEvents()
        }
    }
}