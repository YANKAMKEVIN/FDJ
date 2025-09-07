package com.kev.domain.util

/**
 * Simple sealed class to represent operation outcomes across layers.
 *
 * @param T the type of successful data
 */
sealed class Result<out T> {
    /**
     * Represents a successful outcome with a value.
     */
    data class Success<T>(val value: T) : Result<T>()

    /**
     * Represents a failure with an error type and the original throwable.
     */
    data class Failure(
        val error: DomainError,
        val throwable: Throwable? = null
    ) : Result<Nothing>()
}
