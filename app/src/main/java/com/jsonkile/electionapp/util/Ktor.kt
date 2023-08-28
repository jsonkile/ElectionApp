package com.jsonkile.electionapp.util

import com.jsonkile.electionapp.data.models.FastAPIErrorResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json

val ktorClient = HttpClient {
    install(ContentNegotiation) {
        json(contentType = ContentType.Any, json = kotlinx.serialization.json.Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }

    HttpResponseValidator {
        handleResponseExceptionWithRequest { exception, _ ->
            val clientException = exception as? ClientRequestException
                ?: return@handleResponseExceptionWithRequest
            throw Exception(clientException.message)
        }
    }
}

val fastAPIKtorClient = HttpClient {
    expectSuccess = true
    install(ContentNegotiation) {
        json(contentType = ContentType.Any, json = kotlinx.serialization.json.Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }

    HttpResponseValidator {
        handleResponseExceptionWithRequest { exception, _ ->
            val clientException =
                exception as? ClientRequestException ?: return@handleResponseExceptionWithRequest
            val exceptionResponse = clientException.response
            val error = exceptionResponse.body<FastAPIErrorResponse>()
            throw Exception(error.detail)
        }
    }
}