package projson.annotation

/**
 * Changes the JSON property name used for a Kotlin field.
 *
 * This annotation lets a Kotlin property keep its original name in code while being rendered with
 * a different property identifier in the generated JSON object.
 *
 * @property value name to use in the JSON output.
 */
@Retention(AnnotationRetention.RUNTIME)
@Target( AnnotationTarget.FIELD)
annotation class JsonProperty(
    val value: String
)
