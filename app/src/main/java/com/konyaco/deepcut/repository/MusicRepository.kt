package com.konyaco.deepcut.repository

import android.content.Context
import android.database.Cursor
import android.os.Build
import android.provider.MediaStore
import androidx.core.database.getStringOrNull
import com.konyaco.deepcut.repository.model.Music
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicRepository @Inject constructor(
    @ApplicationContext
    private val context: Context
) {
    private val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        MediaStore.Audio.Media.getContentUri(
            MediaStore.VOLUME_EXTERNAL_PRIMARY
        )
    } else {
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    }

    suspend fun getById(id: Int): Music? {
        val selection = "${MediaStore.Audio.Media._ID} = ?"
        val result =
            context.contentResolver.query(collection, null, selection, arrayOf(id.toString()), null)

        return result?.use {
            if (it.moveToNext()) it.mapToMusic()
            else null
        }
    }

    suspend fun search(text: String): List<Music> {
        val selection = "${MediaStore.Audio.Media.TITLE} like ?"
        val result =
            context.contentResolver.query(collection, null, selection, arrayOf("%${text}%"), null)

        val musicList = mutableListOf<Music>()

        result?.use {
            while (it.moveToNext()) {
                musicList.add(it.mapToMusic())
            }
        }
        return musicList
    }

    suspend fun getAllMusics(): List<Music> {
        val selection =
            "${MediaStore.Audio.Media.IS_MUSIC} != 0 and ${MediaStore.Audio.Media.DURATION} > 30000"
        val result = context.contentResolver.query(collection, null, selection, null, null)

        val musicList = mutableListOf<Music>()
        result?.use { cursor ->
            /*val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
*/
            while (cursor.moveToNext()) {
                /*val id = cursor.getLong(idColumn)
                val contentUri: Uri =
                    ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)*/
                musicList.add(cursor.mapToMusic())
            }
        }
        return musicList
    }

    private fun Cursor.mapToMusic() = Music(
        id = getLong(getColumnIndexOrThrow(MediaStore.Audio.AudioColumns._ID)),
        title = getString(getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TITLE)),
        trackNumber = getInt(getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TRACK)),
        year = getInt(getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.YEAR)),
        duration = getLong(getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DURATION)),
        data = getString(getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)),
        dateModified = getLong(getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DATE_MODIFIED)),
        albumId = getLong(getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ALBUM_ID)),
        albumName = getStringOrNull(getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ALBUM))
            ?: "",
        artistId = getLong(getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ARTIST_ID)),
        artistName = getStringOrNull(getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ARTIST))
            ?: "",
        composer = getStringOrNull(getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.COMPOSER))
            ?: "",
        albumArtist = getStringOrNull(getColumnIndexOrThrow("album_artist")) ?: ""
    )
}