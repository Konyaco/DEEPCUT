package com.konyaco.deepcut.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ControllerBar(modifier: Modifier = Modifier, onClick: () -> Unit) {
    val backgroundColor = Color(0xFF476187)
    val contentColor = if (backgroundColor.luminance() > 0.5f) {
        Color.Black
    } else {
        Color.White
    }
    Surface(
        modifier
            .fillMaxWidth()
            .height(64.dp)
            .clickable(onClick = onClick),
        color = backgroundColor,
        contentColor = contentColor,
        shadowElevation = 2.dp
    ) {
        Box {
            // Progress grim
            Box(
                Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.6f)
                    .background(Color.Black.copy(0.15f))
            )

            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                // Album
                Box(
                    Modifier
                        .padding(8.dp)
                        .size(48.dp)
                        .background(Color(0xFF242424))
                )

                Spacer(modifier = Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        "Sacrifice", color = LocalContentColor.current.copy(0.87f),
                        style = MaterialTheme.typography.titleMedium,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        "The weeknd", color = LocalContentColor.current.copy(0.6f),
                        style = MaterialTheme.typography.bodyMedium,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))
                Icon(imageVector = Icons.Default.Pause, contentDescription = "Pause")
                Spacer(modifier = Modifier.width(16.dp))
                Icon(imageVector = Icons.Default.SkipNext, contentDescription = "Pause")
                Spacer(modifier = Modifier.width(16.dp))
            }
        }
    }
}

@Composable
@Preview
private fun Preview() {
    ControllerBar {}
}