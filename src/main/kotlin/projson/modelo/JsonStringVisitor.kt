package projson.modelo

private const val QUOTE = "\""
private const val COMMA = ","

/**
 * [JsonVisitor] implementation that renders a JSON model tree as compact JSON text.
 *
 * The visitor uses a single `visit` method and dispatches over the sealed [JsonValue] hierarchy
 * with an exhaustive `when`. The compiler enforces handling of every subtype, so adding a new
 * [JsonValue] subtype surfaces here at build time without changing [JsonVisitor].
 */
class JsonStringVisitor : JsonVisitor {
    private val builder = StringBuilder()

    fun result(): String = builder.toString()

    override fun visit(value: JsonValue) {
        when (value) {
            is JsonNull    -> builder.append("null")
            is JsonString  -> builder.append(QUOTE).append(value.value.escapeJsonString()).append(QUOTE)
            is JsonNumber  -> builder.append(value.value)
            is JsonBoolean -> builder.append(value.value)
            is JsonArray   -> renderArray(value)
            is JsonObject  -> renderObject(value)
        }
    }

    private fun renderArray(value: JsonArray) {
        builder.append("[")
        val items = value.elements.iterator()
        while (items.hasNext()) {
            items.next().accept(this)
            if (items.hasNext()) builder.append(COMMA)
        }
        builder.append("]")
    }

    private fun renderObject(value: JsonObject) {
        builder.append("{")
        val fields = value.properties.toSortedMap().iterator()
        while (fields.hasNext()) {
            val field = fields.next()
            builder.append(QUOTE).append(field.key.escapeJsonString()).append("\":")
            field.value.accept(this)
            if (fields.hasNext()) builder.append(COMMA)
        }
        builder.append("}")
    }
}

private fun String.escapeJsonString(): String {
    val original = this

    return buildString {
        for (ch in original) {
            when (ch) {
                '"' -> append("\\\"")
                '\\' -> append("\\\\")
                '\b' -> append("\\b")
                '' -> append("\\f")
                '\n' -> append("\\n")
                '\r' -> append("\\r")
                '\t' -> append("\\t")
                else -> {
                    if (ch < ' ') {
                        append("\\u%04x".format(ch.code))
                    } else {
                        append(ch)
                    }
                }
            }
        }
    }
}
