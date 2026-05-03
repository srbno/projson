package projson.modelo

/**
 * Represents a JSON array, an ordered collection of [JsonValue] elements.
 *
 * Values added through the public API are converted into valid JSON model values through the same
 * conversion rules used by the model layer.
 */
class JsonArray : JsonValue {
    /**
     * Ordered elements stored by this array.
     */
    val elements = mutableListOf<JsonValue>()

    /**
     * Adds [element] to the end of the array.
     *
     * @param element value to add. It may be `null`, a primitive JSON value or an existing [JsonValue].
     */
    fun add(element: Any?) {
        elements.add(element.asJsonValue())
    }

    /**
     * Returns the element stored at [index].
     *
     * @param index zero-based position to read.
     * @return JSON value stored at the requested position.
     */
    fun get(index: Int): JsonValue {
        return elements[index]
    }

    /**
     * Replaces the element stored at [index].
     *
     * @param index zero-based position to update.
     * @param element new value to store.
     */
    fun set(index: Int, element: Any?) {
        elements[index] = element.asJsonValue()
    }

    /**
     * Removes the element stored at [index].
     *
     * @param index zero-based position to remove.
     * @return removed JSON value.
     */
    fun remove(index: Int): JsonValue {
        return elements.removeAt(index)
    }

    /**
     * Returns the number of elements currently stored in the array.
     *
     * @return array size.
     */
    fun size(): Int {
        return elements.size
    }

    /**
     * Checks whether the array has no elements.
     *
     * @return `true` when the array is empty, otherwise `false`.
     */
    fun isEmpty(): Boolean {
        return elements.isEmpty()
    }

    /**
     * Accepts a [JsonVisitor] that performs an operation over this array.
     *
     * @param visitor visitor to execute.
     */
    override fun accept(visitor: JsonVisitor) {
        visitor.visitArray(this)
    }

    /**
     * Renders this array as compact JSON text.
     *
     * @return JSON representation of this array.
     */
    override fun toString(): String = toJsonString()
}