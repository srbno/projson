package projson

import projson.annotation.Reference
import projson.modelo.*
import java.time.LocalDate
import kotlin.reflect.KProperty
import kotlin.reflect.full.hasAnnotation

private const val QUOTE = "\""

private const val COMMA = ","

class ProJson {
    private var documentRoot: JsonValue? = null

    fun toJson(value: Any): String {
        documentRoot = toJsonModel(value)
        return buildString {
            appendJsonNode(documentRoot, this)
        }
    }

    fun toJsonModel(value: Any?): JsonValue {
        return when {
            value == null -> JsonNull()
            isPrimitiveOrString(value) -> convertPrimitive(value)
            value is Map<*, *> -> convertMap(value)
            shouldSerializeAsObject(value) -> convertObject(value)
            else -> convertIterable(value)
        }
    }

    private fun convertMap(entries: Map<*, *>): JsonObject {
        val jsonObject = JsonObject()
        for ((entryKey, entryValue) in entries) {
            jsonObject.setProperty(entryKey.toString(), toJsonModel(entryValue))
        }
        return jsonObject
    }

    private fun convertObject(instance: Any): JsonObject {
        val reflectedType = instance::class
        val jsonObject = JsonObject()
        jsonObject.setProperty($$"$type", JsonString(reflectedType.simpleName!!))

        val properties = reflectedType.members.filterIsInstance<KProperty<*>>()
        val propriedadesDiretas = properties.filterNot { it.hasAnnotation<Reference>() }

        for (property in propriedadesDiretas) {
            jsonObject.setProperty(property.name, toJsonModel(property.call(instance)))
        }

        return jsonObject
    }

    private fun convertIterable(source: Any): JsonArray {
        val jsonArray = JsonArray()
        source.asIterableOrNull()
            ?.map { toJsonModel(it) }
            ?.forEach { jsonArray.add(it) }
        return jsonArray
    }

    private fun convertPrimitive(value: Any): JsonValue {
        return when (value) {
            is LocalDate -> JsonString(value.toString())
            is String -> JsonString(value)
            is Number -> JsonNumber(value)
            is Boolean -> JsonBoolean(value)
            else -> JsonNull()
        }
    }

    private fun appendJsonNode(node: JsonValue?, builder: StringBuilder) {
        when (node) {
            is JsonNull -> builder.append("null")
            is JsonString, is JsonNumber, is JsonBoolean -> appendInlineValue(node, builder)
            is JsonObject -> {
                builder.append("{")
                val fields = node.properties
                    .toSortedMap()
                    .iterator()
                while (fields.hasNext()) {
                    val field = fields.next()
                    builder.append("\"${field.key}\":")
                    appendInlineValue(field.value, builder)
                    if (fields.hasNext()) {
                        builder.append(COMMA)
                    }
                }
                builder.append("}")
            }
            is JsonArray -> {
                builder.append("[")
                val items = node.elements.iterator()
                while (items.hasNext()) {
                    appendJsonNode(items.next(), builder)
                    if (items.hasNext()) {
                        builder.append(COMMA)
                    }
                }
                builder.append("]")
            }
            null -> builder.append("null")
        }
    }

    fun isPrimitiveOrString(value: Any?): Boolean {
        return value is String ||
                value is Number ||
                value is Boolean ||
                value is Char ||
                value is LocalDate
    }

    private fun appendInlineValue(node: JsonValue, sink: StringBuilder) {
        when (node) {
            is JsonNull -> sink.append("null")
            is JsonString -> sink.append(QUOTE)
                .append(node.value.escapeJsonString())
                .append(QUOTE)
            is JsonNumber -> sink.append(node.value)
            is JsonBoolean -> sink.append(node.value)
            else -> appendJsonNode(node, sink)
        }
    }

    private fun shouldSerializeAsObject(value: Any): Boolean {
        return value !is Collection<*> &&
                value !is Array<*> &&
                value !is Iterable<*>
    }
}

fun Any.asIterableOrNull(): Iterable<*>? = when (this) {
    is Iterable<*> -> this
    is Array<*> -> this.asIterable()
    is IntArray -> this.asIterable()
    is LongArray -> this.asIterable()
    is DoubleArray -> this.asIterable()
    is FloatArray -> this.asIterable()
    is BooleanArray -> this.asIterable()
    is CharArray -> this.asIterable()
    is ShortArray -> this.asIterable()
    is ByteArray -> this.asIterable()
    else -> null
}

fun String.escapeJsonString(): String =
    buildString {
        for (ch in this@escapeJsonString) {
            when (ch) {
                '\"' -> append("\\\"")
                '\\' -> append("\\\\")
                '\b' -> append("\\b")
                '\u000C' -> append("\\f") // form feed
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
