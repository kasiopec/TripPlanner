package com.project.tripplanner.di.module

import com.project.tripplanner.data.UserPrefsStorage
import com.project.tripplanner.repositories.UserPrefRepository
import com.project.tripplanner.repositories.UserPrefsRepositoryImp
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun providesUserPrefsRepository(userPrefsStorage: UserPrefsStorage): UserPrefRepository {
        return UserPrefsRepositoryImp(userPrefsStorage)
    }
}