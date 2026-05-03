package projson.annotation
/**
 * Excludes a field from automatic JSON object generation.
 *
 * When a property backing field is annotated with `@JsonIgnore`, [projson.ProJson] does not include
 * that property in the generated [projson.modelo.JsonObject].
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class JsonIgnore()
