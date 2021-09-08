package com.fiore.wazirxticker.utils

import com.fiore.wazirxticker.domain.core.ResponseStatus

fun getErrorType(errorMsg: String): ResponseStatus {
    return try {
        when (errorMsg.substring(5, 8).toInt()) {
            400 -> ResponseStatus.Server_400_ERROR
            401 -> ResponseStatus.Server_401_ERROR
            403 -> ResponseStatus.Server_403_ERROR
            404 -> ResponseStatus.Server_404_ERROR
            else -> ResponseStatus.SERVER_ERROR
        }
    } catch (_: Exception) {
        ResponseStatus.SERVER_ERROR
    }
}

fun getErrorTypeFromResponseCode(code: Int): ResponseStatus {
    return when (code) {
        400 -> ResponseStatus.Server_400_ERROR
        401 -> ResponseStatus.Server_401_ERROR
        403 -> ResponseStatus.Server_403_ERROR
        404 -> ResponseStatus.Server_404_ERROR
        else -> ResponseStatus.SERVER_ERROR
    }
}