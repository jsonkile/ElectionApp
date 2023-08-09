package com.jsonkile.electionapp.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Vote(val party: String? = null)
