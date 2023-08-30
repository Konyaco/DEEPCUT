package com.konyaco.deepcut.viewmodel

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
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
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.exoplayer.MetadataRetriever
import androidx.media3.exoplayer.source.TrackGroupArray
import androidx.media3.session.MediaController
import com.google.common.util.concurrent.FutureCallback
import com.google.common.util.concurrent.Futures
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "AppViewModel"

@Singleton
class AppViewModel @Inject constructor(
    @ApplicationContext
    private val context: Context
) : ViewModel() {
    val showPlayScreen = mutableStateOf(false)
    val isPlaying = mutableStateOf(false)

    val title = mutableStateOf("")
    val artist = mutableStateOf("")
    val progress = mutableFloatStateOf(0f)
    val artworkUri = mutableStateOf<Uri?>(null)
    val artworkImage = mutableStateOf<ByteArray?>(null)
    val duration = mutableStateOf(0)

    private lateinit var mediaController: MediaController

    fun setMediaController(mediaController: MediaController) {
        this.mediaController = mediaController
        mediaController.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                this@AppViewModel.isPlaying.value = isPlaying
            }

            override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                title.value = mediaMetadata.title?.toString() ?: "Unknown"
                artist.value = mediaMetadata.artist?.toString() ?: "Unknown"
                artworkUri.value = mediaMetadata.artworkUri
                artworkImage.value = mediaMetadata.artworkData
                Log.d(TAG, "uri: ${mediaMetadata.artworkUri}")
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

    data class Music(
        val id: Long,
        val uri: Uri,
        val title: String
    )

    private suspend fun loadMusics() = withContext(Dispatchers.IO) {
        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Audio.Media.getContentUri(
                MediaStore.VOLUME_EXTERNAL_PRIMARY
            )
        } else {
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        }
        val selection =
            "${MediaStore.Audio.Media.IS_MUSIC} != 0 and ${MediaStore.Audio.Media.DURATION} > 30"
        val result = context.contentResolver.query(
            collection,
            null,
            selection,
            null,
            null
        )
        val songList = mutableListOf<Music>()

        result?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val contentUri: Uri =
                    ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
                songList.add(
                    Music(
                        id,
                        contentUri,
                        cursor.getString(titleColumn)
                    )
                )
            }
        }
        songList
    }

    private fun getMetadata(uri: Uri) {
        println(MediaItem.fromUri(uri).mediaMetadata.artworkUri)
    }

    private val executor = Executors.newSingleThreadExecutor()

    @androidx.media3.common.util.UnstableApi
    fun initMusics() {
        viewModelScope.launch(Dispatchers.IO) {
            val songs = loadMusics()
            songs.forEach {
                val mediaItem = MediaItem.fromUri(it.uri)
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
        }
    }
}