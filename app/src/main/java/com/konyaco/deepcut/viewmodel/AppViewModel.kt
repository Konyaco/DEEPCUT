package com.konyaco.deepcut.viewmodel

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Size
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import com.konyaco.deepcut.repository.MusicRepository
import com.konyaco.deepcut.repository.model.Music
import com.konyaco.deepcut.util.ColorUtil
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.concurrent.thread
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes

private const val TAG = "AppViewModel"

@Singleton
class AppViewModel @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val musicRepository: MusicRepository
) : ViewModel() {
    val backgroundColor = mutableStateOf<Int?>(null)
    val showPlayScreen = mutableStateOf(false)
    val isPlaying = mutableStateOf(false)

    val isLoading = mutableStateOf(true)

    val musics = mutableStateOf(emptyList<MusicItem>())

    val title = mutableStateOf("")
    val artist = mutableStateOf("")
    val album = mutableStateOf("")
    val progress = mutableStateOf(0f)
    val artworkUri = mutableStateOf<Uri?>(null)
    val artworkImage = mutableStateOf<ByteArray?>(null)
    val duration = mutableStateOf(0L)
    val currentPositionStr = mutableStateOf("0:00")
    val durationStr = mutableStateOf("0:00")

    private lateinit var mediaController: MediaController
    private lateinit var updateProgressJob: Job

    private var currentMusicItem: MusicItem? = null

    private val _musics = HashMap<MusicItem, MediaItem>()
    private val _mediaItemMusics = HashMap<MediaItem, MusicItem>()

    private fun formatDuration(durationMs: Long): String {
        if (durationMs == 0L) return "0:00"
        val duration = durationMs.milliseconds
        val m = duration.inWholeMinutes
        val s = (duration - m.minutes).inWholeSeconds
        return "${m}:${"%02d".format(s)}"
    }

    data class MusicItem(
        val music: Music,
        var artworkImage: MutableState<ImageBitmap?>,
        var themeColor: MutableState<Color?>
    )

    fun setMediaController(mediaController: MediaController) {
        this.mediaController = mediaController
        updateProgressJob = viewModelScope.launch(Dispatchers.Default) {
            while (isActive) {
                val (buffered, currentPosition) = withContext(Dispatchers.Main) {
                    (mediaController.bufferedPosition to mediaController.currentPosition)
                }
                val duration = currentMusicItem?.music?.duration ?: 0L
                if (duration == 0L) progress.value = 0f
                else progress.value = (currentPosition.toFloat() / duration).coerceIn(0f, 1f)

                durationStr.value = formatDuration(duration)
                currentPositionStr.value = formatDuration(currentPosition)
                delay(100)
            }
        }
        mediaController.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                this@AppViewModel.isPlaying.value = isPlaying
            }

            override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                this@AppViewModel.currentMusicItem =
                    _mediaItemMusics[mediaController.currentMediaItem]
                title.value = mediaMetadata.title?.toString() ?: "Loading"
                artist.value = mediaMetadata.artist?.toString() ?: "Loading"
                artworkUri.value = mediaMetadata.artworkUri
                artworkImage.value = mediaMetadata.artworkData
                album.value = mediaMetadata.albumTitle?.toString() ?: "Loading"
                duration.value = currentMusicItem?.music?.duration ?: 0L

                calcThemeColor(mediaMetadata.artworkData)
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

    fun initMusics() {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading.value = true

            val songs = musicRepository.getAllMusics()
            val musics = mutableListOf<MusicItem>()
            songs.forEach {
                val contentUri: Uri =
                    ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, it.id)
                val mediaItem = it.toMediaItem()
                val musicItem = MusicItem(it, mutableStateOf(null), mutableStateOf(null))
                launch(Dispatchers.Default) {
                    val bitmap =
                        context.contentResolver.loadThumbnail(contentUri, Size(128, 128), null)
                    val color = ColorUtil.calcThemeColor(bitmap)
                    musicItem.themeColor.value = Color(color)
                    musicItem.artworkImage.value = bitmap.asImageBitmap()
                }
                musics.add(musicItem)
                this@AppViewModel._musics[musicItem] = mediaItem
                this@AppViewModel._mediaItemMusics[mediaItem] = musicItem
//
                withContext(Dispatchers.Main) { mediaController.addMediaItem(mediaItem) }
            }

            this@AppViewModel.musics.value = musics
            isLoading.value = false
            withContext(Dispatchers.Main) { mediaController.prepare() }
        }
    }

    fun selectMusic(music: MusicItem) {
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                mediaController.clearMediaItems()
                mediaController.addMediaItem(_musics[music]!!)
                mediaController.prepare()
                mediaController.play()
            }
        }
    }

    private fun Music.toMediaItem(): MediaItem {
        val uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
        return MediaItem.fromUri(uri)
    }

    @Volatile
    private var calcThread: Thread? = null
        @Synchronized get
        @Synchronized set

    private fun calcThemeColor(bitmap: ByteArray?) {
        if (bitmap != null) {
            calcThread?.interrupt()
            calcThread = thread {
                backgroundColor.value = ColorUtil.calcThemeColor(bitmap)
            }
        }
    }
}