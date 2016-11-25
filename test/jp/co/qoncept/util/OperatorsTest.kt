package jp.co.qoncept.util

import org.testng.Assert.*
import org.testng.annotations.Test

import jp.co.qoncept.util.Result.*

class OperatorsTest {
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
