package com.project.tripplanner.navigation

import androidx.annotation.DrawableRes
import com.project.tripplanner.R

sealed class Screen(val route: String, val title: String, @DrawableRes val iconResId: Int? = null) {
    object Login : Screen("login_screen", "Login", null)
    object Home : Screen("home_screen", "Home", R.drawable.ic_home_32)
    object RegisterForm : Screen("register_screen", "Registration", null)
    object ResetPassword : Screen("reset_password_screen", "Reset password", null)
}
