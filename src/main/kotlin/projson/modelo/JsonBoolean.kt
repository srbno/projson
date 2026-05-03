package projson.modelo

class JsonBoolean(val value: Boolean) : JsonValue {
    override fun accept(visitor: JsonVisitor) {
        visitor.visitBoolean(this)
    }

    override fun toString(): String = toJsonString()
}