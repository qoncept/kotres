# Kotres

_Kotres_ provides the `Result` class for type-safe error handling in Kotlin.

```kotlin
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

// Monadic methods (`map`, `flatMap`)
val diameter: Result<Double, NumberFormatException> = number.map { 2 * Math.PI * it }
val area: Result<Double, NumberFormatException> = diameter.flatMap { d -> number.map { r -> d * r / 2 } }

// Operators (`mp` as `<$>`, `ap` as `<*>`)
data class Person(val firstName: String, val lastName: String, val age: Int)

val firstName: Result<String, JsonException> = json["firstName"].string
val lastName: Result<String, JsonException> = json["lastName"].string
val age: Result<Int, JsonException> = json["age"].int

val person: Result<Person, JsonException> = curry(::Person) mp firstName ap lastName ap age
```

## How to handle different types of errors

```kotlin
// Upcasting to a common ancestor
val a: Result<Int, DogException> = Success(2)
val b: Result<Int, CatException> = Failure(CatException())
val sum = a.flatMap { a -> b.map { b -> a + b } } // Result<Int, AnimalException>

// Converting errors
val c: Result<Int, FishException> = Success(3)
val product: Result<Int, FishException> = a
        .mapFailure { e -> FishException(e) }
        .flatMap { a -> c.map { c -> a * c } }
```

## License

[The MIT License](LICENSE)
