package com.jsonkile.electionapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.jsonkile.electionapp.ui.theme.ElectionAppTheme

@Composable
fun ElectionWinnerComponent(imageUrl: String, modifier: Modifier) {
    Box {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            modifier = Modifier
                .size(70.dp)
                .clip(CircleShape)
                .background(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceVariant
                )
                .border(
                    width = 2.dp,
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary
                ),
            contentScale = ContentScale.Crop
        )

        Icon(
            imageVector = Icons.Default.Verified,
            contentDescription = "verified",
            modifier = Modifier.align(Alignment.BottomEnd).offset(x = 5.dp, y = 3.dp),
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Preview
@Composable
fun PreviewElectionWinnerComponent() {
    ElectionAppTheme {
        ElectionWinnerComponent("", Modifier)
    }
}