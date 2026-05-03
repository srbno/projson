# ProJson

ProJson is a small Kotlin JSON library developed for the Advanced Programming project. It converts Kotlin values and objects into an in-memory JSON model and can then render that model as compact JSON text.

The library has two main use cases:

1. Convert Kotlin values directly into JSON text with `toJsonString`.
2. Convert Kotlin values into a manipulable JSON model with `toJson`.

---

## Features

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
- object references through `@Reference`
- property renaming through `@JsonProperty`
- ignored fields through `@JsonIgnore`
- custom string serialization through `@JsonString`
- JSON string escaping
- deterministic JSON object rendering by sorting property names

---

## Project structure

```text
src/main/kotlin/projson
├── ProJson.kt
├── JsonStringSerializer.kt
├── annotation
│   ├── JsonIgnore.kt
│   ├── JsonProperty.kt
│   ├── JsonString.kt
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
    ├── JsonNull.kt
    └── asJasonValue.kt
```

Package responsibilities:

- `projson`: main serializer and plugin contract.
- `projson.annotation`: annotations used to customize serialization.
- `projson.modelo`: in-memory JSON model and visitor-based rendering.

---

## Public API overview

The main class is:

```kotlin
class ProJson
```

It exposes two public methods:

```kotlin
fun toJson(value: Any?): JsonValue
fun toJsonString(value: Any?): String
```

Use `toJsonString` when you only need the final JSON text.

```kotlin
val json = ProJson().toJsonString(listOf("a", null, "b"))

println(json)
```

Output:

```json
["a",null,"b"]
```

Use `toJson` when you want to inspect or modify the JSON tree before rendering it.

```kotlin
val json = ProJson().toJson(mapOf("name" to "PA", "ects" to 6)) as JsonObject

json.setProperty("ects", 7)

println(json)
```

Output:

```json
{"ects":7,"name":"PA"}
```

---

## Serializing Kotlin objects

Kotlin objects are serialized through reflection. Regular objects receive a `$type` property with the Kotlin class name.

```kotlin
data class Track(
    val title: String,
    val duration: Double
)

val track = Track(
    title = "Great I Am",
    duration = 3.5
)

val json = ProJson().toJsonString(track)

println(json)
```

Output:

```json
{"$type":"Track","duration":3.5,"title":"Great I Am"}
```

Maps are serialized as JSON objects without `$type`.

```kotlin
val value = mapOf(
    "name" to "PA",
    "ects" to 6
)

println(ProJson().toJsonString(value))
```

Output:

```json
{"ects":6,"name":"PA"}
```

Lists, sets and arrays are serialized as JSON arrays.

```kotlin
println(ProJson().toJsonString(listOf("a", "b")))
println(ProJson().toJsonString(intArrayOf(1, 2, 3)))
```

Output:

```json
["a","b"]
[1,2,3]
```

---

## JSON model

The internal JSON model is based on the `JsonValue` interface.

```kotlin
sealed interface JsonValue
```

Concrete model classes:

- `JsonObject`
- `JsonArray`
- `JsonString`
- `JsonNumber`
- `JsonBoolean`
- `JsonNull`

Every model value can be rendered as JSON text with:

```kotlin
jsonValue.toJsonString()
```

or simply:

```kotlin
jsonValue.toString()
```

---

## Manipulating JSON objects

`JsonObject` allows reading, writing and removing properties.

```kotlin
val obj = JsonObject()

obj.setProperty("name", "Ana")
obj.setProperty("age", 20)

println(obj.getProperty("name"))

obj.setProperty("age", 21)
obj.removeProperty("name")

println(obj)
```

Output:

```json
{"age":21}
```

Useful methods:

```kotlin
fun setProperty(name: String, value: Any?)
fun getProperty(name: String): JsonValue?
fun removeProperty(name: String): JsonValue?
fun containsProperty(name: String): Boolean
fun propertyNames(): Set<String>
fun size(): Int
```

The manipulation API accepts `null`, primitive JSON-compatible values, `LocalDate` and existing `JsonValue` instances. To add complex Kotlin objects or lists, convert them first with `ProJson().toJson(...)`.

---

## Manipulating JSON arrays

`JsonArray` allows adding, reading, replacing and removing elements.

```kotlin
val array = JsonArray()

array.add("a")
array.add(null)
array.add("b")
array.set(1, "x")
array.remove(0)

println(array)
```

Output:

```json
["x","b"]
```

Useful methods:

```kotlin
fun add(element: Any?)
fun get(index: Int): JsonValue
fun set(index: Int, element: Any?)
fun remove(index: Int): JsonValue
fun size(): Int
fun isEmpty(): Boolean
```

---

## Object references with `@Reference`

Use `@Reference` when a property should hold references to other objects instead of embedding those objects directly.

```kotlin
import projson.annotation.Reference
import java.time.LocalDate

data class Course(
    val title: String,
    val startDate: LocalDate?,

    @property:Reference
    val prerequisites: List<Course>
)
```

Example:

```kotlin
val c1 = Course("Course 1", LocalDate.of(2026, 1, 1), emptyList())
val c2 = Course("Course 2", LocalDate.of(2026, 2, 2), emptyList())
val c3 = Course("Advanced", null, listOf(c1, c2))

val json = ProJson().toJsonString(listOf(c1, c2, c3))

println(json)
```

Objects that are referenceable receive a generated `$id`. Referenced objects are represented with `$ref`.

Example shape:

