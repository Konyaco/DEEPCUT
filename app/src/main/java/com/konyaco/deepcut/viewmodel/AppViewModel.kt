package com.konyaco.deepcut.viewmodel

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.media.session.PlaybackState
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.ImageBitmap
import androidx.core.graphics.decodeBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Metadata
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.exoplayer.MetadataRetriever
import androidx.media3.exoplayer.source.TrackGroupArray
import androidx.media3.session.MediaController
import com.google.common.util.concurrent.FutureCallback
import com.google.common.util.concurrent.Futures
import com.konyaco.deepcut.repository.MusicRepository
import com.konyaco.deepcut.repository.model.Music
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.microseconds
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes

private const val TAG = "AppViewModel"

@Singleton
class AppViewModel @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val musicRepository: MusicRepository
) : ViewModel() {
    val showPlayScreen = mutableStateOf(false)
    val isPlaying = mutableStateOf(false)

    val isLoading = mutableStateOf(true)
    val musics = mutableStateOf(emptyList<Music>())
    private var _musics = emptySet<Music>()

    val title = mutableStateOf("")
    val artist = mutableStateOf("")
    val album = mutableStateOf("")
    val progress = mutableStateOf(0f)
    val artworkUri = mutableStateOf<Uri?>(null)
    val artworkImage = mutableStateOf<ByteArray?>(null)
    val duration = mutableStateOf(0L)
    val currentPositionStr = mutableStateOf("0:00")
    val durationStr = mutableStateOf("0:00")

    private var currentSong: Music? = null

    private lateinit var mediaController: MediaController
    private lateinit var updateProgressJob: Job

    private fun formatDuration(durationMs: Long): String {
        val duration = durationMs.milliseconds
        val m = duration.inWholeMinutes
        val s = (duration - m.minutes).inWholeSeconds
        return "${m}:${"%02d".format(s)}"
    }

    fun setMediaController(mediaController: MediaController) {
        this.mediaController = mediaController
        updateProgressJob = viewModelScope.launch(Dispatchers.Default) {
            while (isActive) {
                val (buffered, currentPosition) = withContext(Dispatchers.Main) {
                    (mediaController.bufferedPosition to mediaController.currentPosition)
                }
                if (buffered == 0L) progress.value = 0f
                else progress.value = (currentPosition.toFloat() / buffered).coerceIn(0f, 1f)

                durationStr.value = formatDuration(buffered)
                currentPositionStr.value = formatDuration(currentPosition)
                delay(100)
            }
        }
        mediaController.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                this@AppViewModel.isPlaying.value = isPlaying
            }

            override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                title.value = mediaMetadata.title?.toString() ?: ""
                artist.value = mediaMetadata.artist?.toString() ?: ""
                artworkUri.value = mediaMetadata.artworkUri
                artworkImage.value = mediaMetadata.artworkData
                album.value = mediaMetadata.albumTitle?.toString() ?: ""
                duration.value = currentSong?.duration ?: 0L
            }

            override fun onTimelineChanged(timeline: Timeline, reason: Int) {
                Log.d(TAG, "onTimelineChanged: $timeline")
            }
        })
    }

    fun showPlayScreen() {
        showPlayScreen.value = true
    }

    fun hidePlayScreen() {
        showPlayScreen.value = false
    }

    fun togglePlay() {
        viewModelScope.launch {
            if (mediaController.isPlaying) {
                mediaController.pause()
            } else {
                mediaController.play()
            }
        }
    }

    fun next() {
        mediaController.seekToNextMediaItem()
    }

    fun previous() {
        mediaController.seekToPreviousMediaItem()
    }

    private fun getMetadata(uri: Uri) {
        println(MediaItem.fromUri(uri).mediaMetadata.artworkUri)
    }

    private val executor = Executors.newSingleThreadExecutor()

    @androidx.media3.common.util.UnstableApi
    fun initMusics() {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading.value = true

            val songs = musicRepository.getAllMusics()
            musics.value = songs
            _musics = songs.toSet()

            songs.forEach {
                val mediaItem = it.toMediaItem()
                withContext(Dispatchers.Main) { mediaController.addMediaItem(mediaItem) }
                val future = MetadataRetriever.retrieveMetadata(context, mediaItem)
                Futures.addCallback(future, object : FutureCallback<TrackGroupArray> {
                    override fun onSuccess(result: TrackGroupArray) {
                        Log.d(TAG, "onSuccess: $result")
                        val metadata = mediaItem.mediaMetadata

                        Log.d(
                            TAG,
                            "MediaItem: ${it.title} ${metadata.title} ${metadata.artworkUri}"
                        )
                    }

                    override fun onFailure(t: Throwable) {

                    }
                }, executor)

            }
            withContext(Dispatchers.Main) {
                mediaController.prepare()
            }

            isLoading.value = false
        }
    }

    fun selectMusic(music: Music) {
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                mediaController.clearMediaItems()
                mediaController.addMediaItem(music.toMediaItem())
                mediaController.prepare()
                mediaController.play()
            }
        }
    }

    private fun Music.toMediaItem(): MediaItem {
        val uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
        return MediaItem.fromUri(uri)
    }
}