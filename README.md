# ProJson

ProJson is a small Kotlin JSON serializer developed for the Advanced Programming project. It converts Kotlin values and objects into an internal JSON model and then renders that model as a JSON string.

The project is organised around three main ideas:

1. `ProJson` converts Kotlin values into JSON model objects.
2. The JSON model represents the JSON tree.
3. A Visitor renders the JSON tree into text through `toString()`.

---

## Main features

ProJson supports:

- `null`
- `String`
- `Char`
- `Number`
- `Boolean`
- `LocalDate`
- `Map<*, *>`
- `Iterable<*>`, including `List` and `Set`
- Kotlin arrays, such as `Array<*>`
- primitive arrays, such as `IntArray`, `BooleanArray`, `DoubleArray`, etc.
- Kotlin objects through reflection
- object references through the `@Reference` annotation
- JSON string escaping
- deterministic object field ordering when rendering JSON strings

---

## Project structure

```text
src/main/kotlin/projson
├── ProJson.kt
├── annotation
│   └── Reference.kt
└── modelo
    ├── JsonValue.kt
    ├── JsonVisitor.kt
    ├── JsonStringVisitor.kt
    ├── JsonObject.kt
    ├── JsonArray.kt
    ├── JsonString.kt
    ├── JsonNumber.kt
    ├── JsonBoolean.kt
    └── JsonNull.kt
```

The `projson` package contains the serializer.

The `projson.modelo` package contains the internal JSON model and the Visitor used to render JSON text.

The `projson.annotation` package contains annotations used by the serializer, especially `@Reference`.

---

## Public API

The main public class is:

```kotlin
class ProJson
```

The main method intended for users is:

```kotlin
fun toJson(value: Any?): String
```

It receives any supported Kotlin value and returns a JSON string.

Example:

```kotlin
val json = ProJson().toJson("hello")

println(json)
```

Output:

```json
"hello"
```

---

## Main methods

### `toJson`

```kotlin
fun toJson(value: Any?): String
```

This is the main entry point of the library. It converts a Kotlin value into a JSON string.

Example:

```kotlin
val json = ProJson().toJson(listOf(1, 2, 3))

println(json)
```

Output:

```json
[1,2,3]
```

---

### `toJsonModel`

```kotlin
private fun toJsonModel(value: Any?): JsonValue
```

This method starts the conversion from a Kotlin value into the internal JSON model.

It returns a `JsonValue`, which can be one of the model classes:

- `JsonObject`
- `JsonArray`
- `JsonString`
- `JsonNumber`
- `JsonBoolean`
- `JsonNull`

This method is private because users of the library should normally work with `toJson`.

---

### `serialize`

```kotlin
private fun serialize(value: Any?): JsonValue
```

This method controls the conversion flow before delegating to the normal type conversion.

Its responsibility is to check whether a value has already been registered as a referenceable object. If so, it returns a reference object instead of serializing the same object again.

Example of a generated reference:

```json
{"$ref":"7c351808"}
```

---

### `convertToJsonModel`

```kotlin
private fun convertToJsonModel(value: Any?): JsonValue
```

This method decides how each Kotlin value should be converted.

The conversion order is:

```kotlin
private fun convertToJsonModel(value: Any?): JsonValue {
    return when {
        value == null -> JsonNull()
        isPrimitiveOrString(value) -> convertPrimitive(value)
        value is Map<*, *> -> convertMap(value)
        value is Iterable<*> -> convertIterable(value)
        value is Array<*> -> convertIterable(value.asIterable())
        value is IntArray -> convertIterable(value.asIterable())
        value is LongArray -> convertIterable(value.asIterable())
        value is DoubleArray -> convertIterable(value.asIterable())
        value is FloatArray -> convertIterable(value.asIterable())
        value is BooleanArray -> convertIterable(value.asIterable())
        value is CharArray -> convertIterable(value.asIterable())
        value is ShortArray -> convertIterable(value.asIterable())
        value is ByteArray -> convertIterable(value.asIterable())
        shouldSerializeAsObject(value) -> convertObject(value)
        else -> throw IllegalArgumentException("Unsupported type: ${value::class.simpleName}")
    }
}
```

Arrays and primitive arrays are handled directly in this `when`, so no extra helper function is required for that decision.

---

### `convertPrimitive`

```kotlin
private fun convertPrimitive(value: Any): JsonValue
```

