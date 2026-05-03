package projson.modelo

/**
 * Represents a JSON boolean value.
 *
 * @property value boolean value stored in the JSON model.
 */
class JsonBoolean(val value: Boolean) : JsonValue {
    /**
     * Accepts a [JsonVisitor] that performs an operation over this boolean value.
     *
     * @param visitor visitor to execute.
     */
    override fun accept(visitor: JsonVisitor) {
        visitor.visitBoolean(this)
    }

    /**
     * Renders this boolean value as JSON text.
     *
     * @return JSON representation of this boolean value.
     */
    override fun toString(): String = toJsonString()
}