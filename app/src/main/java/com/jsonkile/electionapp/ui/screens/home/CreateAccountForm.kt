package com.jsonkile.electionapp.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jsonkile.electionapp.ui.components.PrimaryButton
import com.jsonkile.electionapp.ui.theme.ElectionAppTheme
import com.jsonkile.electionapp.ui.theme.Typography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAccountForm(backClick: () -> Unit, onCreateAccount: (String, String) -> Unit) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Create your e-voting account",
            modifier = Modifier
                .wrapContentSize()
                .padding(bottom = 15.dp),
            style = Typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp),
            color = MaterialTheme.colorScheme.onSurface
        )

        var voterId by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }

        TextField(
            value = voterId,
            onValueChange = { voterId = it },
            modifier = Modifier
                .wrapContentSize()
                .padding(vertical = 10.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            label = { Text(text = "Voter ID") })

        TextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier
                .wrapContentSize()
                .padding(vertical = 10.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            label = { Text(text = "Email address") }
        )

        TextField(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier
                .wrapContentSize()
                .padding(vertical = 10.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                capitalization = KeyboardCapitalization.None,
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Password
            ),
            visualTransformation = PasswordVisualTransformation(),
            colors = TextFieldDefaults.textFieldColors(),
            label = { Text(text = "Password") }
        )

        PrimaryButton(
            onClick = { onCreateAccount(email, password) },
            modifier = Modifier.padding(top = 15.dp),
            label = "Continue",
            enabled = email.isNotBlank() && password.isNotBlank()
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Go back",
            modifier = Modifier
                .background(shape = RectangleShape, color = Color.Transparent)
                .clickable {
                    backClick()
                }
                .padding(vertical = 5.dp),
            color = MaterialTheme.colorScheme.tertiary,
            fontWeight = FontWeight.Bold
        )
    }

}


@Preview
@Composable
fun PreviewCreateAccountForm() {
    ElectionAppTheme(useDarkTheme = false) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier) {
                CreateAccountForm(backClick = {}, onCreateAccount = { _, _ -> })
            }
        }
    }

}