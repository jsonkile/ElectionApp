package com.jsonkile.electionapp.ui.screens.vote

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jsonkile.electionapp.data.models.Candidate
import com.jsonkile.electionapp.data.models.Vote
import com.jsonkile.electionapp.util.ktorClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class VoteViewModel(stateHandle: SavedStateHandle) : ViewModel() {

    private val voterId: String = checkNotNull(stateHandle["voterId"])

    data class UiState(
        val isLoading: Boolean = false,
        val uiMessage: String? = null,
        val candidates: List<Candidate> = emptyList(),
        val hasVoted: Boolean = false
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    init {
        fetchCandidates()
        fetchVotingInfo(voterId)
    }

    fun fetchCandidates() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }

                val candidates =
                    ktorClient.get("https://raw.githubusercontent.com/jsonkile/evoting-app-host/main/candidates.json")
                        .body<List<Candidate>>()

                _uiState.update { it.copy(candidates = candidates) }

            } catch (e: Exception) {
                _uiState.update { it.copy(uiMessage = e.message) }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun castVote(party: String) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }

                val db = Firebase.firestore
                db.collection("votes").document(voterId).set(Vote(party = party)).await()

                fetchVotingInfo(voterId)

            } catch (e: Exception) {
                _uiState.update { it.copy(uiMessage = e.message) }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun fetchVotingInfo(voterId: String) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }

                val db = Firebase.firestore
                val document = db.collection("votes").document(voterId).get().await()
                _uiState.update { it.copy(hasVoted = document.exists()) }

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