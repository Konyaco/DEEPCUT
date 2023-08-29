package com.konyaco.deepcut.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.konyaco.deepcut.R

@Preview(showBackground = true)
@Composable
fun Header() {
    Row(
        Modifier
            .fillMaxWidth()
            .height(44.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier
                .padding(start = 18.dp)
                .size(24.dp),
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo"
        )
        Text(
            modifier = Modifier.padding(start = 12.dp, end = 16.dp),
            text = "深切",
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold
        )
        SearchBox(Modifier.weight(1f))
        IconButton(modifier = Modifier.padding(start = 4.dp), onClick = { /*TODO*/ }) {
            Icon(imageVector = Icons.Default.MoreVert, contentDescription = "More")
        }
    }
}

@Composable
fun SearchBox(modifier: Modifier = Modifier) {
    Box(
        modifier
            .height(40.dp)
            .background(Color.Black.copy(0.07f))
    ) {
        IconButton(modifier = Modifier
            .align(Alignment.CenterEnd)
            .padding(end = 8.dp), onClick = { /*TODO*/ }) {
            Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
        }
    }
}