package jp.co.qoncept.util

import org.testng.Assert.*
import org.testng.annotations.Test

import jp.co.qoncept.util.Result.*

class ResultTest {
    @Test
    fun map() {
        run {
            val a = Success<Int, AnimalException>(3)
            val square = a.map { it * it }
            assertEquals(square.value, 9)
        }

        run {
            val s = Success<String, AnimalException>("42")
            val n: Result<Int, AnimalException> = s.map { it.toInt() }
            assertEquals(n.value, 42)
        }

        run {
            val a = Failure<Int, AnimalException>(AnimalException())
            val square = a.map { it * it }
            assertEquals(square.exception, a.exception)
        }
    }

    @Test
    fun flatMap() {
        run { // Same error types
            val a = Success<Int, AnimalException>(2)
            val b = Success<Int, AnimalException>(3)
            val sum = a.flatMap { a -> b.map { b -> a + b } }
            assertEquals(sum.value, 5)
        }

        run { // Different error types
            val a = Success<Int, DogException>(2)
            val b = Success<Int, CatException>(3)
            val sum = a.flatMap { a -> b.map { b -> a + b } }
            assertEquals(sum.value, 5)
        }

        run { // `a` is a `Failure`
            val a = Failure<Int, DogException>(DogException())
            val b = Success<Int, CatException>(3)
            val sum = a.flatMap { a -> b.map { b -> a + b } }
            assertEquals(sum.exception, a.exception)
        }

        run { // `b` is a `Failure`
            val a = Success<Int, DogException>(2)
            val b = Failure<Int, CatException>(CatException())
            val sum = a.flatMap { a -> b.map { b -> a + b } }
            assertEquals(sum.exception, b.exception)
        }

        run { // Both `a` and `b` are `Failure`s
            val a = Failure<Int, DogException>(DogException())
            val b = Failure<Int, CatException>(CatException())
            val sum = a.flatMap { a -> b.map { b -> a + b } }
            assertEquals(sum.exception, a.exception)
        }

        run {
            val s = Success<String, DogException>("42")
            val n = s.flatMap {
                try {
                    Success<Int, CatException>(it.toInt())
                } catch (e: NumberFormatException) {
                    Failure<Int, CatException>(CatException())
                }
            }
            assertEquals(n.value, 42)
        }
    }

    @Test
    fun apply() {
        run {
            val a = Success<Int, AnimalException>(3)
            val b = a.apply(Success<(Int) -> Int, AnimalException>({ it * it }))
            assertEquals(b.value, 9)
        }

        run {
            val a = Success<Int, DogException>(3)
            val b = a.apply(Success<(Int) -> Int, CatException>({ it * it }))
            assertEquals(b.value, 9)
        }

        run {
            val a = Failure<Int, DogException>(DogException())
            val b = a.apply(Success<(Int) -> Int, CatException>({ it * it }))
            assertEquals(b.exception, a.exception)
        }

        run {
            val a = Success<Int, DogException>(3)
            val b = a.apply(Failure<(Int) -> Int, CatException>(CatException()))
            assertTrue(b.exception is CatException)
        }

        run {
            val a = Failure<Int, DogException>(DogException())
            val b = a.apply(Failure<(Int) -> Int, CatException>(CatException()))
            assertEquals(b.exception, a.exception)
        }

        run {
            val a = Success<String, DogException>("42")
            val b = a.apply(Success<(String) -> Int, CatException>({ it.toInt() }))
            assertEquals(b.value, 42)
        }
    }

    @Test
    fun flatten() {
        run {
            val a: Result<Result<Int, AnimalException>, AnimalException> = Success(Success(42))
            val flattened = a.flatten()
            assertEquals(flattened.value, 42)
        }

        run {
            val a: Result<Result<Int, CatException>, DogException> = Success(Success(42))
            val flattened = a.flatten()
            assertEquals(flattened.value, 42)
        }

        run {
            val a: Result<Result<Int, CatException>, DogException> = Failure(DogException())
            val flattened = a.flatten()
            assertEquals(flattened.exception, a.exception)
        }

        run {
            val a: Result<Result<Int, CatException>, DogException> = Success(Failure(CatException()))
            val flattened = a.flatten()
            assertEquals(flattened.exception, a.value?.exception)
        }
    }

    @Test
    fun or() {
        run {
            val a = Success<Int, AnimalException>(42)
            val b = a or -1
            assertEquals(b, 42)
        }

        run {
            val a = Failure<Int, AnimalException>(AnimalException())
            val b = a or -1
            assertEquals(b, -1)
        }

        run {
            val a = Success<Int, AnimalException>(42)
            val b = a or Success(-1)
            assertEquals(b.value, 42)
        }

        run {
            val a = Failure<Int, AnimalException>(AnimalException())
            val b = a or Success(-1)
            assertEquals(b.value, -1)
        }

        run {
            val a = Failure<Int, DogException>(DogException())
            val b = a or Failure(CatException())
            assertTrue(b.exception is CatException)
        }

        // May be a bug of the compiler
        //run {
        //    val a = Success<Int, AnimalException>(42)
        //    val b = a or { -1 }
        //    assertEquals(b, 42)
        //}
        //
        //run {
        //    val a = Failure<Int, AnimalException>(AnimalException())
        //    val b = a or { -1 }
        //    assertEquals(b, -1)
        //}

        run {
            val a = Success<Int, AnimalException>(42)
            val b = a or { Success(-1) }
            assertEquals(b.value, 42)
        }

        run {
            val a = Failure<Int, AnimalException>(AnimalException())
            val b = a or { Success(-1) }
            assertEquals(b.value, -1)
        }

        run {
            val a = Failure<Int, DogException>(DogException())
            val b = a or { Failure<Int, CatException>(CatException()) }
            assertTrue(b.exception is CatException)
        }
    }
}

private open class AnimalException : Exception() {}
private class DogException : AnimalException() {}
private class CatException : AnimalException() {}
