package com.project.tripplanner.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
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
import com.project.tripplanner.R
import com.project.tripplanner.navigation.BottomBarItem
import com.project.tripplanner.navigation.Screen
import com.project.tripplanner.ui.theme.TripPlannerTheme

@Composable
fun TripPlannerBottomBar(
    modifier: Modifier = Modifier,
    currentScreen: Screen? = null,
    onItemSelected: (Screen) -> Unit,
    onLastItemLongPress: (() -> Unit)? = null
) {
    val items = listOf(
        BottomBarItem(icon = R.drawable.home_alt_24, route = Screen.Home.route, contentDescription = "Home"),
        BottomBarItem(icon = R.drawable.ic_list_24, route = Screen.TripDetails.route, contentDescription = "Trip details"),
        BottomBarItem(icon = R.drawable.plus_fab_38, contentDescription = "Add", isSelectable = false),
        BottomBarItem(icon = R.drawable.ic_error_24, route = Screen.Login.route, contentDescription = "Check"),
        BottomBarItem(icon = R.drawable.ic_user_24, route = Screen.Home.route, contentDescription = "Profile")
    )
    var selectedItem by remember(currentScreen) {
        mutableStateOf(items.firstOrNull { it.isSelectable && it.route == currentScreen?.route })
    }
    val colors = TripPlannerTheme.colors

    Column(
        modifier = modifier
            .background(color = colors.surface)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Top
    ) {
        Row(
            modifier = Modifier
                .background(color = colors.surface)
                .fillMaxWidth()
                .heightIn(min = 49.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            items.forEach { item ->
                val isSelected = item == selectedItem
                val contentColor = when {
                    !item.isSelectable -> Color.Unspecified
                    isSelected -> colors.primary
                    else -> colors.onSurface
                }

                val isLastItem = item == items.last()

                Column(
                    modifier = Modifier
                        .heightIn(min = 48.dp)
                        .widthIn(min = 48.dp)
                        .clip(CircleShape)
                        .combinedClickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(bounded = true, color = colors.primary),
                            onClick = {
                                if (item.isSelectable) {
                                    selectedItem = item
                                    when (item.route) {
                                        Screen.TripDetails.route -> onItemSelected(Screen.TripDetails)
                                        Screen.Home.route -> onItemSelected(Screen.Home)
                                        Screen.Login.route -> onItemSelected(Screen.Login)
                                        else -> item.route?.let { route ->
                                            Screen.fromRoute(route)?.let { onItemSelected(it) }
                                        }
                                    }
                                } else {
                                    onItemSelected(Screen.TripForm)
                                }

                            },
                            onLongClick = if (isLastItem) {
                                {
                                    onLastItemLongPress?.invoke()
                                }
                            } else {
                                null
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
                .windowInsetsBottomHeight(WindowInsets.navigationBars)
        )
    }
}

@Composable
@PreviewLightDark
fun TripPlannerBottomBarPreview() {
    TripPlannerTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .systemBarsPadding()
                .navigationBarsPadding()
                .background(Color.Gray),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TripPlannerBottomBar(
                currentScreen = Screen.Home,
                onItemSelected = {}
            )
        }
    }
}
