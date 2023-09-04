package com.konyaco.deepcut.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.konyaco.deepcut.repository.model.Music
import com.konyaco.deepcut.viewmodel.AppViewModel

@Composable
fun HomeScreen(viewModel: AppViewModel) {
    val bottomPadding = if (
        WindowInsets.systemBars.asPaddingValues().calculateBottomPadding() == 0.dp
        ) {
        16.dp
    } else {
        WindowInsets.systemBars.asPaddingValues().calculateBottomPadding()
    }
    Box(Modifier.fillMaxSize()) {
        Column(
            Modifier
                .fillMaxSize()
        ) {
            Header()
            Column(
                Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxSize()
                    .navigationBarsPadding()
                    .padding(bottom = 60.dp)
            ) {
                //Spacer(Modifier.height(8.dp))
                Content(viewModel)
            }
        }
        Box(
            Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp, vertical = bottomPadding)
        ) {
            ControllerBar(
                onClick = { viewModel.showPlayScreen() },
                title = viewModel.title.value,
                artist = viewModel.artist.value,
                progress = viewModel.progress.value,
                isPlaying = viewModel.isPlaying.value,
                onPlayPauseClick = { viewModel.togglePlay() },
                onNextClick = { viewModel.next() },
                cover = viewModel.artworkImage.value,
                backgroundColor = viewModel.backgroundColor.value?.let {Color(it)} ?: Color(0xFF5F88A6)
            )
        }
    }
}

@Composable
fun Content(viewModel: AppViewModel) {
    Column(Modifier.fillMaxWidth()) {
        RecentlyListen()
        Spacer(Modifier.height(16.dp))
        AllSongs(viewModel.musics.value, onSelect = {
            viewModel.selectMusic(it)
        })
    }
}

@Composable
fun RecentlyListen() {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = "最近常听",
                style = MaterialTheme.typography.headlineSmall
            )
            FilledIconButton(
                onClick = { /*TODO*/ },
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.onSurface.copy(0.07f),
                    contentColor = LocalContentColor.current
                )
            ) {
                Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "More")
            }
        }
        // height = 8 (padding) * 2 + 16 (space) + 56 * 3 =
        LazyHorizontalGrid(
            modifier = Modifier.height(200.dp),
            rows = GridCells.Fixed(3),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            repeat(30) {
                item {
                    RowSongItem()
                }
            }
        }
    }
}

@Composable
fun RowSongItem() {
    Row(
        modifier = Modifier
            .size(168.dp, 56.dp)
            .clickable {
                // TODO:
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(40.dp)
                .background(Color(0XFF6D5B44))
        )
        Column(
            Modifier
                .padding(horizontal = 10.dp)
                .fillMaxWidth()
        ) {
            Text(
                "Lorem Ipsum", color = LocalContentColor.current.copy(0.87f),
                style = MaterialTheme.typography.titleMedium,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                "Lorem Ipsum", color = LocalContentColor.current.copy(0.6f),
                style = MaterialTheme.typography.bodyMedium,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun AllSongs(music: List<Music>, onSelect: (Music) -> Unit) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = "全部歌曲",
                style = MaterialTheme.typography.headlineSmall
            )
            FilterOption()
        }
        // height = 8 (padding) * 2 + 16 (space) + 56 * 3 =
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            music.forEachIndexed { index, music ->
                ColumnSongItem(music.title, music.artistName, onSelect = {
                    onSelect(music)
                })
            }
        }
    }
}

@Composable
fun FilterOption() {
    Row(
        Modifier
            .background(MaterialTheme.colorScheme.onSurface.copy(0.07f))
            .clickable { }
            .padding(16.dp, 10.dp, 12.dp, 10.dp)
    ) {
        Text("按名称")
        Spacer(Modifier.width(8.dp))
        Icon(imageVector = Icons.Default.Sort, contentDescription = "Filter")
    }
}

@Composable
fun ColumnSongItem(title: String, artist: String, onSelect: () -> Unit) {
    Row(
        modifier = Modifier
            .clickable(onClick = onSelect)
            .padding(start = 16.dp, end = 8.dp, top = 10.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(40.dp)
                .background(Color(0XFF6D5B44))
        ) {
            /*cover?.let {
                AsyncImage(model = it, contentDescription = "Cover")
            }*/
        }
        Column(
            Modifier
                .padding(horizontal = 10.dp)
                .weight(1f)
        ) {
            Text(
                title, color = LocalContentColor.current.copy(0.87f),
                style = MaterialTheme.typography.titleMedium,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                artist, color = LocalContentColor.current.copy(0.6f),
                style = MaterialTheme.typography.bodyMedium,
                overflow = TextOverflow.Ellipsis
            )
        }
        IconButton(onClick = { /*TODO*/ }) {
            Icon(imageVector = Icons.Default.MoreVert, contentDescription = "More")
        }
    }
}