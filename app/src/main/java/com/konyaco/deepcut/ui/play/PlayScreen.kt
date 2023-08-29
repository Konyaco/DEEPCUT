package com.konyaco.deepcut.ui.play

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.konyaco.deepcut.viewmodel.AppViewModel
import kotlin.random.Random

val backgroundColor = Color(0xFF5F88A6)

//    val contentColor = if (backgroundColor.luminance() > 0.5f) Color.Black else Color.White
val contentColor = Color.Black

@Preview(showBackground = true)
@Composable
fun PlayScreen(viewModel: AppViewModel = AppViewModel()) {
    Surface(Modifier.fillMaxSize(), color = backgroundColor, contentColor = contentColor) {
        Column {
            Row(
                modifier = Modifier.padding(start = 24.dp, end = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.alignByBaseline(),
                        text = "来自专辑",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        modifier = Modifier.alignByBaseline(),
                        text = "DAWN FM",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black
                    )
                }
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(imageVector = Icons.Default.MoreVert, contentDescription = "More")
                }
                IconButton(onClick = { viewModel.hidePlayScreen() }) {
                    Icon(imageVector = Icons.Default.ExpandMore, contentDescription = "Collapse")
                }
            }

            Column(Modifier.padding(horizontal = 24.dp)) {
                Box(
                    modifier = Modifier
                        .padding(vertical = 24.dp)
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .background(Color(0xFF3C3C3C))
                )
                Text(
                    "Sacrifice",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Black,
                    color = LocalContentColor.current.copy(0.87f)
                )
                Text(
                    "The Weeknd",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = LocalContentColor.current.copy(0.72f)
                )
            }

            ProgressBar(
                Modifier
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .fillMaxWidth()
                    .clip(RectangleShape)
            )
            Controllers(
                Modifier
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .fillMaxWidth()
            )
        }
    }
}

@Composable
fun ProgressBar(modifier: Modifier = Modifier) {
    val volumes = remember { generateVolumes() }
    val backgroundColor = Color(0xFF5F88A6)
    val contentColor = Color.Black.copy(0.87f)
    val progress = 0.5f
    Column(modifier) {
        Canvas(
            modifier = Modifier
                .height(48.dp)
                .fillMaxWidth()
        ) {
            drawVolume(contentColor, volumes)
            drawProgress(contentColor, progress)
            clipRect(
                right = size.width * progress
            ) {
                drawVolume(backgroundColor, volumes)
            }
        }
        Row {
            Text(
                text = "1:13",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = LocalContentColor.current.copy(0.87f)
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = "-1:13",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = LocalContentColor.current.copy(0.87f)
            )
        }
    }
}

fun generateVolumes(): List<Float> {
    return buildList {
        repeat(90) {
            add(Random.nextFloat())
        }
    }
}

fun DrawScope.drawVolume(color: Color, volumes: List<Float>) {
    val lineWidth = 2.dp.toPx()
    val gapWidth = 2.dp.toPx()
    val paddingWidth = 3.dp.toPx()
    val paddingHeight = 4.dp.toPx()

    val maxHeight = size.height - paddingHeight * 2

    volumes.forEachIndexed { i, volume ->
        val start = paddingWidth + i * (lineWidth + gapWidth)
        val height = maxHeight * volume
        val x = start + lineWidth / 2f
        val yStart = (size.height - height) / 2f
        val yEnd = yStart + height

        drawLine(
            color = color,
            start = Offset(x, yStart),
            end = Offset(x, yEnd),
            strokeWidth = lineWidth,
            cap = StrokeCap.Round
        )
    }
}

fun DrawScope.drawProgress(color: Color, progress: Float) {
    drawRect(color, size = Size(size.width * progress, size.height))
}

@Composable
fun Controllers(modifier: Modifier) {
    Column {
        Row(
            modifier = modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(imageVector = Icons.Default.Repeat, "Repeat")
            }
            IconButton(onClick = { /*TODO*/ }) {
                Icon(imageVector = Icons.Default.Shuffle, "Shuffle")
            }
            PauseButton()
            IconButton(onClick = { /*TODO*/ }) {
                Icon(imageVector = Icons.Outlined.Timer, "Timer")
            }
            IconButton(onClick = { /*TODO*/ }) {
                Icon(imageVector = Icons.Default.List, "List")
            }
        }
        Row (
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ){
            // Lyric
            PreviousButton()
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "‘Cause life is still worth living", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                Text(text = "因为人生仍然值得", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
            NextButton()
        }
    }
}

@Composable
fun PauseButton() {
    Box(
        Modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(LocalContentColor.current),
        Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Pause,
            contentDescription = "Pause",
            tint = backgroundColor
        )
    }
}

@Composable
fun PreviousButton() {
    Box(
        Modifier
            .size(64.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.onSurface.copy(0.07f)),
        Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.SkipPrevious,
            contentDescription = "SkipPrevious"
        )
    }
}

@Composable
fun NextButton() {
    Box(
        Modifier
            .size(64.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.onSurface.copy(0.07f)),
        Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.SkipNext,
            contentDescription = "SkipNext"
        )
    }
}