```json
[
  {
    "$id":"7c351808",
    "$type":"Course",
    "prerequisites":[],
    "startDate":"2026-01-01",
    "title":"Course 1"
  },
  {
    "$id":"5f1a2b10",
    "$type":"Course",
    "prerequisites":[],
    "startDate":"2026-02-02",
    "title":"Course 2"
  },
  {
    "$id":"3d4e9c21",
    "$type":"Course",
    "prerequisites":[
      {"$ref":"7c351808"},
      {"$ref":"5f1a2b10"}
    ],
    "startDate":null,
    "title":"Advanced"
  }
]
```

The current implementation generates identity-based ids with:

```kotlin
System.identityHashCode(value).toString(16)
```

This keeps object identity transparent to the library user. The client code does not create or manage ids manually.

---

## Renaming JSON properties with `@JsonProperty`

Use `@JsonProperty` to change the name used in JSON output.

```kotlin
import projson.annotation.JsonProperty

data class Task(
    @field:JsonProperty("desc")
    val description: String,

    @field:JsonProperty("deps")
    val dependencies: List<String>
)

val task = Task("T1", emptyList())

println(ProJson().toJsonString(task))
```

Output:

```json
{"$type":"Task","deps":[],"desc":"T1"}
```

---

## Ignoring fields with `@JsonIgnore`

Use `@JsonIgnore` to exclude a field from the generated JSON object.

```kotlin
import projson.annotation.JsonIgnore

data class User(
    val username: String,

    @field:JsonIgnore
    val password: String
)

val user = User("ana", "secret")

println(ProJson().toJsonString(user))
```

Output:

```json
{"$type":"User","username":"ana"}
```

---

## Serializing a class as a string with `@JsonString`

Use `@JsonString` when a class should be represented as a JSON string instead of a JSON object.

First, create a serializer by implementing `JsonStringSerializer<T>`.

```kotlin
import projson.JsonStringSerializer

class DateAsText : JsonStringSerializer<BirthDate> {
    override fun serialize(value: BirthDate): String {
        return "%02d/%02d/%04d".format(
            value.dayOfMonth,
            value.monthOfYear,
            value.yearValue
        )
    }
}
```

Then annotate the class.

```kotlin
import projson.annotation.JsonString

@JsonString(DateAsText::class)
data class BirthDate(
    val dayOfMonth: Int,
    val monthOfYear: Int,
    val yearValue: Int
)
```

Now instances of `BirthDate` are serialized as strings.

```kotlin
val dates = listOf(
    BirthDate(30, 2, 2026),
    BirthDate(31, 4, 2026)
)

println(ProJson().toJsonString(dates))
```

Output:

```json
["30/02/2026","31/04/2026"]
```

This also works when the annotated class appears as a property inside another object.

---

## Visitor pattern

JSON text rendering is implemented with the Visitor pattern.

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

`JsonStringVisitor` implements this interface and builds the final compact JSON string. This keeps rendering separate from the model classes and makes it possible to add other operations later, such as pretty printing or validation.

---

## Running tests

Run all tests with:

```bash
./gradlew test
```

On Windows:

```bash
gradlew.bat test
```

Relevant test files:

```text
src/test/kotlin/projson/ProJsonUnitTest.kt
src/test/kotlin/projson/JsonModelVisitorTest.kt
src/test/kotlin/projson/ProJsonTestCenario1.kt
src/test/kotlin/projson/ProJsonTestCenario2.kt
src/test/kotlin/projson/ProJsonTestCenario3.kt
```

The tests cover primitive serialization, object serialization, arrays, maps, references, JSON model manipulation, annotations, custom string serializers and visitor-based rendering.

---

## Building the JAR

Generate the library JAR with:

```bash
./gradlew jar
```

The generated file is placed in:

```text
build/libs/projson-1.0-SNAPSHOT.jar
```

---

## Using the JAR in another project

After building the JAR, copy it into the project where you want to use ProJson.

Example folder structure:

```text
my-app
├── libs
│   └── projson-1.0-SNAPSHOT.jar
└── src
    └── main
        └── kotlin
            └── Main.kt
```

Example usage:

```kotlin
import projson.ProJson

data class Person(
    val name: String,
    val age: Int
)

fun main() {
    val person = Person("Ana", 20)
    val json = ProJson().toJsonString(person)

    println(json)
}
```

If using Gradle, add the JAR as a file dependency:

```kotlin
dependencies {
    implementation(files("libs/projson-1.0-SNAPSHOT.jar"))
}
```

Because the project uses Kotlin reflection, the consuming project must also have the Kotlin runtime and reflection available through Gradle or through its execution classpath.

---

## Creating a GitHub release

A typical release process is:

```bash
./gradlew clean test jar
git tag v1.0.0
git push origin v1.0.0
```

Then create a new GitHub Release from the `v1.0.0` tag and attach the generated JAR from `build/libs`.

For a final release, it is recommended to change the Gradle version from a snapshot version to a stable version, for example:

```kotlin
version = "1.0.0"
```

---

## Developer notes

The most important internal class is `ProJson`. It is responsible for:

- detecting the Kotlin value type
- converting primitive values
- converting maps
- converting iterables and arrays
- converting Kotlin objects using reflection
- applying `@Reference`
- applying `@JsonProperty`
- applying `@JsonIgnore`
- applying `@JsonString`
- creating `$id` and `$ref` values

The JSON model classes are responsible for representing the JSON tree. Rendering is delegated to `JsonStringVisitor` through `JsonVisitor`.

When adding a new supported type, the main places to inspect are:

```kotlin
private fun isPrimitiveOrString(value: Any?): Boolean
private fun convertPrimitive(value: Any): JsonValue
private fun convertToJsonModel(value: Any?): JsonValue
```

When adding a new output format, create a new `JsonVisitor` implementation instead of changing all model classes.
