package com.project.tripplanner.features.tripdetails.mapbottomsheet

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.net.toUri


private const val GOOGLE_MAPS_PACKAGE = "com.google.android.apps.maps"
private const val GOOGLE_MAPS_SEARCH_URL = "https://www.google.com/maps/search/?api=1&query="

fun openMapsSearch(context: Context, query: String): Boolean {
    val uri = "$GOOGLE_MAPS_SEARCH_URL${Uri.encode(query)}".toUri()
    return try {
        context.startActivity(
            Intent(Intent.ACTION_VIEW, uri).setPackage(GOOGLE_MAPS_PACKAGE)
        )
        true
    } catch (e: ActivityNotFoundException) {
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW, uri))
            true
        } catch (_: ActivityNotFoundException) {
            false
        }
    }
}