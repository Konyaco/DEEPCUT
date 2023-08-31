package com.konyaco.deepcut.repository

import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.test.runTest
import org.junit.Test

class MusicRepositoryTest {

    @Test
    fun testGetAllMusic() = runTest {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        println(MusicRepository(context).getAllMusics())
    }
}