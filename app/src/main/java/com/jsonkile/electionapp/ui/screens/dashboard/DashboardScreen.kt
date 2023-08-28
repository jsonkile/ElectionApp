package com.jsonkile.electionapp.ui.screens.dashboard

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.github.tehras.charts.bar.BarChart
import com.github.tehras.charts.bar.BarChartData
import com.github.tehras.charts.bar.renderer.bar.SimpleBarDrawer
import com.github.tehras.charts.bar.renderer.label.SimpleValueDrawer
import com.github.tehras.charts.bar.renderer.xaxis.SimpleXAxisDrawer
import com.github.tehras.charts.bar.renderer.yaxis.LabelFormatter
import com.github.tehras.charts.bar.renderer.yaxis.SimpleYAxisDrawer
import com.github.tehras.charts.piechart.animation.simpleChartAnimation
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.jsonkile.electionapp.data.models.Candidate
import com.jsonkile.electionapp.data.models.Poll
import com.jsonkile.electionapp.data.models.Voter
import com.jsonkile.electionapp.ui.components.ElectionWinnerComponent
import com.jsonkile.electionapp.ui.components.LoadingDialog
import com.jsonkile.electionapp.ui.components.MessageDialog
import com.jsonkile.electionapp.ui.components.PrimaryButton
import com.jsonkile.electionapp.ui.theme.ElectionAppTheme

