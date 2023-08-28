package com.jsonkile.electionapp.data.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FastAPIAddVoteRequestData(
    @SerialName("candidate_name")
    val candidateName: String? = "",
    @SerialName("voter_id")
    val voterId: String? = "",
    @SerialName("voter_name")
    val voterName: String? = ""
)