package com.jsonkile.electionapp.ui.screens.home

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.jsonkile.electionapp.R
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
    createAccount: (String, String) -> Unit,
    clearUiMessage: () -> Unit
) {

    Box(modifier = Modifier.fillMaxSize()) {

        if (uiState.uiMessage.isNullOrBlank().not()) {
            MessageDialog(
                onDismissRequest = { clearUiMessage() },
                message = uiState.uiMessage.orEmpty()
            )
        }

        if (uiState.isLoading) LoadingDialog {}

        Image(
            painter = painterResource(id = R.drawable.home_background),
            contentDescription = "home background",
            modifier = Modifier
                .fillMaxSize()
                .background(
                    shape = RectangleShape,
                    color = MaterialTheme.colorScheme.secondary
                ),
            contentScale = ContentScale.Crop
        )

        val colorStops = arrayOf(
            0.0f to Color.Transparent,
            0.31f to MaterialTheme.colorScheme.surface.copy(alpha = 0.97f),
            1f to MaterialTheme.colorScheme.surface
        )

        var currentHomeView by remember { mutableStateOf(CurrentHomeView.Home) }

        BackHandler(enabled = true) {
            if (currentHomeView == CurrentHomeView.Home) {
                finish()
            } else {
                currentHomeView = CurrentHomeView.Home
            }
        }

        Box(
            modifier = Modifier
                .aspectRatio(6 / 9f)
                .background(
                    brush = Brush.verticalGradient(
                        0.0f to Color.Transparent,
                        0.31f to MaterialTheme.colorScheme.surface.copy(alpha = 0.97f),
                        1f to MaterialTheme.colorScheme.surface
                    )
                )
                .align(Alignment.BottomCenter),
            contentAlignment = Alignment.Center
        ) {

            AnimatedContent(targetState = currentHomeView) { targetState ->
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
                        LoginForm(backClick = { currentHomeView = CurrentHomeView.Home })
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
        HomeScreen(finish = {}, createAccount = { _, _ -> }, clearUiMessage = { })
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