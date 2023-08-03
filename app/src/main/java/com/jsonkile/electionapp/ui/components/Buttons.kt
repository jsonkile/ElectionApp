package com.jsonkile.electionapp.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jsonkile.electionapp.ui.theme.ElectionAppTheme
import com.jsonkile.electionapp.ui.theme.md_theme_light_tertiary
import com.jsonkile.electionapp.ui.theme.md_theme_light_tertiaryContainer

@Composable
fun PrimaryButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    label: String = "Action",
    loading: Boolean = false,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled && loading.not(),
        colors = ButtonDefaults.buttonColors(
            disabledContentColor = MaterialTheme.colorScheme.secondaryContainer,
            disabledContainerColor = MaterialTheme.colorScheme.secondary,
        ),
        modifier = modifier
    ) {
        Text(
            text = if (loading) "Loading..." else label,
            modifier = Modifier.padding(vertical = 6.dp),
            fontSize = 14.sp
        )
    }
}


@Composable
fun SecondaryButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    label: String = "Action",
    loading: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = loading.not(),
        colors = ButtonDefaults.buttonColors(
            disabledContentColor = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.8F),
            disabledContainerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.8F),
            containerColor = MaterialTheme.colorScheme.tertiary,
            contentColor = MaterialTheme.colorScheme.onTertiary
        ),
        modifier = modifier
    ) {
        Text(
            text = if (loading) "Loading..." else label,
            modifier = Modifier.padding(vertical = 6.dp),
            fontSize = 14.sp
        )
    }
}

@Preview
@Composable
fun PreviewPrimaryButton() {
    ElectionAppTheme {
        PrimaryButton(onClick = {})
    }
}


@Preview
@Composable
fun PreviewSecondaryButton() {
    ElectionAppTheme {
        SecondaryButton(onClick = {})
    }
}