package projson

import projson.annotation.JsonProperty
import projson.annotation.Reference
import projson.annotation.JsonIgnore
import projson.annotation.JsonString as JsonStringAnnotation
import kotlin.reflect.full.createInstance
import projson.modelo.*
import projson.serializer.JsonStringSerializer
import java.time.LocalDate
import java.util.Collections
import kotlin.reflect.KProperty
import kotlin.reflect.full.hasAnnotation
import java.util.IdentityHashMap
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.javaField

private const val FIELD_ID = "\$id"
private const val FIELD_REF = "\$ref"
private const val FIELD_TYPE = "\$type"

private val RESERVED_JSON_PROPERTY_NAMES = setOf("\$id", "\$ref", "\$type", "id", "type")

/**
 * Main entry point for converting Kotlin values into the ProJson in-memory JSON model.
 *
 * The serializer supports primitive JSON values, strings, characters, [LocalDate], maps, iterables,
 * arrays, primitive arrays and Kotlin objects through reflection. Object generation can be customized
 * with annotations such as [Reference], [JsonProperty], [JsonIgnore] and
 * [projson.annotation.JsonString].
 *
 * The [toJson] method returns a mutable [JsonValue] model. The [toJsonString] method renders that
 * model directly as compact JSON text.
 */
class ProJson {
    private val jsonObjectPorReferencias = IdentityHashMap<Any, JsonObject>()
    private val objetosReferenciados: MutableSet<Any> = Collections.newSetFromMap(IdentityHashMap())

    /**
     * Converts [value] into the corresponding JSON model value.
     *
     * The returned [JsonValue] can be inspected, modified and rendered later with [JsonValue.toString]
     * or [JsonValue.toJsonString].
     *
     * @param value Kotlin value to convert.
     * @return JSON model representation of [value].
     */
    fun toJson(value: Any?): JsonValue = toJsonModel(value)

    /**
     * Converts [value] directly into compact JSON text.
     *
     * @param value Kotlin value to convert.
     * @return compact JSON string representing [value].
     */
    fun toJsonString(value: Any?): String = toJson(value).toString()
    private fun toJsonModel(value: Any?): JsonValue {
        jsonObjectPorReferencias.clear()
        objetosReferenciados.clear()
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
            hasJsonStringSerializer(value) -> convertWithJsonStringSerializer(value)
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
        return JsonObject().apply {
            setProperty(FIELD_REF, JsonString(createObjectId(instance)))
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

        val properties = reflectedType.members.filterIsInstance<KProperty<*>>().filterNot { it.isJsonIgnored() }
        val propriedadesDiretas = properties.filterNot { it.hasAnnotation<Reference>() }
        val propriedadesPorReferencias = properties.filter { it.hasAnnotation<Reference>() }

        for (property in propriedadesDiretas) {
            jsonObject.setProperty(property.jsonPropertyName(), serialize(property.call(instance)))
        }

        if (propriedadesPorReferencias.isNotEmpty() || objetosReferenciados.contains(instance)) {
            jsonObject.setProperty(FIELD_ID, JsonString(createObjectId(instance)))
            jsonObjectPorReferencias[instance] = jsonObject
        }

        for (property in propriedadesPorReferencias) {
            jsonObject.setProperty(
                property.jsonPropertyName(),
                serializeAsReference(property.call(instance))
            )
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
        return value.asJsonValue()
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

    private fun KProperty<*>.jsonPropertyName(): String {
        val annotated = findAnnotation<JsonProperty>()?.value
            ?: javaField?.getAnnotation(JsonProperty::class.java)?.value
            ?: return name

        if (annotated in RESERVED_JSON_PROPERTY_NAMES) {
            throw IllegalArgumentException("Nome de propriedade reservado: $annotated")
        }
        return annotated
    }

    private fun KProperty<*>.isJsonIgnored(): Boolean {
        return findAnnotation<JsonIgnore>() != null
                || javaField?.getAnnotation(JsonIgnore::class.java) != null
    }

    private fun hasJsonStringSerializer(value: Any): Boolean {
        return value::class.findAnnotation<JsonStringAnnotation>() != null
    }

    private fun convertWithJsonStringSerializer(value: Any): JsonValue {
        // TODO: Se value for vazio lançar exception
        val annotation = value::class.findAnnotation<JsonStringAnnotation>()
            ?: throw IllegalStateException("Serializer @JsonString não encontrado")

        val serializerInstance = annotation.serializer.objectInstance
            ?: annotation.serializer.createInstance()

        val serializer = serializerInstance as JsonStringSerializer<Any>

        return JsonString(serializer.serialize(value))
    }

    private fun serializeAsReference(value: Any?): JsonValue {
        return when {
            value == null -> JsonNull()

            value is Iterable<*> -> {
                val jsonArray = JsonArray()
                value.forEach {
                    jsonArray.add(serializeAsReference(it))
                }
                jsonArray
            }

            value is Array<*> -> {
                val jsonArray = JsonArray()
                value.forEach {
                    jsonArray.add(serializeAsReference(it))
                }
                jsonArray
            }

            shouldSerializeAsObject(value) -> {
                objetosReferenciados.add(value)
                createReferencePointer(value)
            }

            else -> serialize(value)
        }
    }
}
