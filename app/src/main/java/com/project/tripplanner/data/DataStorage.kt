package com.project.tripplanner.data

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import androidx.core.content.edit

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

abstract class DataStorageImpl(
    @ApplicationContext context: Context,
    storageName: String
) : DataStorage {
    private val storage = context.getSharedPreferences(storageName, 0)

    override fun contains(key: String) = storage.contains(key)

    override fun <T : Any> put(key: String, data: T) {
        storage.edit {
            when (data) {
                is Boolean -> putBoolean(key, data)
                is Float -> putFloat(key, data)
                is Int -> putInt(key, data)
                is Long -> putLong(key, data)
                is String -> putString(key, data)
                else -> throw IllegalArgumentException("Unsupported type")
            }
        }
    }

    override fun getBoolean(key: String): Boolean? {
        val value = storage.all[key] ?: return null
        return when (value) {
            is Boolean -> value
            is String -> value.toBooleanStrictOrNullCompat()
            else -> null
        }
    }

    override fun getFloat(key: String): Float? {
        val value = storage.all[key] ?: return null
        return when (value) {
            is Float -> value
            is Int -> value.toFloat()
            is Long -> value.toFloat()
            is String -> value.toFloatOrNull()
            else -> null
        }
    }

    override fun getInt(key: String): Int? {
        val value = storage.all[key] ?: return null
        return when (value) {
            is Int -> value
            is Long -> value.toInt()
            is Float -> value.toInt()
            is String -> value.toIntOrNull()
            else -> null
        }
    }

    override fun getLong(key: String): Long? {
        val value = storage.all[key] ?: return null
        return when (value) {
            is Long -> value
            is Int -> value.toLong()
            is Float -> value.toLong()
            is String -> value.toLongOrNull()
            else -> null
        }
    }

    override fun getString(key: String): String? = storage.all[key] as? String

    override fun remove(key: String) {
        storage.edit { remove(key) }
    }

    override fun clear() {
        storage.edit { clear() }
    }

    private fun String.toBooleanStrictOrNullCompat(): Boolean? = when (lowercase()) {
        "true" -> true
        "false" -> false
        else -> null
    }
}
