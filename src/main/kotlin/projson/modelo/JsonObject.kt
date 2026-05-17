package projson.modelo

/**
 * Represents a JSON object, a collection of named [JsonValue] properties.
 *
 * Property values added through the public API are converted into valid JSON model values through
 * the same conversion rules used by the model layer.
 */
class JsonObject : JsonValue {

    /**
     * Properties stored by this object, indexed by their JSON property names.
     */
    val properties = mutableMapOf<String, JsonValue>()

    /**
     * Adds or replaces a property.
     *
     * @param name JSON property name.
     * @param value value to store. It may be `null`, a primitive JSON value or an existing [JsonValue].
     */
    fun setProperty(name: String, value: Any?) {
        properties[name] = value.asJsonValue()
    }

    /**
     * Reads a property by name.
     *
     * @param name JSON property name.
     * @return property value, or `null` when the property does not exist.
     */
    fun getProperty(name: String): JsonValue? {
        return properties[name]
    }

    /**
     * Removes a property by name.
     *
     * @param name JSON property name.
     * @return removed value, or `null` when the property did not exist.
     */
    fun removeProperty(name: String): JsonValue? {
        return properties.remove(name)
    }

    /**
     * Checks whether this object contains a property.
     *
     * @param name JSON property name.
     * @return `true` when the property exists, otherwise `false`.
     */
    fun containsProperty(name: String): Boolean {
        return properties.containsKey(name)
    }

    /**
     * Returns the set of property names currently stored in this object.
     *
     * @return property names.
     */
    fun propertyNames(): Set<String> {
        return properties.keys
    }

    /**
     * Returns the number of properties currently stored in this object.
     *
     * @return object size.
     */
    fun size(): Int {
        return properties.size
    }

    /**
     * Accepts a [JsonVisitor] that performs an operation over this object.
     *
     * @param visitor visitor to execute.
     */
    override fun accept(visitor: JsonVisitor) {
        visitor.visit(this)
    }

    /**
     * Renders this object as compact JSON text.
     *
     * @return JSON representation of this object.
     */
    override fun toString(): String = toJsonString()
}
