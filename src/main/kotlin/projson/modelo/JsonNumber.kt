package projson.modelo

/**
 * Represents a JSON number value.
 *
 * @property value numeric value stored in the JSON model.
 */
class JsonNumber(val value: Number) : JsonValue {
    /**
     * Accepts a [JsonVisitor] that performs an operation over this number value.
     *
     * @param visitor visitor to execute.
     */
    override fun accept(visitor: JsonVisitor) {
        visitor.visitNumber(this)
    }

    /**
     * Renders this number value as JSON text.
     *
     * @return JSON representation of this number value.
     */
    override fun toString(): String = toJsonString()
}
