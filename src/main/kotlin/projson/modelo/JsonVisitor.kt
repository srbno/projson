package projson.modelo

interface JsonVisitor {
    fun visitNull(value: JsonNull)
    fun visitString(value: JsonString)
    fun visitNumber(value: JsonNumber)
    fun visitBoolean(value: JsonBoolean)
    fun visitArray(value: JsonArray)
    fun visitObject(value: JsonObject)
}
