package projson.modelo

/**
 * Visitor contract for operations over the JSON model tree.
 *
 * Implementations receive a single callback for every [JsonValue]. Because [JsonValue] is sealed,
 * concrete visitors can dispatch with an exhaustive `when` on the value, and adding a new
 * [JsonValue] subtype does not require changing this interface.
 */
fun interface JsonVisitor {
    /**
     * Visits any [JsonValue] node.
     *
     * @param value value being visited.
     */
    fun visit(value: JsonValue)
}
