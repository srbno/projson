package projson.modelo

class JsonNumber(val value: Number) : JsonValue {
    override fun accept(visitor: JsonVisitor) {
        visitor.visitNumber(this)
    }

    override fun toString(): String = toJsonString()
}
