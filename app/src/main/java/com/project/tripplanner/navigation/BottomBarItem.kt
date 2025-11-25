package com.project.tripplanner.navigation

data class BottomBarItem(
    val icon: Int,
    val contentDescription: String? = null,
    val route: String? = null,
    val isSelectable: Boolean = true
)