Converts simple Kotlin values into JSON model values.

Examples:

```kotlin
"hello"       -> JsonString("hello")
'a'           -> JsonString("a")
10            -> JsonNumber(10)
true          -> JsonBoolean(true)
LocalDate     -> JsonString("2026-01-01")
```

---

### `convertMap`

```kotlin
private fun convertMap(entries: Map<*, *>): JsonObject
```

Converts a Kotlin `Map` into a `JsonObject`.

Map keys are converted to strings and used as JSON property names.

Example:

```kotlin
val value = mapOf(
    "name" to "PA",
    "ects" to 6
)

val json = ProJson().toJson(value)
```

Output:

```json
{"ects":6,"name":"PA"}
```

---

### `convertIterable`

```kotlin
private fun convertIterable(source: Iterable<*>): JsonArray
```

Converts lists, sets, arrays and primitive arrays into a `JsonArray`.

Examples:

```kotlin
ProJson().toJson(listOf("a", "b"))
```

Output:

```json
["a","b"]
```

```kotlin
ProJson().toJson(intArrayOf(1, 2, 3))
```

Output:

```json
[1,2,3]
```

---

### `convertObject`

```kotlin
private fun convertObject(instance: Any): JsonObject
```

Converts a Kotlin object into a `JsonObject` using reflection.

The generated object includes a `$type` property with the name of the Kotlin class.

Example:

```kotlin
data class Track(
    val title: String,
    val duration: Double
)

val track = Track(
    title = "Great I Am",
    duration = 3.5
)

val json = ProJson().toJson(track)
```

Output:

```json
{"$type":"Track","duration":3.5,"title":"Great I Am"}
```

---

## Object references

ProJson supports references through the `@Reference` annotation.

A property annotated with `@Reference` is serialized as a list of references to other objects.

Example:

```kotlin
data class Course(
    val title: String,
    val startDate: LocalDate?,

    @property:Reference
    val prerequisites: List<Course>
)
```

When an object contains reference properties, ProJson assigns it an identity based id:

```kotlin
System.identityHashCode(value).toString(16)
```

This produces ids based on object identity instead of manual ids.

Example output:

```json
{
  "$id": "7c351808",
  "$type": "Course",
  "prerequisites": [],
  "startDate": null,
  "title": "PA"
}
```

If the same object instance is found again, it can be represented as:

```json
{"$ref":"7c351808"}
```

This allows the JSON output to preserve shared object references.

---

## JSON model

The serializer does not write JSON text directly from Kotlin objects. Instead, it first builds an internal JSON tree.

The common interface is:

```kotlin
sealed interface JsonValue {
    fun accept(visitor: JsonVisitor)

    fun toJsonString(): String {
        val visitor = JsonStringVisitor()
        accept(visitor)
        return visitor.result()
    }
}
```

Each JSON value implements `JsonValue`.

Examples:

```kotlin
class JsonString(val value: String) : JsonValue
class JsonNumber(val value: Number) : JsonValue
class JsonBoolean(val value: Boolean) : JsonValue
class JsonNull : JsonValue
class JsonArray : JsonValue
class JsonObject : JsonValue
```

---

## Visitor pattern

The conversion from JSON model to JSON text is handled by the Visitor pattern.

The Visitor interface is:

```kotlin
interface JsonVisitor {
    fun visitNull(value: JsonNull)
    fun visitString(value: JsonString)
    fun visitNumber(value: JsonNumber)
    fun visitBoolean(value: JsonBoolean)
    fun visitArray(value: JsonArray)
    fun visitObject(value: JsonObject)
}
```

`JsonStringVisitor` implements this interface and builds the final JSON string.

Each model delegates its `toString()` to `toJsonString()`:

```kotlin
override fun toString(): String = toJsonString()
```

This keeps the JSON rendering logic outside the model classes while still allowing model objects to be printed directly.

---

## User perspective

From the user's point of view, the library is simple to use.

Create a Kotlin value or object:

```kotlin
data class UserProfile(
    val fullName: String,
    val yearsOld: Int,
    val dateOfBirth: BirthDate?
)

data class BirthDate(
    val dayOfMonth: Int,
    val monthOfYear: Int,
    val yearValue: Int
)
```

Create an instance:

