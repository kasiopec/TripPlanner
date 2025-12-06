package com.project.tripplanner.cover

import android.content.Context
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import java.io.ByteArrayInputStream
import java.io.File
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowContentResolver

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class TripCoverImageStorageImplTest {

    private lateinit var context: Context
    private lateinit var storage: TripCoverImageStorageImpl
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        storage = TripCoverImageStorageImpl(context, testDispatcher)
    }

    @After
    fun tearDown() {
        File(context.filesDir, "cover_images").deleteRecursively()
    }

    @Test
    fun importFromPicker_copiesFileToAppStorage() = runTest(testDispatcher.scheduler) {
        val imageBytes = "image-data".toByteArray()
        val sourceUri = Uri.parse("content://test/image.png")
        ShadowContentResolver.registerInputStream(sourceUri, ByteArrayInputStream(imageBytes))

        val storedPath = storage.importFromPicker(sourceUri)

        assertTrue(storedPath.startsWith("cover_images/"))
        val storedFile = File(context.filesDir, storedPath)
        assertTrue(storedFile.exists())
        assertArrayEquals(imageBytes, storedFile.readBytes())
    }

    @Test
    fun resolveForDisplay_returnsUriOnlyWhenFileExists() = runTest(testDispatcher.scheduler) {
        val sourceUri = Uri.parse("content://test/image.jpg")
        ShadowContentResolver.registerInputStream(sourceUri, ByteArrayInputStream(byteArrayOf(1, 2, 3)))

        val storedPath = storage.importFromPicker(sourceUri)

        val resolvedUri = storage.resolveForDisplay(storedPath)
        val missingUri = storage.resolveForDisplay("cover_images/missing.jpg")

        assertEquals(Uri.fromFile(File(context.filesDir, storedPath)), resolvedUri)
        assertNull(missingUri)
    }

    @Test
    fun delete_removesStoredFile() = runTest(testDispatcher.scheduler) {
        val sourceUri = Uri.parse("content://test/image-to-delete.jpg")
        ShadowContentResolver.registerInputStream(sourceUri, ByteArrayInputStream(byteArrayOf(5, 6, 7)))

        val storedPath = storage.importFromPicker(sourceUri)
        val storedFile = File(context.filesDir, storedPath)
        assertTrue(storedFile.exists())

        storage.delete(storedPath)

        assertFalse(storedFile.exists())
        storage.delete("cover_images/non-existent.jpg")
    }
}
