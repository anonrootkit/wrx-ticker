package com.fiore.wazirxticker.domain.core

import androidx.annotation.NonNull
import com.fiore.wazirxticker.domain.core.ResponseStatus.*

data class Result<T>(val status: ResponseStatus, val data: T?) {
    fun isError() = when(status) {
        NETWORK_CONNECTION_ERROR, Server_400_ERROR, Server_404_ERROR, SERVER_ERROR -> true else -> false
    }
    fun isLoading() = status == LOADING
    fun isSuccess() = status == SUCCESS

    companion object {

        /**
         * Helper method to create fresh state result
         */
        fun <T> success(@NonNull data: T): Result<T> {
            return Result(SUCCESS, data)
        }

        /**
         * Helper method to create error state Result. Error state might also have the current data, if any
         */
        fun <T> error(status : ResponseStatus, error: T? = null): Result<T> {
            return Result(status, error)
        }

        /**
         * Helper method to create loading state Result.
         */
        fun <T> loading(data: T? = null): Result<T> {
            return Result(LOADING, data)
        }
    }
}

