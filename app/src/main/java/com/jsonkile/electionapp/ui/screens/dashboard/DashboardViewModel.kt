package com.jsonkile.electionapp.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class DashboardViewModel : ViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val isAuthenticated: Boolean = false,
        val uiMessage: String? = null
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()
}