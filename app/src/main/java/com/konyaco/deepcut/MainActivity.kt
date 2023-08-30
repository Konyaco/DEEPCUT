package com.konyaco.deepcut

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_AUDIO
import android.content.ComponentName
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors
import com.konyaco.deepcut.service.PlaybackService
import com.konyaco.deepcut.ui.App
import com.konyaco.deepcut.viewmodel.AppViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var viewModel: AppViewModel

    private lateinit var controller: MediaController

    override fun onStart() {
        super.onStart()
        val sessionToken = SessionToken(this, ComponentName(this, PlaybackService::class.java))
        val controllerFuture = MediaController.Builder(this, sessionToken).buildAsync()
        controllerFuture.addListener(
            {
                // Call controllerFuture.get() to retrieve the MediaController.
                // MediaController implements the Player interface, so it can be
                // attached to the PlayerView UI component.
                controller = controllerFuture.get()
                viewModel.setMediaController(controller)
                viewModel.initMusics()
            },
            MoreExecutors.directExecutor()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        requestPermission()
        setContent {
            App(viewModel)
        }
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    READ_MEDIA_AUDIO
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(this, arrayOf(READ_MEDIA_AUDIO), 0)
            }
        } else {
            if (ContextCompat.checkSelfPermission(
                    this,
                    READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(this, arrayOf(READ_EXTERNAL_STORAGE), 0)
            }
        }
    }
}