```kotlin
val user = UserProfile(
    fullName = "Alice Johnson",
    yearsOld = 30,
    dateOfBirth = BirthDate(
        dayOfMonth = 15,
        monthOfYear = 6,
        yearValue = 1995
    )
)
```

Serialize it:

```kotlin
val json = ProJson().toJson(user)

println(json)
```

Output:

```json
{"$type":"UserProfile","dateOfBirth":{"$type":"BirthDate","dayOfMonth":15,"monthOfYear":6,"yearValue":1995},"fullName":"Alice Johnson","yearsOld":30}
```

The user does not need to interact with the JSON model or the Visitor directly.

---

## Developer perspective

From the developer's point of view, the project is split into clear responsibilities.

### `ProJson`

Responsible for:

- detecting the Kotlin value type
- converting primitives
- converting maps
- converting iterables and arrays
- converting objects using reflection
- handling reference annotations
- creating `$id` and `$ref` values

### JSON model classes

Responsible for representing the JSON tree:

- `JsonObject` stores JSON properties
- `JsonArray` stores ordered elements
- `JsonString` stores string values
- `JsonNumber` stores number values
- `JsonBoolean` stores boolean values
- `JsonNull` represents JSON null

### Visitor classes

Responsible for rendering the JSON tree as text.

If the JSON output format needs to change, the main class to update is `JsonStringVisitor`.

If a new JSON operation is needed, such as pretty printing or validation, a new Visitor can be created without changing the model classes heavily.

---

## Testing

The project contains both unit tests and integration tests.

Run all tests with:

```bash
./gradlew test
```

On Windows:

```bash
gradlew.bat test
```

---

## Unit tests

Unit tests are focused on small and isolated behaviours.

Examples of tested behaviours:

- serializing `null`
- serializing strings
- escaping special characters in strings
- serializing numbers
- serializing booleans
- serializing chars as strings
- serializing `LocalDate` as string
- serializing object arrays
- serializing primitive arrays
- serializing lists
- serializing maps
- serializing Kotlin objects
- generating identity based references
- rendering JSON model objects through the Visitor

Relevant test files:

```text
src/test/kotlin/projson/ProJsonUnitTest.kt
src/test/kotlin/projson/JsonModelVisitorTest.kt
```

---

## Integration tests

Integration tests validate complete serialization scenarios with real object structures.

Relevant test files:

```text
src/test/kotlin/projson/ProJsonTestCenario1.kt
src/test/kotlin/projson/ProJsonTestCenario2.kt
src/test/kotlin/projson/ProJsonTestCenario3.kt
```

### Scenario 1

Serializes a user profile with a nested birth date object.

### Scenario 2

Serializes a list of courses with shared prerequisites and reference handling.

### Scenario 3

Serializes a playlist containing tracks, genres and attributes.

---

## Adding support for new types

To support a new Kotlin type, update the conversion logic in `ProJson`.

Usually this means updating:

```kotlin
private fun isPrimitiveOrString(value: Any?): Boolean
```

and:

```kotlin
private fun convertPrimitive(value: Any): JsonValue
```

For complex structures, update:

```kotlin
private fun convertToJsonModel(value: Any?): JsonValue
```

---

## Adding a new JSON output format

The current output format is implemented by:

```kotlin
JsonStringVisitor
```

To add a different output style, such as pretty printed JSON, create a new Visitor implementation.

Example idea:

```kotlin
class PrettyJsonStringVisitor : JsonVisitor
```

This keeps formatting separate from object conversion.

---

## Example outputs

### Null

```kotlin
ProJson().toJson(null)
```

```json
null
```

### String

```kotlin
ProJson().toJson("ola")
```

```json
"ola"
```

### List

```kotlin
ProJson().toJson(listOf("x", "y"))
```

```json
["x","y"]
```

### Map

```kotlin
ProJson().toJson(mapOf("a" to 1, "b" to true))
```

```json
{"a":1,"b":true}
```

### Kotlin object

```kotlin
data class Track(
    val title: String,
    val duration: Double
)

ProJson().toJson(Track("Great I Am", 3.5))
```

```json
{"$type":"Track","duration":3.5,"title":"Great I Am"}
```

---

## Summary

ProJson separates the serialization process into two stages:

1. Kotlin values are converted into an internal JSON model.
2. The JSON model is rendered into text using a Visitor.

This makes the project easier to test, easier to extend and easier to explain.
