package projson

import org.junit.jupiter.api.Test
import projson.modelo.JsonArray
import projson.modelo.JsonBoolean
import projson.modelo.JsonNull
import projson.modelo.JsonNumber
import projson.modelo.JsonObject
import projson.modelo.JsonString
import projson.modelo.JsonValue
import projson.modelo.JsonVisitor
import kotlin.test.assertEquals

class JsonVisitorExtensibilityTest {

    @Test
    fun `visitor tem apenas um metodo visit`() {
        val declared = JsonVisitor::class.java.declaredMethods.map { it.name }.toSet()
        assertEquals(setOf("visit"), declared)
    }

    @Test
    fun `visitor recebe cada valor uma vez`() {
        val seen = mutableListOf<String>()
        val visitor = object : JsonVisitor {
            override fun visit(value: JsonValue) {
                seen += value::class.simpleName!!
            }
        }
        JsonString("x").accept(visitor)
        JsonNumber(1).accept(visitor)
        JsonBoolean(true).accept(visitor)
        JsonNull().accept(visitor)
        JsonArray().accept(visitor)
        JsonObject().accept(visitor)

        assertEquals(
            listOf("JsonString","JsonNumber","JsonBoolean","JsonNull","JsonArray","JsonObject"),
            seen
        )
    }
}
