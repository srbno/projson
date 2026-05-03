package projson.modelo
/**
 * Represents a JSON string value.
 *
 * @property value raw string value stored in the JSON model. Escaping is applied when rendering.
 */
class JsonString(val value: String) : JsonValue {
    /**
     * Accepts a [JsonVisitor] that performs an operation over this string value.
     *
     * @param visitor visitor to execute.
     */
    override fun accept(visitor: JsonVisitor) {
        visitor.visitString(this)
    }

    /**
     * Renders this string value as escaped JSON text.
     *
     * @return JSON representation of this string value.
     */
    override fun toString(): String = toJsonString()
}