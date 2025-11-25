package com.project.tripplanner

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.project.tripplanner.navigation.NavGraph
import com.project.tripplanner.navigation.Screen
import com.project.tripplanner.ui.components.BottomBarItem
import com.project.tripplanner.ui.components.TripPlannerBottomBar
import com.project.tripplanner.ui.theme.TripPlannerTheme
import dagger.hilt.android.AndroidEntryPoint
import io.github.jan.supabase.auth.Auth
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var supabaseAuth: Auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            val currentScreen = Screen.fromRoute(currentRoute)
            val isBottomBarVisible = currentScreen?.isBottomBarVisible == true
            TripPlannerTheme {
                val items = listOf(
                    BottomBarItem(R.drawable.home_alt_24, "Home"),
                    BottomBarItem(R.drawable.ic_document_24, "Docs"),
                    BottomBarItem(R.drawable.plus_fab_38, "Add", isSelectable = false),
                    BottomBarItem(R.drawable.ic_error_24, "Check"),
                    BottomBarItem(R.drawable.home_alt_24, "Profile")
                )

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.onBackground,
                    bottomBar = {
                        AnimatedVisibility(
                            visible = isBottomBarVisible,
                            enter = slideInVertically { it },
                            exit = slideOutVertically { it }
                        ) {
                            TripPlannerBottomBar(
                                items = items,
                                onItemClick = {
                                    // Handle item clicks (e.g., navigation) here
                                }
                            )
                        }

                    }
                ) { paddingValues ->
                    Box(
                        modifier = Modifier.padding(paddingValues)
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
}