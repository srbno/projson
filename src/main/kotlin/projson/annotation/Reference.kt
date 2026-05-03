package projson.annotation

/**
 * Marks a property whose object values should be serialized as JSON references.
 *
 * Properties annotated with `@Reference` are not expanded as full objects inside the owning object.
 * Instead, their object values are represented with `$ref` objects that point to the `$id` generated
 * for the referenced instances.
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class Reference()
