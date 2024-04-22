package com.project.tripplanner.repositories

import com.project.tripplanner.data.UserPrefsStorage
import javax.inject.Inject

const val ACCESS_TOKEN_KEY = "access_token"

interface UserPrefRepository {
    fun saveUserAccessToken(authToken: String)
    fun getUserAccessToken(): String?
}

class UserPrefsRepositoryImp @Inject constructor(
    private val storage: UserPrefsStorage
) : UserPrefRepository {

    override fun saveUserAccessToken(authToken: String) {
        storage.put(ACCESS_TOKEN_KEY, authToken)
    }

    override fun getUserAccessToken(): String? = storage.getString(ACCESS_TOKEN_KEY)
}