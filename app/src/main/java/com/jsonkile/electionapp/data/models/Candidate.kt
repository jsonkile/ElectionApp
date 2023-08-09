package com.jsonkile.electionapp.data.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Candidate(
    @SerialName("briefSummary")
    val briefSummary: String? = "",
    @SerialName("firstName")
    val firstName: String? = "",
    @SerialName("lastName")
    val lastName: String? = "",
    @SerialName("party")
    val party: String? = "",
    @SerialName("profileImageUrl")
    val profileImageUrl: String? = ""
) {

    val fullName = "$firstName $lastName"
}