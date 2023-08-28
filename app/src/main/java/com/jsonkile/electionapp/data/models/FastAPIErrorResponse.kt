package com.jsonkile.electionapp.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FastAPIErrorResponse(@SerialName("detail") val detail: String)