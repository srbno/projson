package projson

import projson.annotation.Reference
import projson.modelo.*
import java.time.LocalDate
import kotlin.reflect.KProperty
import kotlin.reflect.full.hasAnnotation
import java.util.IdentityHashMap

private const val FIELD_ID = $$"$id"
private const val FIELD_REF = "\$ref"
private const val FIELD_TYPE = "\$type"

class ProJson {
    private val jsonObjectPorReferencias = IdentityHashMap<Any, JsonObject>()

    fun toJson(value: Any?): String = toJsonModel(value).toString()

    private fun toJsonModel(value: Any?): JsonValue {
        jsonObjectPorReferencias.clear()
        return serialize(value)
    }

    private fun serialize(value: Any?): JsonValue {
        if (value != null && jsonObjectPorReferencias.containsKey(value)) {
            return createReferencePointer(value)
        }

        return convertToJsonModel(value)
    }

    private fun convertToJsonModel(value: Any?): JsonValue {
        return when {
            value == null -> JsonNull()
            isPrimitiveOrString(value) -> convertPrimitive(value)
            value is Map<*, *> -> convertMap(value)
            value is Iterable<*> -> convertIterable(value)
            value is Array<*> -> convertIterable(value.asIterable())
            value is IntArray -> convertIterable(value.asIterable())
            value is LongArray -> convertIterable(value.asIterable())
            value is DoubleArray -> convertIterable(value.asIterable())
            value is FloatArray -> convertIterable(value.asIterable())
            value is BooleanArray -> convertIterable(value.asIterable())
            value is CharArray -> convertIterable(value.asIterable())
            value is ShortArray -> convertIterable(value.asIterable())
            value is ByteArray -> convertIterable(value.asIterable())
            shouldSerializeAsObject(value) -> convertObject(value)
            else -> throw IllegalArgumentException("Tipo não suportado: ${value::class.simpleName}")
        }
    }

    private fun createReferencePointer(instance: Any): JsonObject {
        val targetObject = jsonObjectPorReferencias[instance]
            ?: throw IllegalStateException("Não existe objeto registado para a referência")
        val referenceId = targetObject.getProperty(FIELD_ID) as JsonString
        return JsonObject().apply {
            setProperty(FIELD_REF, referenceId)
        }
    }

    private fun convertMap(entries: Map<*, *>): JsonObject {
        val jsonObject = JsonObject()
        for ((entryKey, entryValue) in entries) {
            jsonObject.setProperty(entryKey.toString(), serialize(entryValue))
        }
        return jsonObject
    }

    private fun convertObject(instance: Any): JsonObject {
        val reflectedType = instance::class
        val jsonObject = JsonObject()
        jsonObject.setProperty(FIELD_TYPE, JsonString(reflectedType.simpleName!!))

        val properties = reflectedType.members.filterIsInstance<KProperty<*>>()
        val propriedadesDiretas = properties.filterNot { it.hasAnnotation<Reference>() }
        val propriedadesPorReferencias = properties.filter { it.hasAnnotation<Reference>() }

        for (property in propriedadesDiretas) {
            jsonObject.setProperty(property.name, serialize(property.call(instance)))
        }

        if (propriedadesPorReferencias.isNotEmpty()) {
            jsonObject.setProperty(FIELD_ID, JsonString(createObjectId(instance)))
            jsonObjectPorReferencias[instance] = jsonObject
        }

        for (property in propriedadesPorReferencias) {
            val serializedReferences = JsonArray()
            val referencedValue = property.call(instance)

            if (referencedValue != null) {
                val serializedValue = serialize(referencedValue)

                if (serializedValue is JsonArray) {
                    serializedValue.elements.forEach { serializedReferences.add(it) }
                } else {
                    serializedReferences.add(serializedValue)
                }
            }

            jsonObject.setProperty(property.name, serializedReferences)
        }

        return jsonObject
    }

    private fun convertIterable(source: Iterable<*>): JsonArray {
        val jsonArray = JsonArray()
        source
            .map { serialize(it) }
            .forEach { jsonArray.add(it) }
        return jsonArray
    }

    private fun createObjectId(value: Any): String =
        System.identityHashCode(value).toString(16)

    private fun convertPrimitive(value: Any): JsonValue {
        return when (value) {
            is LocalDate -> JsonString(value.toString())
            is String -> JsonString(value)
            is Char -> JsonString(value.toString())
            is Number -> JsonNumber(value)
            is Boolean -> JsonBoolean(value)
            else -> JsonNull()
        }
    }

    private fun isPrimitiveOrString(value: Any?): Boolean {
        return value is String ||
                value is Number ||
                value is Boolean ||
                value is Char ||
                value is LocalDate
    }
    private fun shouldSerializeAsObject(value: Any): Boolean {
        return !isPrimitiveOrString(value) && value !is Map<*, *>
    }
}

