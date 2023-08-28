package com.jsonkile.electionapp.data.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FastAPIAddVoteResponseData(
    @SerialName("data")
    val `data`: Data? = Data(),
    @SerialName("message")
    val message: String? = "",
    @SerialName("status")
    val status: String? = ""
) {
    @Serializable
    data class Data(
        @SerialName("candidate_name")
        val candidateName: String? = "",
        @SerialName("voter_id")
        val voterId: String? = "",
        @SerialName("voter_name")
        val voterName: String? = ""
    )
}