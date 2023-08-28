package com.jsonkile.electionapp.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jsonkile.electionapp.data.models.Voter
import com.jsonkile.electionapp.util.ktorClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class HomeViewModel : ViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val isAuthenticated: Boolean = false,
        val uiMessage: String? = null
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val auth = Firebase.auth

    fun createAccount(emailAddress: String, password: String, voterId: String) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }

                val voters =
                    ktorClient.get("https://firebasestorage.googleapis.com/v0/b/election-d2d38.appspot.com/o/voters.json?alt=media&token=9e2f2384-be8a-4bed-a43c-ec0cc53909bf")
                        .body<List<Voter>>()

                val voter =
                    voters.firstOrNull { voter -> voter.emailAddress == emailAddress && voter.voterId == voterId }

                requireNotNull(voter) { "The email address or voters id was not found in voters registration record. Please contact INEC." }

                auth.createUserWithEmailAndPassword(emailAddress.trim(), password).await()
                _uiState.update { it.copy(isAuthenticated = true) }

            } catch (e: Exception) {
                _uiState.update { it.copy(uiMessage = e.message) }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun login(emailAddress: String, password: String) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                auth.signInWithEmailAndPassword(emailAddress.trim(), password).await()
                _uiState.update { it.copy(isAuthenticated = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(uiMessage = e.message) }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun clearUiMessage() {
        _uiState.update { it.copy(uiMessage = null) }
    }

}