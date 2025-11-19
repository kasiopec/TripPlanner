package com.project.tripplanner.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.project.tripplanner.Effect
import com.project.tripplanner.features.home.HomeScreen
import com.project.tripplanner.features.login.LoginEffect
import com.project.tripplanner.features.login.LoginEvent
import com.project.tripplanner.features.login.LoginScreen
import com.project.tripplanner.features.login.LoginViewModel
import com.project.tripplanner.features.register.RegisterEffect
import com.project.tripplanner.features.register.RegisterScreen
import com.project.tripplanner.features.register.RegisterViewModel
import com.project.tripplanner.features.resetpassword.ResetPasswordScreen
import com.project.tripplanner.features.resetpassword.ResetPasswordViewModel
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

private const val REFRESH_FAILURE_REASON = "refreshFailed"

@Composable
fun NavGraph(
    navController: NavHostController,
    supabaseAuth: Auth
) {
    LaunchedEffect(Unit) {
        supabaseAuth.sessionStatus.collect { status ->
            when (status) {
                is SessionStatus.Authenticated -> {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                    }
                }

                is SessionStatus.NotAuthenticated -> {
                    navController.navigate(route = Screen.Login.route) {
                        popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                    }
                }

                is SessionStatus.RefreshFailure -> {
                    supabaseAuth.signOut()
                    navController.navigate("${Screen.Login.route}?reason=$REFRESH_FAILURE_REASON") {
                        popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                    }
                }

                else -> Unit
            }
        }
    }
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(route = Screen.Login.route) { backStackEntry ->
            val loginViewModel = hiltViewModel<LoginViewModel>()
            val reason = backStackEntry.arguments?.getString("reason")
            LaunchedEffect(reason) {
                if (reason == REFRESH_FAILURE_REASON) {
                    loginViewModel.emitEvent(LoginEvent.ForcedLogoutSessionExpiredEvent)
                }
            }
            LoginScreen(
                viewModel = loginViewModel,
                supabaseAuth = supabaseAuth
            )
            ObserveNavigationEffect(flow = loginViewModel.effect) { effect ->
                when (effect) {
                    LoginEffect.NavigateToHomeScreenEffect -> navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) {
                            inclusive = true
                        }
                    }

                    LoginEffect.NavigateToRegisterFormEffect -> navController.navigate(Screen.RegisterForm.route)
                    LoginEffect.NavigateToResetPasswordScreenEffect -> navController.navigate(Screen.ResetPassword.route)
                }
            }
        }
        composable(route = Screen.Home.route) { HomeScreen() }
        composable(route = Screen.RegisterForm.route) {
            val registerViewModel = hiltViewModel<RegisterViewModel>()
            RegisterScreen(viewModel = registerViewModel)
            ObserveNavigationEffect(flow = registerViewModel.effect) { effect ->
                when (effect) {
                    RegisterEffect.GoBack -> navController.navigateUp()
                    RegisterEffect.GoToLogin -> navController.navigate(route = Screen.Login.route) {
                        popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                    }

                    RegisterEffect.GoToHome -> navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            }
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

@Composable
fun <T : Effect> ObserveNavigationEffect(flow: Flow<T>, effect: (T) -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(flow, lifecycleOwner.lifecycle) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            withContext(Dispatchers.Main.immediate) {
                flow.collect(effect)
            }
        }
    }
}