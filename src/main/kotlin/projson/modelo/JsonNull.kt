package projson.modelo

/**
 * Represents the JSON `null` value.
 */
class JsonNull : JsonValue {
    /**
     * Accepts a [JsonVisitor] that performs an operation over this null value.
     *
     * @param visitor visitor to execute.
     */
    override fun accept(visitor: JsonVisitor) {
        visitor.visitNull(this)
    }

    /**
     * Renders this value as the JSON literal `null`.
     *
     * @return JSON representation of this null value.
     */
    override fun toString(): String = toJsonString()
}
