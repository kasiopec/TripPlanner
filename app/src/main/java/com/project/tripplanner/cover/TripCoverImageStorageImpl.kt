package com.project.tripplanner.cover

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.util.UUID
import javax.inject.Inject
import kotlin.text.Regex
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TripCoverImageStorageImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : TripCoverImageStorage {

    override suspend fun importFromPicker(sourceUri: Uri): String = withContext(ioDispatcher) {
        val resolver = context.contentResolver
        val inputStream = resolver.openInputStream(sourceUri)
            ?: throw IllegalArgumentException("Unable to open input stream for $sourceUri")

        val coverDirectory = File(context.filesDir, COVER_DIRECTORY_NAME).apply { mkdirs() }
        val extension = resolver.getType(sourceUri)?.let(::mimeTypeToExtension)
        val fileName = buildString {
            append(UUID.randomUUID().toString())
            if (!extension.isNullOrBlank()) {
                append(".")
                append(extension)
            }
        }
        val destinationFile = File(coverDirectory, fileName)

        inputStream.use { input ->
            destinationFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        return@withContext "${COVER_DIRECTORY_NAME}/${destinationFile.name}"
    }

    override suspend fun resolveForDisplay(path: String?): Uri? = withContext(ioDispatcher) {
        if (path.isNullOrBlank()) return@withContext null

        val file = resolveFile(path)
        if (file.exists()) Uri.fromFile(file) else null
    }

    override suspend fun delete(path: String?) = withContext(ioDispatcher) {
        if (path.isNullOrBlank()) return@withContext

        val file = resolveFile(path)
        if (file.exists()) {
            file.delete()
        }
    }

    private fun resolveFile(path: String): File {
        val sanitizedPath = path
            .replace("\\", "/")
            .trimStart('/')
        val safePath = sanitizedPath
            .split("/")
            .filter { it.isNotEmpty() && it != ".." }
            .joinToString("/")
        return File(context.filesDir, safePath)
    }

    private fun mimeTypeToExtension(mimeType: String): String? {
        val normalizedMime = mimeType.lowercase()
        val extensionFromMime = MimeTypeMap.getSingleton().getExtensionFromMimeType(normalizedMime)
        if (!extensionFromMime.isNullOrBlank()) {
            return extensionFromMime
        }

        val suffix = normalizedMime.substringAfterLast('/', missingDelimiterValue = "")
        return suffix.takeIf { it.matches(EXTENSION_PATTERN) }
    }

    private companion object {
        const val COVER_DIRECTORY_NAME = "cover_images"
        val EXTENSION_PATTERN = Regex("^[a-z0-9]+$")
    }
}
