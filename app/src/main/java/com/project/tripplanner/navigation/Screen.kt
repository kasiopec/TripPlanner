package com.project.tripplanner.navigation

sealed class Screen(
    val route: String,
    val title: String,
    val isBottomBarVisible: Boolean = false
) {
    object Login : Screen(route = "login_screen", title = "Login")
    object Home : Screen(route = "home_screen", title = "Home", isBottomBarVisible = true)
    object RegisterForm : Screen(route = "register_screen", title = "Registration")
    object ResetPassword : Screen(route = "reset_password_screen", title = "Reset password")
    object TripDetails : Screen(route = "trip_details/{tripId}", title = "Trip details") {
        const val ARG_TRIP_ID = "tripId"
        fun createRoute(tripId: Long): String = "trip_details/$tripId"
    }
    object TripForm : Screen(route = "trip_form_screen/{tripId}", title = "Trip Form") {
        const val ARG_TRIP_ID = "tripId"
        fun createRoute(tripId: Long? = null): String =
            if (tripId != null) "trip_form_screen/$tripId" else "trip_form_screen/-1"
    }
    object Debug : Screen(route = "debug_screen", title = "Debug")

    companion object {
        fun fromRoute(route: String?): Screen? = when (route?.substringBefore("/")?.substringBefore("?")) {
            Login.route.substringBefore("/") -> Login
            Home.route.substringBefore("/") -> Home
            RegisterForm.route.substringBefore("/") -> RegisterForm
            ResetPassword.route.substringBefore("/") -> ResetPassword
            TripDetails.route.substringBefore("/") -> TripDetails
            "trip_form_screen" -> TripForm
            Debug.route.substringBefore("/") -> Debug
            else -> null
        }
    }
}
