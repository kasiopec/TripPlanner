package com.project.tripplanner.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.project.tripplanner.navigation.Screen
import com.project.tripplanner.register.RegisterEvent
import com.project.tripplanner.register.RegisterUiState
import com.project.tripplanner.register.RegisterViewModel
import com.project.tripplanner.ui.components.text.TitleLargeBold

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel
) {
    LaunchedEffect(Unit) {
        viewModel.emitEvent(RegisterEvent.ScreenVisibleEvent)
    }
    val uiState by viewModel.state.collectAsState()
    val loadingState = uiState as? RegisterUiState.Loading
    val registerState = uiState as? RegisterUiState.Register

    when {
        registerState != null -> RegisterContent()
    }
}

@Composable
fun RegisterContent() {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            TitleLargeBold(text = "${Screen.RegisterForm.title}")
        }
    }
}