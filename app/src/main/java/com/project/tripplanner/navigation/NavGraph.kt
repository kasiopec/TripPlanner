package com.project.tripplanner.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.project.tripplanner.login.LoginViewModel
import com.project.tripplanner.ui.screens.RegisterScreen
import com.project.tripplanner.register.RegisterViewModel
import com.project.tripplanner.ui.screens.HomeScreen
import com.project.tripplanner.ui.screens.LoginScreen
import io.github.jan.supabase.compose.auth.ComposeAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

@Composable
fun NavGraph(
    navController: NavHostController,
    supabaseComposeAuth: ComposeAuth
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(route = Screen.Login.route) {
            val loginViewModel = hiltViewModel<LoginViewModel>()
            ObserveAsNavigationEvent(flow = loginViewModel.navigationEvent) {
                when (it) {
                    NavigationEvent.Home -> navController.navigate(Screen.Home.route)
                    NavigationEvent.RegisterForm -> navController.navigate(Screen.RegisterForm.route)
                    else -> {
                        // not interested
                    }
                }
            }
            LoginScreen(
                viewModel = loginViewModel,
                supabaseComposeAuth = supabaseComposeAuth
            )
        }
        composable(route = Screen.Home.route) { HomeScreen() }
        composable(route = Screen.RegisterForm.route) {
            val registerViewModel = viewModel<RegisterViewModel>()
            RegisterScreen(
                viewModel = registerViewModel
            )
        }
    }
}

@Composable
fun ObserveAsNavigationEvent(flow: Flow<NavigationEvent>, onEvent: (NavigationEvent) -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(flow, lifecycleOwner.lifecycle) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            withContext(Dispatchers.Main.immediate) {
                flow.collect(onEvent)
            }
        }
    }
}