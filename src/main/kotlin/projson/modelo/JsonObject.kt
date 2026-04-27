package projson.modelo

class JsonObject : JsonValue {
    val properties = mutableMapOf<String, JsonValue>()

    fun setProperty(name: String, value: JsonValue) {
        properties[name] = value
    }

    fun getProperty(name: String): JsonValue? = properties[name]

}