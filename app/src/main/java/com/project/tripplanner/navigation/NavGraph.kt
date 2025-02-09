package com.project.tripplanner.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.project.tripplanner.features.login.LoginViewModel
import com.project.tripplanner.features.register.RegisterScreen
import com.project.tripplanner.features.register.RegisterViewModel
import com.project.tripplanner.features.home.HomeScreen
import com.project.tripplanner.features.login.LoginScreen
import com.project.tripplanner.features.resetpassword.ResetPasswordScreen
import com.project.tripplanner.features.resetpassword.ResetPasswordViewModel
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
                    NavigationEvent.Home -> navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) {
                            inclusive = true
                        }
                    }

                    NavigationEvent.RegisterForm -> navController.navigate(Screen.RegisterForm.route)
                    NavigationEvent.ResetPassword -> navController.navigate(Screen.ResetPassword.route)
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
            val registerViewModel = hiltViewModel<RegisterViewModel>()
            ObserveAsNavigationEvent(flow = registerViewModel.navigationEvent) {
                when (it) {
                    NavigationEvent.Back -> navController.navigateUp()
                    NavigationEvent.Login -> navController.navigate(route = Screen.Login.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            inclusive = true
                        }
                    }

                    else -> {
                        // don't navigate
                    }
                }
            }
            RegisterScreen(viewModel = registerViewModel)
        }
        composable(route = Screen.ResetPassword.route) {
            val resetPasswordViewModel = hiltViewModel<ResetPasswordViewModel>()
            ObserveAsNavigationEvent(flow = resetPasswordViewModel.navigationEvent) {
                when (it) {
                    NavigationEvent.Home -> {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) {
                                inclusive = true
                            }
                        }
                    }

                    NavigationEvent.Back -> navController.navigateUp()

                    else -> {
                        // don't navigate
                    }
                }
            }
            ResetPasswordScreen(viewModel = resetPasswordViewModel)
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