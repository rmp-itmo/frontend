package com.rmp.data

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json

const val baseUrl = "https://api.rmp.dudosyka.ru"

object ApiClient {
    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }
}