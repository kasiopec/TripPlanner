package com.project.tripplanner.features.login

import java.security.MessageDigest


private const val HEX_BYTE_FORMAT = "%02x"

private fun Byte.toHex(): String = HEX_BYTE_FORMAT.format(this)

fun createNonce(rawNonce: String): String {
    val bytes = rawNonce.toByteArray()
    val messageDigest = MessageDigest.getInstance("SHA-256")
    val digest = messageDigest.digest(bytes)
    val hashedNonce = digest.joinToString(separator = "") { it.toHex() }
    return hashedNonce
}