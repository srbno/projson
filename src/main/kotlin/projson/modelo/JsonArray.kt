package projson.modelo


class JsonArray : JsonValue {
    val elements = mutableListOf<JsonValue>()

    fun add(element: JsonValue) = elements.add(element)

    fun get(index: Int) = elements[index]

    override fun accept(visitor: JsonVisitor) {
        visitor.visitArray(this)
    }

    override fun toString(): String = toJsonString()
}