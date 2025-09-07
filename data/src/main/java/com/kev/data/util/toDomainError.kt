package com.kev.data.util

import com.kev.domain.util.DomainError
import java.io.IOException
import retrofit2.HttpException

/**
 * Maps low-level exceptions to [DomainError] categories.
 */
fun Throwable.toDomainError(): DomainError = when (this) {
    is IOException -> DomainError.Network
    is HttpException -> when (code()) {
        401 -> DomainError.Unauthorized
        404 -> DomainError.NotFound
        else -> DomainError.Unknown
    }
    else -> DomainError.Unknown
}
