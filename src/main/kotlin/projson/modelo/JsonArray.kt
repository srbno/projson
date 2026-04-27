package projson.modelo


class JsonArray : JsonValue {
    val elements = mutableListOf<JsonValue>()

    fun add(element: JsonValue) = elements.add(element)

    fun get(index: Int) = elements[index]

}