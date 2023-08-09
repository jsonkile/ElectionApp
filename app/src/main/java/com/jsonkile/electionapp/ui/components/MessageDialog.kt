package com.jsonkile.electionapp.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.jsonkile.electionapp.ui.theme.ElectionAppTheme

@Composable
fun MessageDialog(onDismissRequest: () -> Unit, message: String) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 0.dp,
                pressedElevation = 0.dp,
                focusedElevation = 0.dp,
                disabledElevation = 0.dp
            ),
            modifier = Modifier
                .padding(bottom = 30.dp)
        ) {
            Text(
                message,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(20.dp),
                fontSize = 13.sp
            )
        }
    }
}

@Preview
@Composable
fun PreviewMessageDialog() {
    ElectionAppTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            MessageDialog(onDismissRequest = { /*TODO*/ }, message = "Heaven on earth!")
        }
    }
}