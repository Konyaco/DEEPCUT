package com.konyaco.deepcut.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppViewModel @Inject constructor() : ViewModel() {
    val showPlayScreen = mutableStateOf(false)

    fun showPlayScreen() {
        showPlayScreen.value = true
    }

    fun hidePlayScreen() {
        showPlayScreen.value = false
    }
}