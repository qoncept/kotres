package jp.co.qoncept.util

sealed class Result<out T, out E: Exception> {
    abstract val value: T?
    abstract val exception: E?

    class Success<T, E: Exception>(override val value: T): Result<T, E>() {
        override val exception: E? = null
    }

    class Failure<T, E: Exception>(override val exception: E): Result<T, E>() {
        override val value: T? = null
    }

    inline fun <R> map(transform: (T) -> R): Result<R, E> {
        return when (this) {
            is Success -> Success(transform(value))
            is Failure -> Failure(exception)
        }
    }
}

inline fun <T, R, E: Exception> Result<T, E>.flatMap(transform: (T) -> Result<R, E>): Result<R, E> {
   return when (this) {
       is Result.Success -> transform(value)
       is Result.Failure -> Result.Failure(exception)
   }
}

fun <T, R, E: Exception> Result<T, E>.apply(transform: Result<(T) -> R, E>): Result<R, E> {
    return when (this) {
        is Result.Success -> transform.map { it(value) }
        is Result.Failure -> Result.Failure(exception)
    }
}

fun <T, E: Exception> Result<Result<T, E>, E>.flatten(): Result<T, E> {
    return flatMap { it }
}
