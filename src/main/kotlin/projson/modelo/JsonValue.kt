package projson.modelo
/**
 * Common contract for every value in the in-memory JSON model.
 *
 * A [JsonValue] can represent any valid JSON node: object, array, string, number, boolean or null.
 * Implementations accept visitors so that operations such as rendering can be added without placing
 * all behaviour directly inside the model classes.
 */
sealed interface JsonValue {
    /**
     * Accepts a [JsonVisitor] that performs an operation over this value.
     *
     * @param visitor visitor to execute.
     */
    fun accept(visitor: JsonVisitor)

    /**
     * Renders this value as compact JSON text using [JsonStringVisitor].
     *
     * @return JSON representation of this value.
     */
    fun toJsonString(): String {
        val visitor = JsonStringVisitor()
        accept(visitor)
        return visitor.result()
    }
}