package com.jsonkile.electionapp.util

import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

fun FragmentActivity.voteWithFingerprint(party: String, onSuccess: () -> Unit) {
    val executor = ContextCompat.getMainExecutor(this)
    val biometricPrompt = BiometricPrompt(this, executor,
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(
                errorCode: Int,
                errString: CharSequence
            ) {
                super.onAuthenticationError(errorCode, errString)
                Toast.makeText(
                    applicationContext,
                    "Authentication error: $errString", Toast.LENGTH_SHORT
                )
                    .show()
            }

            override fun onAuthenticationSucceeded(
                result: BiometricPrompt.AuthenticationResult
            ) {
                super.onAuthenticationSucceeded(result)
                onSuccess()
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Toast.makeText(
                    applicationContext, "Authentication failed",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        })

    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle("Cast your vote")
        .setSubtitle("Vote for $party using your biometric credential")
        .setNegativeButtonText("Use account password")
        .setConfirmationRequired(true)
        .build()



    biometricPrompt.authenticate(promptInfo)
}