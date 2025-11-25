package com.project.tripplanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.project.tripplanner.navigation.NavGraph
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
            TripPlannerTheme {
                val items = listOf(
                    BottomBarItem(R.drawable.home_alt_24, "Home"),
                    BottomBarItem(R.drawable.ic_document_24, "Docs"),
                    BottomBarItem(R.drawable.plus_fab_38, "Add", isSelectable = false),
                    BottomBarItem(R.drawable.ic_check_circle_24, "Check"),
                    BottomBarItem(R.drawable.ic_home_32, "Profile")
                )

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.onBackground,
                    bottomBar = {
                        TripPlannerBottomBar(
                            items = items,
                            onItemClick = {
                                // Handle item clicks (e.g., navigation) here
                            }
                        )
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