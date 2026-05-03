package projson.modelo

class JsonNull : JsonValue {
    override fun accept(visitor: JsonVisitor) {
        visitor.visitNull(this)
    }

    override fun toString(): String = toJsonString()
}
