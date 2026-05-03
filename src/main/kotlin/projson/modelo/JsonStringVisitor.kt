package projson.modelo


private const val QUOTE = "\""
private const val COMMA = ","

class JsonStringVisitor : JsonVisitor {
    private val builder = StringBuilder()

    fun result(): String = builder.toString()

    override fun visitNull(value: JsonNull) {
        builder.append("null")
    }

    override fun visitString(value: JsonString) {
        builder.append(QUOTE)
            .append(value.value.escapeJsonString())
            .append(QUOTE)
    }

    override fun visitNumber(value: JsonNumber) {
        builder.append(value.value)
    }

    override fun visitBoolean(value: JsonBoolean) {
        builder.append(value.value)
    }

    override fun visitArray(value: JsonArray) {
        builder.append("[")
        val items = value.elements.iterator()
        while (items.hasNext()) {
            items.next().accept(this)
            if (items.hasNext()) {
                builder.append(COMMA)
            }
        }
        builder.append("]")
    }

    override fun visitObject(value: JsonObject) {
        builder.append("{")
        val fields = value.properties.toSortedMap().iterator()
        while (fields.hasNext()) {
            val field = fields.next()
            builder.append(QUOTE)
                .append(field.key.escapeJsonString())
                .append("\":")
            field.value.accept(this)
            if (fields.hasNext()) {
                builder.append(COMMA)
            }
        }
        builder.append("}")
    }
}

private fun String.escapeJsonString(): String =
    buildString {
        for (ch in this@escapeJsonString) {
            when (ch) {
                '"' -> append("\\\"")
                '\\' -> append("\\\\")
                '\b' -> append("\\b")
                '\u000C' -> append("\\f")
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