package jp.co.qoncept.kotres

import org.testng.Assert.*
import org.testng.annotations.Test

import jp.co.qoncept.kotres.Result.*

class Sample {
    @Test
    fun sample() {
        /**/ val string = "3"
        /**/ val json = Json()

        fun parseInt(string: String): Result<Int, NumberFormatException> {
            return try {
                Success(Integer.parseInt(string))
            } catch (e: NumberFormatException) {
                Failure(e)
            }
        }

        val number: Result<Int, NumberFormatException> = parseInt(string)
        when (number) {
            is Success -> print(number.value)
            is Failure -> print(number.exception.message)
        }

        // Monadic methods
        val diameter: Result<Double, NumberFormatException> = number.map { 2 * Math.PI * it }
        val area: Result<Double, NumberFormatException> = diameter.flatMap { d -> number.map { r -> d * r / 2 } }

        // Operators
        data class Person(val firstName: String, val lastName: String, val age: Int)

        val firstName: Result<String, JsonException> = json["firstName"].string
        val lastName: Result<String, JsonException> = json["lastName"].string
        val age: Result<Int, JsonException> = json["age"].int

        val person: Result<Person, JsonException> = curry(::Person) mp firstName ap lastName ap age

        /**/ assertEquals(number.value!!, 3)
        /**/ assertEquals(diameter.value!!, 2 * Math.PI * 3, 1.0e-10)
        /**/ assertEquals(area.value!!, Math.PI * 3 * 3, 1.0e-10)
        /**/ assertEquals(person.value!!.firstName, "ABC")
        /**/ assertEquals(person.value!!.lastName, "ABC")
        /**/ assertEquals(person.value!!.age, 42)
    }

    @Test
    fun howToHandleDifferentTypesOfErrors() {
        // Upcasting to a common ancestor
        val a: Result<Int, DogException> = Success(2)
        val b: Result<Int, CatException> = Failure(CatException())
        val sum = a.flatMap { a -> b.map { b -> a + b } } // Result<Int, AnimalException>

        // Converting errors
        val c: Result<Int, FishException> = Success(3)
        val product: Result<Int, FishException> = a
                .mapFailure { e -> FishException(e) }
                .flatMap { a -> c.map { c -> a * c } }

        /**/ assertTrue(sum.exception!! is CatException)
        /**/ assertEquals(product.value!!, 6)
    }
}


private fun <A, B, C, Z> curry(f: (A, B, C) -> Z): (A) -> (B) -> (C) -> Z {
    return { a -> { b -> { c -> f(a, b, c) } } }
}

private class Json {
    operator fun get(key: String): Result<Json, JsonException> {
        return Success(this)
    }

    val int: Result<Int, JsonException> = Success(42)
    val string: Result<String, JsonException> = Success("ABC")
}

private val Result<Json, JsonException>.int: Result<Int, JsonException>
    get() = Success(42)

private val Result<Json, JsonException>.string: Result<String, JsonException>
    get() = Success("ABC")

private class JsonException : Exception()

private class FishException(cause: Exception) : Exception(cause)
