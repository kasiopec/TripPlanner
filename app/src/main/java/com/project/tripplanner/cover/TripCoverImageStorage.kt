package com.project.tripplanner.cover

import android.net.Uri

interface TripCoverImageStorage {
    /**
     * Copies the selected picker image into app-private storage and returns a neutral relative path.
     */
    suspend fun importFromPicker(sourceUri: Uri): String

    /**
     * Resolves the stored relative path to a displayable Uri if the file exists; returns null otherwise.
     */
    suspend fun resolveForDisplay(path: String?): Uri?

    /**
     * Deletes the stored file for the provided relative path, if present.
     */
    suspend fun delete(path: String?)
}
