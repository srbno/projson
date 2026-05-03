package projson.modelo

class JsonString(val value: String) : JsonValue {
    override fun accept(visitor: JsonVisitor) {
        visitor.visitString(this)
    }
    override fun toString(): String = toJsonString()
}