package com.jsonkile.electionapp.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.snapshots
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.jsonkile.electionapp.data.models.Candidate
import com.jsonkile.electionapp.data.models.Headline
import com.jsonkile.electionapp.data.models.HeadlinesResultWrapper
import com.jsonkile.electionapp.data.models.Poll
import com.jsonkile.electionapp.data.models.Vote
import com.jsonkile.electionapp.data.models.Voter
import com.jsonkile.electionapp.util.ktorClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class DashboardViewModel : ViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val uiMessage: String? = null,
        val voter: Voter? = null,
        val candidates: List<Candidate> = emptyList(),
        val headlines: List<Headline> = emptyList(),
        val votersCount: Int = 0,
        val polls: List<Poll> = emptyList()
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    init {
        Firebase.remoteConfig.fetchAndActivate()

        fetchLoggedInVoterData()
        fetchCandidates()
        fetchHeadlines()
    }

    fun fetchLoggedInVoterData() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }

                val loggedInUserEmailAddress = Firebase.auth.currentUser?.email

                val voters =
                    ktorClient.get("https://raw.githubusercontent.com/jsonkile/evoting-app-host/main/voters.json")
                        .body<List<Voter>>()

                val voter =
                    voters.firstOrNull { voter -> voter.emailAddress == loggedInUserEmailAddress }

                requireNotNull(voter) { "The email address or voters id was not found in voters registration record. Please contact INEC." }

                _uiState.update { it.copy(voter = voter, votersCount = voters.size) }

            } catch (e: Exception) {
                _uiState.update { it.copy(uiMessage = e.message) }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
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

    fun fetchHeadlines() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }

                val headlines =
                    ktorClient.get("https://newsapi.org/v2/everything?pageSize=20&apiKey=8be0ef4b8253433e857342ba877641f8&q=\"The Independent National Electoral Commission\"")
                        .body<HeadlinesResultWrapper>().articles

                _uiState.update { it.copy(headlines = headlines.take(3)) }

            } catch (e: Exception) {
                _uiState.update { it.copy(uiMessage = e.message) }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun getPollsAsFlow() = Firebase.firestore.collection("votes").snapshots().mapLatest {
        val hashMapOfVotes = it.toObjects(Vote::class.java)
            .groupBy { vote -> vote.party }

        val polls =
            hashMapOfVotes.keys.map { key ->
                Poll(party = key!!, count = hashMapOfVotes[key]!!.size)
            }

        polls
    }

    fun clearUiMessage() {
        _uiState.update { it.copy(uiMessage = null) }
    }
}