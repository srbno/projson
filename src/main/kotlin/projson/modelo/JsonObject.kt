package projson.modelo

class JsonObject : JsonValue {
    val properties = mutableMapOf<String, JsonValue>()

    fun setProperty(name: String, value: JsonValue) {
        properties[name] = value
    }

    fun getProperty(name: String): JsonValue? = properties[name]

    override fun accept(visitor: JsonVisitor) {
        visitor.visitObject(this)
    }

    override fun toString(): String = toJsonString()
}
