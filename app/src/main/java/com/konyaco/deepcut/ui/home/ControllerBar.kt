package com.konyaco.deepcut.ui.home

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun ControllerBar(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    title: String,
    cover: ByteArray?,
    backgroundColor: Color,
    artist: String,
    progress: Float,
    isPlaying: Boolean,
    onPlayPauseClick: () -> Unit,
    onNextClick: () -> Unit
) {
    val contentColor = if (backgroundColor.luminance() > 0.5f) Color.Black else Color.White
    Surface(
        modifier
            .fillMaxWidth()
            .height(64.dp)
            .clickable(onClick = onClick),
        color = animateColorAsState(backgroundColor, tween(1000), label = "Background Color").value,
        contentColor = contentColor,
        shadowElevation = 2.dp
    ) {
        Box {
            // Progress grim
            Box(
                Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress)
                    .background(Color.Black.copy(0.15f))
            )

            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                // Album
                Crossfade(
                    modifier = Modifier
                        .padding(8.dp)
                        .size(48.dp),
                    targetState = cover, label = "Cover"
                ) {
                    if (it != null) AsyncImage(
                        modifier = Modifier.fillMaxSize(),
                        model = it,
                        contentDescription = "Cover",
                        contentScale = ContentScale.Crop
                    )
                    else Box(
                        Modifier
                            .fillMaxSize()
                            .background(Color(0xFF242424))
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        title, color = LocalContentColor.current.copy(0.87f),
                        style = MaterialTheme.typography.titleMedium,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                    Text(
                        artist, color = LocalContentColor.current.copy(0.6f),
                        style = MaterialTheme.typography.bodyMedium,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = onPlayPauseClick) {
                    if (isPlaying) Icon(
                        imageVector = Icons.Default.Pause,
                        contentDescription = "Pause"
                    )
                    else Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Play")
                }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = onNextClick) {
                    Icon(imageVector = Icons.Default.SkipNext, contentDescription = "Next")
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}

@Composable
@Preview
private fun Preview() {
    ControllerBar(
        onClick = {}, title = "Sacrifice", artist = "the Weeknd", progress = 0.6f,
        isPlaying = true,
        onPlayPauseClick = {},
        onNextClick = {},
        cover = null,
        backgroundColor = Color(0xFF5F88A6)
    )
}