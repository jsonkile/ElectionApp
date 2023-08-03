package com.jsonkile.electionapp.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
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

    fun createAccount(emailAddress: String, password: String) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                auth.createUserWithEmailAndPassword(emailAddress, password).await()
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