@Composable
fun DashboardScreen(
    finish: () -> Unit,
    useDarkMode: Boolean,
    toggleDarkMode: (Boolean) -> Unit,
    moveToVoteScreen: (String, String) -> Unit,
    uiState: DashboardViewModel.UiState,
    clearUiMessage: () -> Unit,
    polls: List<Poll> = emptyList()
) {
    BackHandler(enabled = true) {
        finish()
    }

    if (uiState.isLoading) LoadingDialog {}

    if (uiState.uiMessage.isNullOrBlank().not()) {
        MessageDialog(
            onDismissRequest = { clearUiMessage() },
            message = uiState.uiMessage.orEmpty()
        )
    }

    //set to false
    var visible by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        visible = true
    }

    var expandedCandidate: Candidate? by remember { mutableStateOf(null) }

    AnimatedVisibility(
        enter = fadeIn(
            tween(delayMillis = 500, easing = LinearEasing, durationMillis = 700),
            initialAlpha = 0f
        ), visible = visible, exit = fadeOut(
            tween(easing = LinearEasing, durationMillis = 700),
        )
    ) {

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 30.dp, vertical = 50.dp),
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            //dark mode
            item {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Switch(
                        checked = useDarkMode,
                        onCheckedChange = { toggleDarkMode(it) },
                        thumbContent = {
                            Icon(imageVector = Icons.Default.DarkMode, contentDescription = null)
                        }, modifier = Modifier.align(Alignment.CenterEnd)
                    )
                }
            }

            //welcome
            uiState.voter?.let {
                item {
                    Text(
                        text = "Welcome back, ${uiState.voter.firstName}",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 26.sp
                        ),
                        maxLines = 1,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        "Securely and quickly have your say from the comfort of your home",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 13.sp,
                        lineHeight = 15.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))


                    PrimaryButton(
                        onClick = {
                            moveToVoteScreen(
                                uiState.voter.voterId.orEmpty(),
                                uiState.voter.fullName
                            )
                        },
                        label = if (Firebase.remoteConfig.getBoolean("polls_active")) "Vote now" else "Voting closed",
                        enabled = Firebase.remoteConfig.getBoolean("polls_active")
                    )
                }
            }

            //winner
            if (Firebase.remoteConfig.getBoolean("show_winner") && polls.isNotEmpty() && uiState.candidates.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Your winner",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 19.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    val winningCandidate =
                        uiState.candidates.firstOrNull { candidate -> candidate.party == polls.maxByOrNull { poll -> poll.count }?.party }

                    ElectionWinnerComponent(
                        imageUrl = winningCandidate?.profileImageUrl.orEmpty(),
                        modifier = Modifier
                    )

                    Spacer(modifier = Modifier.height(15.dp))

                    Text(
                        winningCandidate?.fullName.orEmpty(), fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        modifier = Modifier
                            .wrapContentSize(),
                        textAlign = TextAlign.Center,
                        lineHeight = 13.sp
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        "has been confirmed the winner of the latest presidential election",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        modifier = Modifier.wrapContentSize(),
                        textAlign = TextAlign.Center,
                        lineHeight = 13.sp
                    )
                }
            }

            //polls
            if (polls.isNotEmpty()) {
                item {

                    Spacer(modifier = Modifier.height(13.dp))

                    Text(
                        text = "Live polls",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 19.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start
                    )

                    Spacer(modifier = Modifier.height(15.dp))

                    val barColor = MaterialTheme.colorScheme.primary

                    val bars =
                        remember(polls, barColor) {
                            polls.map { poll ->
                                BarChartData.Bar(
                                    label = "${poll.party}-${poll.count}",
                                    value = poll.count.toFloat(),
                                    color = barColor
                                )
                            }
                        }


                    BarChart(
                        barChartData = BarChartData(bars = bars),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        animation = simpleChartAnimation(),
                        barDrawer = SimpleBarDrawer(),
                        xAxisDrawer = SimpleXAxisDrawer(axisLineColor = MaterialTheme.colorScheme.onSurface),
                        yAxisDrawer = SimpleYAxisDrawer(
                            axisLineColor = MaterialTheme.colorScheme.onSurface,
                            labelTextColor = MaterialTheme.colorScheme.onSurface,
                            labelTextSize = 8.sp,
                            labelValueFormatter = object : LabelFormatter {
                                override fun invoke(value: Float): String = "${value.toInt()}"
                            }
                        ),
                        labelDrawer = SimpleValueDrawer(
                            drawLocation = SimpleValueDrawer.DrawLocation.Outside,
                            labelTextColor = MaterialTheme.colorScheme.onSurface,
                            labelTextSize = 9.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(15.dp))

                    Text(
                        text = "Registered voters: ${uiState.votersCount}",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Normal, fontSize = 10.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.wrapContentSize(),
                        textAlign = TextAlign.Start
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    Text(
                        text = "Votes recorded: ${polls.sumOf { it.count }}",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Normal, fontSize = 10.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.wrapContentSize(),
                        textAlign = TextAlign.Start
                    )
                }
            }

            //candidates
            if (uiState.candidates.isNotEmpty()) {
                item {

                    Spacer(modifier = Modifier.height(13.dp))

                    Text(
                        text = "Meet the candidates",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 19.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start
                    )
                }

                item {

                    val scrollState = rememberScrollState()

                    Row(
                        modifier = Modifier
                            .layout { measurable, constraints ->
                                val placeable = measurable.measure(
                                    constraints.copy(
                                        maxWidth = constraints.maxWidth + 60.dp.roundToPx()
                                    )
                                )
                                layout(placeable.width, placeable.height) {
                                    placeable.place(0, 0)
                                }
                            }
                            .wrapContentHeight()
                            .horizontalScroll(enabled = true, state = scrollState)
                            .padding(horizontal = 30.dp),
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {

                        uiState.candidates.forEach { candidate ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .wrapContentHeight()
                                    .clickable(interactionSource = remember {
                                        MutableInteractionSource()
                                    }, indication = rememberRipple(bounded = false)) {
                                        expandedCandidate = candidate
                                    }
                            ) {
                                AsyncImage(
                                    model = candidate.profileImageUrl,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(50.dp)
                                        .clip(CircleShape)
                                        .background(
                                            shape = CircleShape,
                                            color = MaterialTheme.colorScheme.surfaceVariant
                                        ),
                                    contentScale = ContentScale.Crop
                                )

                                Spacer(modifier = Modifier.height(7.dp))

                                Text(
                                    candidate.fullName, fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 2,
                                    modifier = Modifier
                                        .width(80.dp)
                                        .wrapContentHeight(),
                                    textAlign = TextAlign.Center,
                                    lineHeight = 13.sp
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    candidate.party.orEmpty(), fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    modifier = Modifier.wrapContentSize(),
                                    textAlign = TextAlign.Center,
                                    lineHeight = 13.sp
                                )
                            }
                        }

                    }

                }
            }

            //press
            if (uiState.headlines.isNotEmpty()) {
                item {

                    Spacer(modifier = Modifier.height(13.dp))

                    Text(
                        text = "In the news",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 19.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start
                    )
                }

                items(items = uiState.headlines) { headline ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                    ) {
                        AsyncImage(
                            model = headline.urlToImage,
                            contentDescription = "headline image",
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(3 / 2f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(10.dp)
                                ),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(3f)) {
                            Text(
                                headline.title.orEmpty(),
                                maxLines = 2,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 11.sp, lineHeight = 16.sp
                            )
                            Spacer(modifier = Modifier.height(5.dp))
                            Text(
                                headline.source?.name.orEmpty(),
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            //auth
            uiState.voter?.let {
                item {

                    Spacer(modifier = Modifier.height(25.dp))

                    val lineColor = MaterialTheme.colorScheme.onSurfaceVariant

                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                    ) {
                        drawLine(
                            color = lineColor,
                            start = Offset.Zero,
                            end = Offset(size.width, 0f),
                            strokeWidth = 3f
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillParentMaxWidth()
                    ) {
                        Text(
                            text = "Logged in as ${uiState.voter.fullName}",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .width(250.dp)
                                .padding(end = 20.dp),
                            textAlign = TextAlign.Start,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = null,
                            modifier = Modifier
                                .wrapContentSize()
                                .clickable(interactionSource = remember {
                                    MutableInteractionSource()
                                }, indication = rememberRipple(bounded = false)) {
                                    Firebase.auth.signOut()
                                },
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Voter ID: ${uiState.voter.voterId}",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Normal, fontSize = 10.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.wrapContentSize(),
                        textAlign = TextAlign.Start
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    Text(
                        text = "State of origin: ${uiState.voter.stateOfOrigin}",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Normal, fontSize = 10.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.wrapContentSize(),
                        textAlign = TextAlign.Start
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    Text(
                        text = "Local government: ${uiState.voter.localGovernment}",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Normal, fontSize = 10.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.wrapContentSize(),
                        textAlign = TextAlign.Start
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    Text(
                        text = "DOB: ${uiState.voter.dateOfBirth}",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Normal, fontSize = 10.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.wrapContentSize(),
                        textAlign = TextAlign.Start
                    )
                }
            }

        }

    }

    expandedCandidate?.let { candidate ->
        Dialog(
            onDismissRequest = { expandedCandidate = null },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .wrapContentHeight()
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(15.dp)
                    )
                    .padding(20.dp)
            ) {
                AsyncImage(
                    model = candidate.profileImageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(7.dp))

                Text(
                    candidate.fullName, fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    modifier = Modifier
                        .width(80.dp)
                        .wrapContentHeight(),
                    textAlign = TextAlign.Center,
                    lineHeight = 13.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    candidate.party.orEmpty(), fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    modifier = Modifier.wrapContentSize(),
                    textAlign = TextAlign.Center,
                    lineHeight = 13.sp
                )

                Spacer(modifier = Modifier.height(10.dp))


                Text(
                    candidate.briefSummary.orEmpty(), fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .wrapContentHeight(),
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )
            }
        }
    }

}

@Preview
@Composable
fun PreviewDashboardScreen() {
    ElectionAppTheme {
        DashboardScreen(
            finish = {},
            useDarkMode = false,
            toggleDarkMode = {},
            moveToVoteScreen = { _, _ -> },
            uiState = DashboardViewModel.UiState(
                voter = Voter(firstName = "Jayson Kardashian Silicon"),
                candidates = listOf(
                    Candidate(party = "ADC"),
                    Candidate(party = "PDP")
                )
            ),
            clearUiMessage = {}
        )
    }
}