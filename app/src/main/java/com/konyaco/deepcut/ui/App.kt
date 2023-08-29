package com.konyaco.deepcut.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import com.konyaco.deepcut.ui.home.HomeScreen
import com.konyaco.deepcut.ui.theme.DEEPCUTTheme

@Composable
fun App(viewModel: ViewModel) {
    DEEPCUTTheme {
        // A surface container using the 'background' color from the theme
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            HomeScreen()
        }
    }
}