package com.project.tripplanner.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.project.tripplanner.Effect
import com.project.tripplanner.features.home.HomeRoute
import com.project.tripplanner.features.login.LoginEffect
import com.project.tripplanner.features.login.LoginEvent
import com.project.tripplanner.features.login.LoginScreen
import com.project.tripplanner.features.login.LoginViewModel
import com.project.tripplanner.features.register.RegisterEffect
import com.project.tripplanner.features.register.RegisterScreen
import com.project.tripplanner.features.register.RegisterViewModel
import com.project.tripplanner.features.resetpassword.ResetPasswordScreen
import com.project.tripplanner.features.resetpassword.ResetPasswordViewModel
import com.project.tripplanner.features.tripform.TripFormEffect
import com.project.tripplanner.features.tripform.TripFormEvent
import com.project.tripplanner.features.tripform.TripFormScreen
import com.project.tripplanner.features.tripform.TripFormUiState
import com.project.tripplanner.features.tripform.TripFormViewModel
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
        composable(route = Screen.Home.route) {
            HomeRoute(
                onTripClick = { tripId ->
                    navController.navigate(Screen.TripForm.createRoute(tripId))
                }
            )
        }
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
                    }
                }
            }
            ResetPasswordScreen(viewModel = resetPasswordViewModel)
        }
        composable(
            route = Screen.TripForm.route,
            arguments = listOf(
                navArgument(Screen.TripForm.ARG_TRIP_ID) {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) { backStackEntry ->
            val tripFormViewModel = hiltViewModel<TripFormViewModel>()
            val uiState by tripFormViewModel.state.collectAsState()
            val tripId = backStackEntry.arguments?.getLong(Screen.TripForm.ARG_TRIP_ID)
                ?.takeIf { it > 0 }

            LaunchedEffect(tripId) {
                tripFormViewModel.emitEvent(TripFormEvent.ScreenLoaded(tripId))
            }

            when (val state = uiState) {
                is TripFormUiState.Loading -> {
                }

                is TripFormUiState.Form -> {
                    TripFormScreen(
                        uiState = state,
                        onDestinationChange = { tripFormViewModel.emitEvent(TripFormEvent.DestinationChanged(it)) },
                        onStartDateClick = { tripFormViewModel.emitEvent(TripFormEvent.StartDateClicked) },
                        onEndDateClick = { tripFormViewModel.emitEvent(TripFormEvent.EndDateClicked) },
                        onStartDateSelected = { tripFormViewModel.emitEvent(TripFormEvent.StartDateSelected(it)) },
                        onEndDateSelected = { tripFormViewModel.emitEvent(TripFormEvent.EndDateSelected(it)) },
                        onDatePickerDismissed = { tripFormViewModel.emitEvent(TripFormEvent.DatePickerDismissed) },
                        onSingleDayChange = { tripFormViewModel.emitEvent(TripFormEvent.SingleDayToggled(it)) },
                        onCoverImageSelected = { tripFormViewModel.emitEvent(TripFormEvent.CoverImageSelected(it)) },
                        onNotesChange = { tripFormViewModel.emitEvent(TripFormEvent.NotesChanged(it)) },
                        onSaveClick = { tripFormViewModel.emitEvent(TripFormEvent.SaveClicked) },
                        onBackClick = { tripFormViewModel.emitEvent(TripFormEvent.BackClicked) }
                    )
                }

                is TripFormUiState.GlobalError -> {
                }
            }

            val context = LocalContext.current
            ObserveNavigationEffect(flow = tripFormViewModel.effect) { effect ->
                when (effect) {
                    TripFormEffect.NavigateBack -> navController.navigateUp()
                    is TripFormEffect.NavigateToTripDetail -> {
                        navController.navigateUp()
                    }

                    is TripFormEffect.ShowSnackbar -> {
                        Toast.makeText(context, effect.messageResId, Toast.LENGTH_SHORT).show()
                    }
                }
            }
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
