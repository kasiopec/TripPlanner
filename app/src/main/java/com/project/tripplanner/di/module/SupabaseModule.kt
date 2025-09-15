package com.project.tripplanner.di.module

import com.project.tripplanner.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.compose.auth.ComposeAuth
import io.github.jan.supabase.compose.auth.composeAuth
import io.github.jan.supabase.compose.auth.googleNativeLogin
import io.github.jan.supabase.createSupabaseClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SupabaseModule {

    @Provides
    @Singleton
    fun providesSupabase(): SupabaseClient {
        return createSupabaseClient(
            supabaseKey = BuildConfig.SUPABASE_PUBLISHABLE_KEY,
            supabaseUrl = BuildConfig.SUPABASE_URL
        ) {
            install(Auth)
            install(ComposeAuth) {
                googleNativeLogin(BuildConfig.SUPABASE_CLIENT_ID)
            }
        }
    }

    @Provides
    @Singleton
    fun providesSupabaseComposeAuth(client: SupabaseClient): ComposeAuth = client.composeAuth

    @Provides
    @Singleton
    fun providesSupabaseAuth(client: SupabaseClient): Auth = client.auth
}