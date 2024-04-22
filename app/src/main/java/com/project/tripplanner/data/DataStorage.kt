package com.project.tripplanner.data

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext

interface DataStorage {
    fun contains(key: String): Boolean
    fun <T : Any> put(key: String, data: T)
    fun getBoolean(key: String): Boolean?
    fun getFloat(key: String): Float?
    fun getInt(key: String): Int?
    fun getLong(key: String): Long?
    fun getString(key: String): String?
    fun remove(key: String)
    fun clear()
}

abstract class DataStorageImpl(@ApplicationContext context: Context, storageName: String) : DataStorage {
    private val storage = context.getSharedPreferences(storageName, 0)

    override fun contains(key: String) = storage.contains(key)

    override fun <T : Any> put(key: String, data: T) {
        with(storage.edit()) {
            when (data) {
                is Boolean -> putBoolean(key, data)
                is Float -> putFloat(key, data)
                is Int -> putInt(key, data)
                is Long -> putLong(key, data)
                is String -> putString(key, data)
                else -> throw IllegalArgumentException("Unsupported type")
            }
            apply()
        }
    }

    override fun getBoolean(key: String): Boolean? = getString(key)?.toBoolean()

    override fun getFloat(key: String): Float? = getString(key)?.toFloat()

    override fun getInt(key: String): Int? = getString(key)?.toInt()

    override fun getLong(key: String): Long? = getString(key)?.toLong()

    override fun getString(key: String) = storage.getString(key, null)

    override fun remove(key: String) {
        storage.edit().remove(key).apply()
    }

    override fun clear() {
        storage.edit().clear().apply()
    }
}