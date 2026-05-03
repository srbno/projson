package projson

import org.junit.jupiter.api.Test
import projson.modelo.JsonArray
import projson.modelo.JsonBoolean
import projson.modelo.JsonNull
import projson.modelo.JsonNumber
import projson.modelo.JsonObject
import projson.modelo.JsonString
import kotlin.test.assertEquals

class JsonModelVisitorTest {
    @Test
    fun `json string usa visitor no toString`() {
        assertEquals("\"texto\"", JsonString("texto").toString())
    }

    @Test
    fun `json number usa visitor no toString`() {
        assertEquals("5", JsonNumber(5).toString())
    }

    @Test
    fun `json boolean usa visitor no toString`() {
        assertEquals("false", JsonBoolean(false).toString())
    }

    @Test
    fun `json null usa visitor no toString`() {
        assertEquals("null", JsonNull().toString())
    }

    @Test
    fun `json array usa visitor no toString`() {
        val array = JsonArray().apply {
            add(JsonString("a"))
            add(JsonNumber(1))
        }

        assertEquals("[\"a\",1]", array.toString())
    }

    @Test
    fun `json object usa visitor no toString`() {
        val jsonObject = JsonObject().apply {
            setProperty("b", JsonBoolean(true))
            setProperty("a", JsonNumber(1))
        }

        assertEquals("{\"a\":1,\"b\":true}", jsonObject.toString())
    }
}
