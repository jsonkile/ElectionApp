package com.jsonkile.electionapp.ui.screens.home

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jsonkile.electionapp.ui.components.LoadingDialog
import com.jsonkile.electionapp.ui.components.MessageDialog
import com.jsonkile.electionapp.ui.components.PrimaryButton
import com.jsonkile.electionapp.ui.components.SecondaryButton
import com.jsonkile.electionapp.ui.theme.ElectionAppTheme
import com.jsonkile.electionapp.ui.theme.Typography

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun HomeScreen(
    finish: () -> Unit,
    uiState: HomeViewModel.UiState = HomeViewModel.UiState(),
    createAccount: (String, String, String) -> Unit,
    login: (String, String) -> Unit,
    clearUiMessage: () -> Unit
) {

    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    AnimatedVisibility(
        enter = fadeIn(
            tween(delayMillis = 500, easing = LinearEasing, durationMillis = 700),
            initialAlpha = 0f
        ), visible = visible, exit = fadeOut(
            tween(easing = LinearEasing, durationMillis = 700),
        )
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

            if (uiState.uiMessage.isNullOrBlank().not()) {
                MessageDialog(
                    onDismissRequest = { clearUiMessage() },
                    message = uiState.uiMessage.orEmpty()
                )
            }

            if (uiState.isLoading) LoadingDialog {}

            var currentHomeView by remember { mutableStateOf(CurrentHomeView.Home) }

            BackHandler(enabled = true) {
                if (currentHomeView == CurrentHomeView.Home) {
                    finish()
                } else {
                    currentHomeView = CurrentHomeView.Home
                }
            }

            AnimatedContent(
                targetState = currentHomeView,
                modifier = Modifier
                    .wrapContentSize()
                    .padding(top = 300.dp),
            ) { targetState ->
                when (targetState) {
                    CurrentHomeView.Home -> {
                        HomeControls(
                            onLoginClick = { currentHomeView = CurrentHomeView.Login },
                            onCreateAccountClick = {
                                currentHomeView = CurrentHomeView.CreateAccount
                            }
                        )
                    }

                    CurrentHomeView.Login -> {
                        LoginForm(
                            backClick = { currentHomeView = CurrentHomeView.Home },
                            login = login
                        )
                    }

                    CurrentHomeView.CreateAccount -> {
                        CreateAccountForm(
                            backClick = { currentHomeView = CurrentHomeView.Home },
                            onCreateAccount = createAccount
                        )
                    }
                }
            }

        }

    }
}

@Preview
@Composable
fun PreviewHomeScreen() {
    ElectionAppTheme(useDarkTheme = false) {
        HomeScreen(
            finish = {},
            createAccount = { _, _, _ -> },
            clearUiMessage = { },
            login = { _, _ -> })
    }
}

@Composable
fun HomeControls(onLoginClick: () -> Unit, onCreateAccountClick: () -> Unit) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "#NigeriaDecides 🇳🇬",
            style = Typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        PrimaryButton(
            onClick = { onLoginClick() },
            label = "Login to your account",
            modifier = Modifier.padding(top = 27.dp)
        )

        SecondaryButton(
            onClick = { onCreateAccountClick() },
            label = "Create e-voting account",
            modifier = Modifier.padding(top = 18.dp),
            loading = false
        )
    }
}

enum class CurrentHomeView {
    Home, Login, CreateAccount
}