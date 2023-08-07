package com.jsonkile.electionapp

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jsonkile.electionapp.ui.screens.dashboard.DashboardScreen
import com.jsonkile.electionapp.ui.screens.home.HomeScreen
import com.jsonkile.electionapp.ui.screens.home.HomeViewModel
import com.jsonkile.electionapp.ui.screens.vote.VoteScreen
import com.jsonkile.electionapp.ui.theme.ElectionAppTheme
import com.jsonkile.electionapp.util.voteWithFingerprint

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {

            var useDarkMode by remember { mutableStateOf(true) }

            ElectionAppTheme(useDarkTheme = useDarkMode) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.surface
                ) {

                    val navController = rememberNavController()

                    var currentDestination by remember { mutableStateOf("dashboard") }

                    LaunchedEffect(Unit) {
                        Firebase.auth.addAuthStateListener {
                            if (it.currentUser == null) {
                                navController.navigate("home")
                            } else {
                                navController.navigate("dashboard")
                            }
                        }
                    }

                    val surfaceColor = MaterialTheme.colorScheme.surface
                    val image = ImageBitmap.imageResource(id = R.drawable.home_background)

                    val blushRadius: Float by animateFloatAsState(
                        targetValue = if (currentDestination == "home") LocalDensity.current.run { 650.dp.toPx() } else LocalDensity.current.run { 300.dp.toPx() },
                        animationSpec = tween(durationMillis = 700, easing = LinearEasing)
                    )

                    LaunchedEffect(Unit) {
                        navController.addOnDestinationChangedListener { _, destination, _ ->
                            currentDestination = destination.route ?: "home"
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .drawWithContent {
                                drawImage(image = image, topLeft = Offset.Zero)
                                drawRect(
                                    Brush.radialGradient(
                                        listOf(Color.Transparent, surfaceColor),
                                        center = Offset.Zero,
                                        radius = blushRadius,
                                    )
                                )
                                drawContent()
                            }
                    ) {
                        NavHost(
                            navController = navController,
                            startDestination = "dashboard",
                            modifier = Modifier
                        ) {
                            composable("home") {

                                val homeViewModel: HomeViewModel = viewModel()
                                val uiState by homeViewModel.uiState.collectAsState()

                                LaunchedEffect(uiState.isAuthenticated) {
                                    if (uiState.isAuthenticated) navController.navigate("dashboard")
                                }

                                HomeScreen(
                                    finish = { finish() },
                                    createAccount = { email, password ->
                                        homeViewModel.createAccount(email, password)
                                    }, uiState = uiState,
                                    clearUiMessage = { homeViewModel.clearUiMessage() },
                                    login = { email, password ->
                                        homeViewModel.login(email, password)
                                    }
                                )

                            }

                            composable("dashboard") {
                                DashboardScreen(
                                    finish = { finish() },
                                    useDarkMode = useDarkMode,
                                    toggleDarkMode = { useDarkMode = it }, moveToVoteScreen = {
                                        navController.navigate("vote")
                                    })
                            }

                            composable("vote") {
                                VoteScreen(onBack = {
                                    navController.popBackStack()
                                }, onAuth = { party ->
                                    this@MainActivity.voteWithFingerprint(
                                        party = party,
                                        onSuccess = {})
                                })
                            }
                        }

                    }
                }
            }
        }

    }
}