package projson

/**
 * Contract used by [projson.annotation.JsonString] to convert Kotlin objects into JSON strings.
 *
 * A serializer implementation receives an object of type [T] and returns the textual value that
 * will be stored inside a JSON string. The returned value is still escaped by the JSON model when
 * the final JSON text is rendered.
 *
 * @param T type of object handled by this serializer.
 */
interface JsonStringSerializer<T : Any> {
    /**
     * Converts [value] into the string representation that should appear in JSON.
     *
     * @param value object to serialize.
     * @return string value to be used as the JSON string content.
     */
    fun serialize(value: T): String
}