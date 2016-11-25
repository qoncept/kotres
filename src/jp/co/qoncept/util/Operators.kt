package jp.co.qoncept.util

fun <T, R, E: Exception> ((T) -> R).mp(result: Result<T, E>): Result<R, E> {
    return result.map(this)
}

fun <T, R, E: Exception> Result<(T) -> R, E>.ap(result: Result<T, E>): Result<R, E> {
    return result.apply(this)
}
