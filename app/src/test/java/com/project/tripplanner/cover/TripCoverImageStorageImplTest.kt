package com.project.tripplanner.cover

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
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

@OptIn(ExperimentalCoroutinesApi::class)
class TripCoverImageStorageImplTest {

    private lateinit var context: Context
    private lateinit var resolver: ContentResolver
    private lateinit var storage: TripCoverImageStorageImpl
    private lateinit var tempDir: File
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        mockkStatic(Uri::class)
        mockkStatic(MimeTypeMap::class)
        context = mockk()
        resolver = mockk()
        tempDir = createTempDir()
        every { context.contentResolver } returns resolver
        every { context.filesDir } returns tempDir
        every { MimeTypeMap.getSingleton().getExtensionFromMimeType(any()) } answers { (it.invocation.args[0] as String?)?.substringAfter('/') }
        storage = TripCoverImageStorageImpl(context, testDispatcher)
    }

    @After
    fun tearDown() {
        tempDir.deleteRecursively()
        unmockkAll()
        clearAllMocks()
    }

    @Test
    fun importFromPicker_copiesFileToAppStorage() = runTest(testDispatcher.scheduler) {
        val imageBytes = "image-data".toByteArray()
        val sourceUri = mockk<Uri>()
        every { resolver.getType(sourceUri) } returns "image/png"
        every { resolver.openInputStream(sourceUri) } returns ByteArrayInputStream(imageBytes)

        val storedPath = storage.importFromPicker(sourceUri)

        assertTrue(storedPath.startsWith("cover_images/"))
        val storedFile = File(tempDir, storedPath)
        assertTrue(storedFile.exists())
        assertArrayEquals(imageBytes, storedFile.readBytes())
    }

    @Test
    fun resolveForDisplay_returnsUriOnlyWhenFileExists() = runTest(testDispatcher.scheduler) {
        val sourceUri = mockk<Uri>()
        every { resolver.getType(sourceUri) } returns "image/jpeg"
        every { resolver.openInputStream(sourceUri) } returns ByteArrayInputStream(byteArrayOf(1, 2, 3))

        val storedPath = storage.importFromPicker(sourceUri)
        val storedFile = File(tempDir, storedPath)
        val expectedUri = mockk<Uri>()
        every { Uri.fromFile(storedFile) } returns expectedUri

        val resolvedUri = storage.resolveForDisplay(storedPath)
        val missingUri = storage.resolveForDisplay("cover_images/missing.jpg")

        assertEquals(expectedUri, resolvedUri)
        assertNull(missingUri)
    }

    @Test
    fun delete_removesStoredFile() = runTest(testDispatcher.scheduler) {
        val sourceUri = mockk<Uri>()
        every { resolver.getType(sourceUri) } returns "image/jpeg"
        every { resolver.openInputStream(sourceUri) } returns ByteArrayInputStream(byteArrayOf(5, 6, 7))

        val storedPath = storage.importFromPicker(sourceUri)
        val storedFile = File(tempDir, storedPath)
        assertTrue(storedFile.exists())

        storage.delete(storedPath)

        assertFalse(storedFile.exists())
        storage.delete("cover_images/non-existent.jpg")
    }
}
