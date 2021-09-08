@file:Suppress("BlockingMethodInNonBlockingContext")

package com.fiore.wazirxticker.domain.core

import com.dropbox.android.external.store4.StoreResponse
import com.fiore.wazirxticker.utils.InternetUtils
import com.fiore.wazirxticker.utils.getErrorType
import com.fiore.wazirxticker.utils.getErrorTypeFromResponseCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
data class Request @Inject constructor(
    private val internetUtils: InternetUtils
) {
    @Suppress("unused")
    suspend fun <T, R> streamRequest(
        stream: Flow<StoreResponse<T>>,
        transform: (T) -> R,
        default: T
    ): Flow<Result<R>> {
        return flow {
            stream.collect { response ->
                when (response) {
                    is StoreResponse.Loading -> {
                        emit(Result.loading<R>())
                    }

                    is StoreResponse.Error -> {
                        if (internetUtils.isConnected()) {
                            emit(Result.error<R>(getErrorType(response.errorMessageOrNull() ?: "")))
                        } else {
                            emit(Result.error<R>(ResponseStatus.NETWORK_CONNECTION_ERROR))
                        }
                    }

                    is StoreResponse.Data -> {
                        val data: T = response.value
                        emit(Result.success(transform(data)))
                    }

                    is StoreResponse.NoNewData -> {
                        emit(Result.success(transform(default)))
                    }
                }
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun <T, R> normalRequest(
        call: Call<T>,
        transform: (T) -> R = { it as R },
        default: T,
        transformError: ((String) -> R)? = null
    ): Result<R> {

        return withContext(Dispatchers.IO) {
            try {
                val response: Response<T?> = call.execute()
                when (response.isSuccessful) {
                    true -> {
                        Result.success(transform((response.body() ?: default)))
                    }
                    false -> {
                        val errorResponse = response.errorBody()?.string()
                        if (errorResponse.isNullOrBlank())
                            Result.error(ResponseStatus.SERVER_ERROR)
                        else
                            Result.error(
                                getErrorTypeFromResponseCode(response.code()),
                                error = transformError?.let { it(errorResponse) }
                            )
                    }
                }
            } catch (exception: Throwable) {
                exception.printStackTrace()
                if (internetUtils.isConnected()) {
                    Result.error(ResponseStatus.SERVER_ERROR)
                } else {
                    Result.error(ResponseStatus.NETWORK_CONNECTION_ERROR)
                }
            }
        }
    }
}
