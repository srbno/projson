package projson.modelo

sealed interface JsonValue {
    fun accept(visitor: JsonVisitor)

    fun toJsonString(): String {
        val visitor = JsonStringVisitor()
        accept(visitor)
        return visitor.result()
    }
}