package com.rmp.data

import android.util.Log
import com.rmp.ui.appLogout
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.request
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.toMap
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

const val baseUrl = "https://api.rmp.dudosyka.ru"

@Serializable
open class AnyResponse

@Serializable
open class ApiException(
    val code: Int = 0,
    val status: String = "",
    override val message: String = "",
): Exception()

sealed class Result<out R> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: ApiException) : Result<Nothing>()
}

fun <T> Result<T>.successOr(fallback: T): T {
    return (this as? Result.Success<T>)?.data ?: fallback
}

fun <T> Result<T>.getIfException(): ApiException? {
    return (this as? Result.Error)?.exception
}

fun Result<*>.isSuccess(): Boolean {
    return this is Result.Success
}

@Serializable
class UnauthorizedException: ApiException(401, "Unauthorized", "Unauthorized")
class BadResponse: ApiException(0, "Bad response", "Bad response")


object ApiClient {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    enum class Method {
        GET, POST, DELETE, PATCH
    }

    suspend fun getAuthorizationData() =
        ApplicationDatabase!!.authTokenDao().getTokens().let {
            if (it.accessToken == "" || it.refreshToken == "") null else it
        }

    suspend fun updateAuthorizationData(token: TokenDto) =
        ApplicationDatabase!!.authTokenDao().saveTokens(token.accessToken, token.refreshToken)

    suspend fun execute(method: Method, url: String, builder: HttpRequestBuilder.() -> Unit): HttpResponse =
        when (method) {
            Method.GET -> {
                Log.d("API", "GET $url")
                client.get("$baseUrl/$url", builder)
            }
            Method.POST -> {
                Log.d("API", "POST $url")
                client.post("$baseUrl/$url", builder)
            }
            Method.DELETE -> {
                Log.d("API", "DELETE $url")
                client.delete("$baseUrl/$url", builder)
            }
            Method.PATCH -> {
                Log.d("API", "PATCH $url")
                client.patch("$baseUrl/$url", builder)
            }
        }

    suspend inline fun <reified T> response(resp: HttpResponse): Result<T> =
        try {
            Result.Success(resp.body())
        } catch (e: Exception) {
            Log.d("API", "Deserialization failed! ${resp.bodyAsText()}")
            Log.d("API", e.message.toString())
            Log.d("API", e.stackTraceToString())
            Result.Error(resp.body())
        }

    suspend inline fun <reified T> unauthorizedRequest(method: Method, url: String, data: Any? = null): Result<T> {
        val builder: HttpRequestBuilder.() -> Unit = {
            setBody(data)
            headers {
                set("Content-Type", "application/json")
            }
        }
        val resp = execute(method, url, builder)
        return if (resp.status.value == 200) response(resp)
        else Result.Error(resp.body())
    }

    suspend inline fun <reified T> refreshAndTryAgain(method: Method, url: String, data: Any? = null): Result<T> {
        val authorizationData = getAuthorizationData().let {
            if (it == null) {
                appLogout()
                return Result.Error(UnauthorizedException())
            } else it
        }

        val builder: HttpRequestBuilder.() -> Unit = {
            bearerAuth(authorizationData.refreshToken)
            headers {
                set("Content-Type", "application/json")
            }
        }

        val refreshed = execute(Method.POST, "auth/refresh", builder)

        Log.d("API", "Refresh req headers: ${refreshed.request.headers.toMap()} {${refreshed}")

        if (refreshed.status.value == 401) {
            updateAuthorizationData(TokenDto("", ""))
            appLogout()
            return Result.Error(UnauthorizedException())
        }
        if (refreshed.status.value != 200) {
            updateAuthorizationData(TokenDto("", ""))
            appLogout()
            return Result.Error(UnauthorizedException())
        }

        val tokenDto = refreshed.body<TokenDto>()

        Log.d("API", "Refreshed: $tokenDto")

        updateAuthorizationData(tokenDto)

        Log.d("API", "Retry request $method $url [token=${tokenDto.accessToken}]")
        val resp = execute(method, url) {
            setBody(data)
            headers {
                set("Content-Type", "application/json")
            }
            bearerAuth(tokenDto.accessToken)
        }

        Log.d("API", "Retry request headers: ${resp.request.headers.toMap()}")

        if (resp.status.value == 401) {
            updateAuthorizationData(TokenDto("", ""))
            appLogout()
            Log.d("API", "Unauthorized auto retry")
            return Result.Error(UnauthorizedException())
        }

        return response(resp)
    }

    suspend inline fun <reified T> authorizedRequest(method: Method, url: String, data: Any? = null): Result<T> {
        val authorizationData = getAuthorizationData().let {
            if (it == null) {
                appLogout()
                return Result.Error(UnauthorizedException())
            } else it
        }

        val builder: HttpRequestBuilder.() -> Unit = {
            setBody(data)
            Log.d("API", "Set data: $data")
            bearerAuth(authorizationData.accessToken)
            headers {
                set("Content-Type", "application/json")
            }
        }
        val resp = execute(method, url, builder)
        if (resp.status.value == 401) {
            Log.d("API", "Request failed due to unauthorized [token=${authorizationData.accessToken}]")
            return refreshAndTryAgain(method, url, data)
        }
        return response(resp)
    }
}