@file:Suppress("EnumEntryName")

package com.fiore.wazirxticker.domain.core

enum class ResponseStatus {
    LOADING,
    SUCCESS,
    SERVER_ERROR,
    Server_400_ERROR,
    Server_401_ERROR,
    Server_403_ERROR,
    Server_404_ERROR,
    NETWORK_CONNECTION_ERROR
}

interface ErrorMessage{
    fun getErrorMessage() : String?
}