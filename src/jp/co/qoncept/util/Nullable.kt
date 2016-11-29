package jp.co.qoncept.util

import jp.co.qoncept.util.Result.*

inline fun <T, R: Any> Result<T, Exception>.flatMap(transform: (T) -> R?): Result<R, Exception> {
    return when (this) {
        is Result.Success -> transform(value)?.let { Success<R, Exception>(it) } ?: Failure(KotlinNullPointerException())
        is Result.Failure -> Result.Failure(exception)
    }
}

fun <T, R> Result<T, Exception>.apply(transform: ((T) -> R)?): Result<R, Exception> {
    return when (this) {
        is Result.Success -> transform?.let { Success<R, Exception>(it(value)) } ?: Failure(KotlinNullPointerException())
        is Result.Failure -> Result.Failure(exception)
    }
}

fun <T: Any> Result<T?, Exception>.flatten(): Result<T, Exception> {
    return flatMap<T?, T> { it }
}

inline fun <T, E: Exception, ER: Exception> Result<T, E>.flatMapFailure(transform: (E) -> ER?): Result<T, Exception> {
    return when (this) {
        is Result.Success -> Result.Success(value)
        is Result.Failure -> transform(exception)?.let { Failure<T, Exception>(it) } ?: Failure(KotlinNullPointerException())
    }
}
