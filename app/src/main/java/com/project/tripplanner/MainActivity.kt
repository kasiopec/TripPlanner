package com.project.tripplanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.project.tripplanner.navigation.NavGraph
import com.project.tripplanner.navigation.Screen
import com.project.tripplanner.ui.components.TripPlannerBottomBar
import com.project.tripplanner.ui.theme.TripPlannerTheme
import androidx.core.view.WindowCompat
import dagger.hilt.android.AndroidEntryPoint
import io.github.jan.supabase.auth.Auth
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var supabaseAuth: Auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            TripPlannerTheme {
                val colors = TripPlannerTheme.colors
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    NavGraph(
                        navController = navController,
                        supabaseAuth = supabaseAuth
                    )
                }
            }
        }
    }
}
