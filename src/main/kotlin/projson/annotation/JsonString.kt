package projson.annotation

import projson.serializer.JsonStringSerializer
import kotlin.reflect.KClass

/**
 * Marks a class to be serialized as a JSON string through a custom serializer.
 *
 * When [projson.ProJson] finds this annotation on a class, it creates the configured [serializer]
 * and uses it to convert instances of that class into [projson.modelo.JsonString] values instead of
 * serializing their properties as regular JSON objects.
 *
 * @property serializer serializer class responsible for converting instances into strings.
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class JsonString(
    val serializer: KClass<out JsonStringSerializer<*>>
)
