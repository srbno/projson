package projson.modelo

import java.time.LocalDate

internal fun Any?.asJsonValue(): JsonValue {
    return when (this) {
        null -> JsonNull()
        is JsonValue -> this
        is LocalDate -> JsonString(this.toString())
        is String -> JsonString(this)
        is Char -> JsonString(this.toString())
        is Number -> JsonNumber(this)
        is Boolean -> JsonBoolean(this)
        else -> throw IllegalArgumentException("Só são aceites valores JSON válidos")
    }
}