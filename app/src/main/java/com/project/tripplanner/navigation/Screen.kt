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

    companion object {
        fun fromRoute(route: String?): Screen? = when (route?.substringBefore("?")) {
            Login.route -> Login
            Home.route -> Home
            RegisterForm.route -> RegisterForm
            ResetPassword.route -> ResetPassword
            else -> null
        }
    }
}
