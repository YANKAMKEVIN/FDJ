package com.kev.domain.util

/**
 * Error categories that can be surfaced to the domain/UI layer.
 *
 * This decouples app logic from raw exceptions (network, parsing, etc.).
 */
sealed class DomainError {
    object Network : DomainError()
    object NotFound : DomainError()
    object Unauthorized : DomainError()
    object Unknown : DomainError()
}
