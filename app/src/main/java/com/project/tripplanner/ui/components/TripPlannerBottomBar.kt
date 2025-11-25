package com.project.tripplanner.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.project.tripplanner.R
import com.project.tripplanner.navigation.BottomBarItem
import com.project.tripplanner.navigation.Screen
import com.project.tripplanner.ui.theme.TripPlannerTheme

@Composable
fun TripPlannerBottomBar(
    modifier: Modifier = Modifier,
    currentScreen: Screen? = null,
    navController: NavController
) {
    val items = listOf(
        BottomBarItem(icon = R.drawable.home_alt_24, route = Screen.Home.route, contentDescription = "Home"),
        BottomBarItem(icon = R.drawable.ic_calendar_24, route = Screen.Home.route, contentDescription = "Docs"),
        BottomBarItem(icon = R.drawable.plus_fab_38, contentDescription = "Add", isSelectable = false),
        BottomBarItem(icon = R.drawable.ic_error_24, route = Screen.Login.route, contentDescription = "Check"),
        BottomBarItem(icon = R.drawable.ic_user_24, route = Screen.Home.route, contentDescription = "Profile")
    )
    var selectedItem by remember { mutableStateOf(items.firstOrNull { it.isSelectable && it.route == currentScreen?.route }) }
    val paddingValues = WindowInsets.navigationBars.asPaddingValues()
    val bottomBarHeight = paddingValues.calculateBottomPadding()

    Column(
        modifier = modifier
            .background(color = MaterialTheme.colorScheme.surface)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Top
    ) {
        Row(
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.surface)
                .fillMaxWidth()
                .heightIn(min = 49.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            items.forEach { item ->
                val isSelected = item == selectedItem
                val contentColor = when {
                    !item.isSelectable -> Color.Unspecified
                    isSelected -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.onSurface
                }

                Column(
                    modifier = Modifier
                        .heightIn(min = 48.dp)
                        .widthIn(min = 48.dp)
                        .clip(CircleShape)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(bounded = true, color = MaterialTheme.colorScheme.primary),
                            onClick = {
                                if (item.isSelectable) {
                                    selectedItem = item
                                    item.route?.let {
                                        navController.navigate(it) {
                                            popUpTo(Screen.Home.route) {
                                                inclusive = true
                                            }
                                        }
                                    }
                                } else {
                                    // handle add item logic
                                }

                            }
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    val topPadding = if (item.isSelectable) 12.dp else 5.dp
                    val bottomPadding = if (!item.isSelectable) 5.dp else 0.dp
                    Icon(
                        modifier = Modifier.padding(top = topPadding, bottom = bottomPadding),
                        painter = painterResource(item.icon),
                        contentDescription = item.contentDescription,
                        tint = contentColor
                    )

                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .padding(bottom = 8.dp)
                                .size(4.dp)
                                .background(color = contentColor, shape = CircleShape)
                        )
                    } else if (item.isSelectable) {
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                        )
                    }
                }
            }
        }
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(bottomBarHeight)
        )
    }
}

@Composable
@PreviewLightDark
fun TripPlannerBottomBarPreview() {
    TripPlannerTheme {
        val navController = rememberNavController()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .systemBarsPadding()
                .navigationBarsPadding()
                .background(Color.Gray),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TripPlannerBottomBar(navController = navController, currentScreen = Screen.Home)
        }
    }
}