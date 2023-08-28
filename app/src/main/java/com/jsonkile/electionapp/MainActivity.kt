package com.jsonkile.electionapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jsonkile.electionapp.ui.screens.dashboard.DashboardScreen
import com.jsonkile.electionapp.ui.screens.dashboard.DashboardViewModel
import com.jsonkile.electionapp.ui.screens.home.HomeScreen
import com.jsonkile.electionapp.ui.screens.home.HomeViewModel
import com.jsonkile.electionapp.ui.screens.vote.VoteScreen
import com.jsonkile.electionapp.ui.screens.vote.VoteViewModel
import com.jsonkile.electionapp.ui.theme.ElectionAppTheme
import com.jsonkile.electionapp.util.voteWithFingerprint

class MainActivity : FragmentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            //create notifications channel
            val name = "general"
            val descriptionText = "all notifications come through this channel"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel("fcm_channel_id", name, importance)
            mChannel.description = descriptionText
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestNotificationPermission()

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
                        animationSpec = tween(durationMillis = 700, easing = LinearEasing),
                        label = ""
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
                                    createAccount = { email, password, voterId ->
                                        homeViewModel.createAccount(email, password, voterId)
                                    }, uiState = uiState,
                                    clearUiMessage = { homeViewModel.clearUiMessage() },
                                    login = { email, password ->
                                        homeViewModel.login(email, password)
                                    }
                                )

                            }

                            composable("dashboard") {

                                val dashboardViewModel: DashboardViewModel = viewModel()
                                val uiState by dashboardViewModel.uiState.collectAsState()

                                val polls by dashboardViewModel.getPollsAsFlow()
                                    .collectAsState(initial = emptyList())

                                DashboardScreen(
                                    finish = { finish() },
                                    useDarkMode = useDarkMode,
                                    toggleDarkMode = { useDarkMode = it },
                                    moveToVoteScreen = { voterId, voterName ->
                                        navController.navigate("vote?voterId=$voterId&voterName=$voterName")
                                    },
                                    uiState = uiState,
                                    clearUiMessage = { dashboardViewModel.clearUiMessage() },
                                    polls = polls
                                )
                            }

                            composable(
                                "vote?voterId={voterId}&voterName={voterName}",
                                arguments = listOf(navArgument("voterId") {
                                    type = NavType.StringType
                                }, navArgument("voterName") {
                                    type = NavType.StringType
                                })
                            ) {

                                val voteViewModel: VoteViewModel = viewModel()
                                val uiState by voteViewModel.uiState.collectAsState()


                                VoteScreen(
                                    onBack = {
                                        navController.popBackStack()
                                    },
                                    onAuth = { candidate ->
                                        this@MainActivity.voteWithFingerprint(
                                            party = candidate.party.orEmpty(),
                                            onSuccess = {
                                                voteViewModel.castVote(candidate)
                                            })
                                    },
                                    uiState = uiState,
                                    clearUiMessage = { voteViewModel.clearUiMessage() })
                            }
                        }

                    }
                }
            }
        }

    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this, android.Manifest.permission.POST_NOTIFICATIONS
                ) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}