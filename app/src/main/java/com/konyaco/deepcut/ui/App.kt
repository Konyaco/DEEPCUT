package com.konyaco.deepcut.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.konyaco.deepcut.ui.home.HomeScreen
import com.konyaco.deepcut.ui.play.PlayScreen
import com.konyaco.deepcut.ui.theme.DEEPCUTTheme
import com.konyaco.deepcut.viewmodel.AppViewModel

@Composable
fun App(viewModel: AppViewModel) {
    DEEPCUTTheme {
        // A surface container using the 'background' color from the theme
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Box(
                modifier = Modifier.fillMaxSize(),
            ) {
                HomeScreen(viewModel)
                AnimatedVisibility(
                    modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter),
                    visible = viewModel.showPlayScreen.value,
                    enter = expandVertically(expandFrom = Alignment.Top),
                    exit = shrinkVertically(shrinkTowards = Alignment.Top)
                ) {
                    PlayScreen(viewModel)
                }
            }
        }
    }
}