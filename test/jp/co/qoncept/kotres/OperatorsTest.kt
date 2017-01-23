package jp.co.qoncept.kotres

import org.testng.Assert.*
import org.testng.annotations.Test

import jp.co.qoncept.kotres.Result.*

class OperatorsTest {
    @Test
    fun mpAndAp() {
        run {
            val foo = curry(::Foo) mp Success<Int, AnimalException>(42) ap Success<Boolean, DogException>(true) ap Success<String, CatException>("ABC")
            assertEquals(foo.value?.i, 42)
            assertEquals(foo.value?.b, true)
            assertEquals(foo.value?.s, "ABC")
        }

        run {
            val e = AnimalException()
            val foo = curry(::Foo) mp Failure<Int, AnimalException>(e) ap Success<Boolean, DogException>(true) ap Success<String, CatException>("ABC")
            assertEquals(foo.exception, e)
        }

        run {
            val e = DogException()
            val foo = curry(::Foo) mp Success<Int, AnimalException>(42) ap Failure<Boolean, DogException>(e) ap Success<String, CatException>("ABC")
            assertEquals(foo.exception, e)
        }

        run {
            val e = CatException()
            val foo = curry(::Foo) mp Success<Int, AnimalException>(42) ap Success<Boolean, DogException>(true) ap Failure<String, CatException>(e)
            assertEquals(foo.exception, e)
        }

        run {
            val e1 = AnimalException()
            val e2 = DogException()
            val e3 = CatException()
            val foo = curry(::Foo) mp Failure<Int, AnimalException>(e1) ap Failure<Boolean, DogException>(e2) ap Failure<String, CatException>(e3)
            assertEquals(foo.exception, e3)
        }

        run {
            val e1 = AnimalException()
            val e2 = DogException()
            val foo = curry(::Foo) mp Failure<Int, AnimalException>(e1) ap Failure<Boolean, DogException>(e2) ap Success<String, CatException>("ABC")
            assertEquals(foo.exception, e2)
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

private data class Foo(val i: Int, val b: Boolean, val s: String)

private fun <A, B, C, Z> curry(f: (A, B, C) -> Z): (A) -> (B) -> (C) -> Z {
    return { a -> { b -> { c -> f(a, b, c) } } }
}
