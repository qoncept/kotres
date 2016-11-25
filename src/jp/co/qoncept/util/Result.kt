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

inline fun <T, R, ER: Exception, E1: ER, E2: ER> Result<T, E1>.flatMap(transform: (T) -> Result<R, E2>): Result<R, ER> {
   return when (this) {
       is Result.Success -> transform(value)
       is Result.Failure -> Result.Failure(exception)
   }
}

fun <T, R, ER: Exception, E1: ER, E2: ER> Result<T, E1>.apply(transform: Result<(T) -> R, E2>): Result<R, ER> {
    return when (this) {
        is Result.Success -> transform.map { it(value) }
        is Result.Failure -> Result.Failure(exception)
    }
}

fun <T, E: Exception, E1: E, E2: E> Result<Result<T, E2>, E1>.flatten(): Result<T, E> {
    return flatMap { it }
}

