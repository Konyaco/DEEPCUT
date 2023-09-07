package com.konyaco.deepcut.ui.home

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.konyaco.deepcut.R

@Composable
fun Header(float: Boolean) {
    val elevation = animateDpAsState(if (float) 2.dp else 0.dp, label = "Elevation")
    Surface(
        Modifier
            .fillMaxWidth(),
        tonalElevation = elevation.value
    ) {
        Row(
            Modifier
                .statusBarsPadding()
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 16.dp)
                .height(44.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier
                    .padding(start = 18.dp),
                painter = painterResource(id = R.drawable.ic_deepcut_32dp),
                contentDescription = "Logo"
            )

            Icon(
                modifier = Modifier
                    .padding(start = 6.dp, end = 16.dp),
                painter = rememberTitleResource(),
                contentDescription = "Logo"
            )

            SearchBox(Modifier.weight(1f))
            IconButton(modifier = Modifier.padding(start = 4.dp), onClick = { /*TODO*/ }) {
                Icon(imageVector = Icons.Default.MoreVert, contentDescription = "More")
            }
        }
    }
}

@Composable
fun SearchBox(modifier: Modifier = Modifier) {
    Box(
        modifier
            .height(48.dp)
            .background(MaterialTheme.colorScheme.onSurface.copy(0.07f))
    ) {
        IconButton(modifier = Modifier
            .align(Alignment.CenterEnd)
            .padding(end = 0.dp), onClick = { /*TODO*/ }) {
            Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
        }
    }
}

@Composable
private fun rememberTitleResource(): Painter {
    val locale = Locale.current
    val titleResourceId = remember(locale) {
        when (locale.toLanguageTag()) {
            "zh-Hans-CN" -> R.drawable.ic_deepcut_title_zh_cn_32dp
            "zh-Hant-TW", "zh-Hant-HK", "zh-Hant-MO" -> R.drawable.ic_deepcut_title_zh_tw_hk_32dp
            "en-US" -> R.drawable.ic_deepcut_title_en_32dp
            else -> R.drawable.ic_deepcut_title_en_32dp
        }
    }
    return painterResource(titleResourceId)

}

@Composable
@Preview(showBackground = true)
private fun Preview() {
    Header(float = true)
}