package com.konyaco.deepcut.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.core.graphics.scale
import com.google.android.material.color.DynamicColorsOptions
import kotlin.system.measureTimeMillis

private const val TAG = "ColorUtil"

object ColorUtil {
    fun calcThemeColor(bitmap: ByteArray): Int {
        val color: Int
        val time = measureTimeMillis {
            var bitmap = BitmapFactory.decodeByteArray(bitmap, 0, bitmap.size)
            bitmap = bitmap.scale(128, 128)
            val options = DynamicColorsOptions.Builder()
                .setContentBasedSource(bitmap)
                .build()
            color = options.contentBasedSeedColor ?: 0
        }
        Log.d(TAG, "calc theme color spends ${time}ms")
        return color
    }

    fun calcThemeColor(bitmap: Bitmap): Int {
        val color: Int
        val time = measureTimeMillis {
            val options = DynamicColorsOptions.Builder()
                .setContentBasedSource(bitmap.scale(128, 128))
                .build()
            color = options.contentBasedSeedColor ?: 0
        }
        Log.d(TAG, "calc theme color spends ${time}ms")
        return color
    }
}