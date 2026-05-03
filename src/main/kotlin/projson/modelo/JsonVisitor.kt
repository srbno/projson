package projson.modelo

/**
 * Visitor contract for operations over the JSON model tree.
 *
 * Implementations receive a callback for each concrete [JsonValue] type. This makes it possible to
 * add operations such as string rendering or pretty printing without changing every model class.
 */
interface JsonVisitor {
    /**
     * Visits a JSON null value.
     *
     * @param value value being visited.
     */
    fun visitNull(value: JsonNull)

    /**
     * Visits a JSON string value.
     *
     * @param value value being visited.
     */
    fun visitString(value: JsonString)

    /**
     * Visits a JSON number value.
     *
     * @param value value being visited.
     */
    fun visitNumber(value: JsonNumber)

    /**
     * Visits a JSON boolean value.
     *
     * @param value value being visited.
     */
    fun visitBoolean(value: JsonBoolean)

    /**
     * Visits a JSON array value.
     *
     * @param value value being visited.
     */
    fun visitArray(value: JsonArray)

    /**
     * Visits a JSON object value.
     *
     * @param value value being visited.
     */
    fun visitObject(value: JsonObject)
}
