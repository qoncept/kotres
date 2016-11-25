package jp.co.qoncept.util

fun <T, R, E: Exception> ((T) -> R).mp(result: Result<T, E>): Result<R, E> {
    return result.map(this)
}

fun <T, R, E: Exception> Result<(T) -> R, E>.ap(result: Result<T, E>): Result<R, E> {
    return result.apply(this)
}

infix fun <T, E: Exception> Result<T, E>.or(alternative: T): T {
    return when (this) {
        is Result.Success -> value
        is Result.Failure -> alternative
    }
}

infix fun <T, E: Exception> Result<T, E>.or(alternative: Result<T, E>): Result<T, E> {
    return when (this) {
        is Result.Success -> this
        is Result.Failure -> alternative
    }
}
