package com.project.tripplanner.data

import android.content.Context

private const val USER_SHARED_PREFS = "user_shared_prefs"

class UserPrefsStorage(context: Context) : DataStorageImpl(context, USER_SHARED_PREFS)