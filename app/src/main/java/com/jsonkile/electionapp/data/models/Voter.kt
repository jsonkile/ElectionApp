package com.jsonkile.electionapp.data.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Voter(
    @SerialName("DateOfBirth")
    val dateOfBirth: String? = "",
    @SerialName("emailAddress")
    val emailAddress: String? = "",
    @SerialName("firstName")
    val firstName: String? = "",
    @SerialName("lastName")
    val lastName: String? = "",
    @SerialName("localGovernment")
    val localGovernment: String? = "",
    @SerialName("phoneNumber")
    val phoneNumber: String? = "",
    @SerialName("stateOfOrigin")
    val stateOfOrigin: String? = "",
    @SerialName("voterId")
    val voterId: String? = ""
) {
    val fullName = "$firstName $lastName"
}