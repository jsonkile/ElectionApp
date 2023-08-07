package com.jsonkile.electionapp.ui.screens.dashboard

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jsonkile.electionapp.ui.components.PrimaryButton
import com.jsonkile.electionapp.ui.theme.ElectionAppTheme
import com.jsonkile.electionapp.util.mockCandidates
import com.jsonkile.electionapp.util.mockNews

@Composable
fun DashboardScreen(
    finish: () -> Unit,
    useDarkMode: Boolean,
    toggleDarkMode: (Boolean) -> Unit,
    moveToVoteScreen: () -> Unit
) {
    BackHandler(enabled = true) {
        finish()
    }

    //set to false
    var visible by remember { mutableStateOf(true) }

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

            //start voting
            item {
                Text(
                    text = "Cast your vote",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 19.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    "Securely and quickly have your say from the comfort of your home",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 13.sp,
                    lineHeight = 15.sp
                )

            }

            item {

                PrimaryButton(
                    onClick = { moveToVoteScreen() },
                    label = "Vote now"
                )

            }

            //candidates
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

                    mockCandidates.forEach { candidate ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.wrapContentHeight()
                        ) {
                            AsyncImage(
                                model = candidate.image,
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
                                candidate.name, fontSize = 11.sp,
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
                                candidate.party, fontSize = 10.sp,
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

            //press
            item {

                Spacer(modifier = Modifier.height(13.dp))

                Text(
                    text = "INEC Press",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 19.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
            }

            items(items = mockNews) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { }
                ) {
                    AsyncImage(
                        model = it.imageUrl,
                        contentDescription = "headline image",
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(3 / 2f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(10.dp)
                            ),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(3f)) {
                        Text(
                            it.header,
                            maxLines = 2,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(
                            it.dateAdded,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            //auth
            item {
                PrimaryButton(
                    onClick = { Firebase.auth.signOut() },
                    label = "Logout"
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
            moveToVoteScreen = {})
    }
}