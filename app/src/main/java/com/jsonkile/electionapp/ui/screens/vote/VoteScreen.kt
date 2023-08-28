package com.jsonkile.electionapp.ui.screens.vote

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.jsonkile.electionapp.data.models.Candidate
import com.jsonkile.electionapp.ui.components.LoadingDialog
import com.jsonkile.electionapp.ui.components.MessageDialog
import com.jsonkile.electionapp.ui.components.PrimaryButton
import com.jsonkile.electionapp.ui.theme.ElectionAppTheme

@Composable
fun VoteScreen(
    onBack: () -> Unit,
    onAuth: (Candidate) -> Unit,
    uiState: VoteViewModel.UiState,
    clearUiMessage: () -> Unit
) {

    BackHandler {
        onBack()
    }

    if (uiState.hasVoted) MessageDialog(
        onDismissRequest = { onBack() },
        message = "Your vote has already been recorded. Thank you."
    )

    if (uiState.isLoading) LoadingDialog {}

    if (uiState.uiMessage.isNullOrBlank().not()) {
        MessageDialog(
            onDismissRequest = { clearUiMessage() },
            message = uiState.uiMessage.orEmpty()
        )
    }

    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {

        var selectedCandidate: Candidate? by remember { mutableStateOf(null) }

        Text(
            text = "Cast your vote",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Medium
            ),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            "Scroll through the candidates, pick a candidate and place your finger on the biometric icon to vote",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .wrapContentSize()
                .padding(horizontal = 50.dp),
            textAlign = TextAlign.Center,
            lineHeight = 17.sp
        )

        Box(
            modifier = Modifier
                .aspectRatio(1 / 1f)
                .padding(horizontal = 30.dp, vertical = 50.dp)
        ) {

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
                    .padding(horizontal = 30.dp)
                    .align(alignment = Alignment.TopStart),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                uiState.candidates.forEach { candidate ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .wrapContentHeight()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = rememberRipple(bounded = false),
                            ) {
                                selectedCandidate = candidate
                            }
                    ) {

                        val imageSize: Dp by animateDpAsState(if (selectedCandidate == candidate) 90.dp else 80.dp)
                        val imageBorderWidth: Dp by animateDpAsState(if (selectedCandidate == candidate) 5.dp else 0.dp)
                        val imageBorderColor: Color by animateColorAsState(if (selectedCandidate == candidate) MaterialTheme.colorScheme.primary else Color.Transparent)
                        val nameTextColor: Color by animateColorAsState(if (selectedCandidate == candidate) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)

                        AsyncImage(
                            model = candidate.profileImageUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .size(imageSize)
                                .clip(CircleShape)
                                .background(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.surfaceVariant
                                )
                                .border(
                                    width = imageBorderWidth,
                                    color = imageBorderColor,
                                    shape = CircleShape
                                ),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.height(7.dp))

                        Text(
                            candidate.fullName, fontSize = 13.sp,
                            color = nameTextColor,
                            maxLines = 2,
                            modifier = Modifier
                                .width(110.dp)
                                .requiredHeight(40.dp),
                            textAlign = TextAlign.Center,
                            lineHeight = 13.sp
                        )

                        Spacer(modifier = Modifier.height(1.dp))

                        Text(
                            candidate.party.orEmpty(), fontSize = 11.sp,
                            color = nameTextColor,
                            maxLines = 1,
                            modifier = Modifier.wrapContentSize(),
                            textAlign = TextAlign.Center,
                            lineHeight = 13.sp
                        )
                    }
                }

            }



            Icon(
                imageVector = Icons.Default.Fingerprint,
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .size(100.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple(bounded = false),
                        enabled = selectedCandidate != null
                    ) {
                        onAuth(selectedCandidate!!)
                    },
                tint = MaterialTheme.colorScheme.primary
            )
        }


        PrimaryButton(
            onClick = { onBack() },
            label = "Go back", modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
        )
    }

}

@Preview
@Composable
fun PreviewVoteScreen() {
    ElectionAppTheme() {
        VoteScreen(
            {},
            {},
            uiState = VoteViewModel.UiState(
                candidates = listOf(
                    Candidate(
                        firstName = "Ryan",
                        lastName = "Gosling",
                        profileImageUrl = "",
                        briefSummary = "Bad bitch"
                    ),
                    Candidate(
                        firstName = "Missy",
                        lastName = "Elliot",
                        profileImageUrl = "",
                        briefSummary = "Bad bitch"
                    )
                )
            ), clearUiMessage = {}
        )
    }
}