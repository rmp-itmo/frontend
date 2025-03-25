package com.rmp.data

import com.rmp.ui.appLogout
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable

const val baseUrl = "https://api.rmp.dudosyka.ru"

@Serializable
open class AnyResponse

@Serializable
open class ApiException(
    val httpStatusCode: String,
    val httpStatusMessage: String,
    override val message: String,
): Exception()

sealed class Result<out R> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: ApiException) : Result<Nothing>()
}

fun <T> Result<T>.successOr(fallback: T): T {
    return (this as? Result.Success<T>)?.data ?: fallback
}

fun <T> Result<T>.success(): T {
    return (this as? Result.Success<T>)!!.data
}

fun Result<*>.isSuccess(): Boolean {
    return this is Result.Success
}

@Serializable
class UnauthorizedException: ApiException(401.toString(), "Unauthorized", "Unauthorized")


object ApiClient {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
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
            Method.GET -> client.get("$baseUrl/$url")
            Method.POST -> client.post("$baseUrl/$url", builder)
            Method.DELETE -> client.delete("$baseUrl/$url", builder)
            Method.PATCH -> client.patch("$baseUrl/$url", builder)
        }

    suspend inline fun <reified T> unauthorizedRequest(method: Method, url: String, data: Any? = null): Result<T> {
        val builder: HttpRequestBuilder.() -> Unit = {
            setBody(data)
            headers {
                set("Content-Type", "application/json")
            }
        }
        val resp = execute(method, url, builder)
        return if (resp.status.value == 200) Result.Success(resp.body()) else Result.Error(resp.body())
    }

    suspend inline fun <reified T> refreshAndTryAgain(method: Method, url: String, data: Any? = null): Result<T> {
        val authorizationData = getAuthorizationData().let {
            if (it == null) {
                appLogout()
                return Result.Error(UnauthorizedException())
            } else it
        }

        val builder: HttpRequestBuilder.() -> Unit = {
            headers {
                set("Content-Type", "application/json")
                set("Authorization", "Bearer ${authorizationData.refreshToken}")
            }
        }
        val refreshed = execute(Method.POST, "refresh", builder)
        if (refreshed.status.value != 200)
            return Result.Error(UnauthorizedException())

        val tokenDto = refreshed.body<TokenDto>()

        updateAuthorizationData(tokenDto)

        val resp = execute(method, url) {
            setBody(data)
            headers {
                set("Content-Type", "application/json")
                set("Authorization", "Bearer ${tokenDto.accessToken}")
            }
        }
        if (resp.status.value == 401)
            return Result.Error(UnauthorizedException())

        return Result.Success(resp.body())
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
            headers {
                set("Content-Type", "application/json")
                set("Authorization", "Bearer ${authorizationData.accessToken}")
            }
        }
        val resp = execute(method, url, builder)
        if (resp.status.value == 401) {
            return refreshAndTryAgain(method, url, data)
        }
        return Result.Success(resp.body())
    }
}