package com.konyaco.deepcut.repository.model

data class Music(
    val id: Long,
    val title: String,
    val trackNumber: Int,
    val year: Int,
    val duration: Long,
    val data: String,
    val dateModified: Long,
    val albumId: Long,
    val albumName: String,
    val artistId: Long,
    val artistName: String,
    val composer: String?,
    val albumArtist: String?
)