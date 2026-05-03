package projson

import org.junit.jupiter.api.Test
import projson.classestest.Course
import projson.classestest.Track
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ProJsonUnitTest {
    @Test
    fun `serializa null`() {
        assertEquals("null", ProJson().toJson(null))
    }

    @Test
    fun `serializa string`() {
        assertEquals("\"ola\"", ProJson().toJson("ola"))
    }

    @Test
    fun `serializa string com caracteres escapados`() {
        assertEquals("\"linha 1\\nlinha 2\"", ProJson().toJson("linha 1\nlinha 2"))
    }

    @Test
    fun `serializa numero`() {
        assertEquals("10", ProJson().toJson(10))
    }

    @Test
    fun `serializa boolean`() {
        assertEquals("true", ProJson().toJson(true))
    }

    @Test
    fun `serializa char como string`() {
        assertEquals("\"a\"", ProJson().toJson('a'))
    }

    @Test
    fun `serializa data como string`() {
        assertEquals("\"2026-01-01\"", ProJson().toJson(LocalDate.of(2026, 1, 1)))
    }

    @Test
    fun `serializa array de objetos`() {
        assertEquals("[\"a\",\"b\"]", ProJson().toJson(arrayOf("a", "b")))
    }

    @Test
    fun `serializa int array`() {
        assertEquals("[1,2,3]", ProJson().toJson(intArrayOf(1, 2, 3)))
    }

    @Test
    fun `serializa boolean array`() {
        assertEquals("[true,false]", ProJson().toJson(booleanArrayOf(true, false)))
    }

    @Test
    fun `serializa lista`() {
        assertEquals("[\"x\",\"y\"]", ProJson().toJson(listOf("x", "y")))
    }

    @Test
    fun `serializa map como json object`() {
        assertEquals("{\"a\":1,\"b\":true}", ProJson().toJson(mapOf("b" to true, "a" to 1)))
    }

    @Test
    fun `serializa objeto kotlin`() {
        val track = Track(title = "Great I Am", duration = 3.5)

        assertEquals(
            """{"${'$'}type":"Track","duration":3.5,"title":"Great I Am"}""",
            ProJson().toJson(track)
        )
    }

    @Test
    fun `referencias usam identidade em memoria e nao ids manuais`() {
        val course = Course(
            title = "PA",
            startDate = null,
            prerequisites = emptyList()
        )

        val expectedId = System.identityHashCode(course).toString(16)

        assertEquals(
            """[{"${'$'}id":"$expectedId","${'$'}type":"Course","prerequisites":[],"startDate":null,"title":"PA"},{"${'$'}ref":"$expectedId"}]""",
            ProJson().toJson(listOf(course, course))
        )
    }

    @Test
    fun `objetos iguais com instancias diferentes recebem ids diferentes`() {
        val first = Course("PA", null, emptyList())
        val second = Course("PA", null, emptyList())

        val firstId = System.identityHashCode(first).toString(16)
        val secondId = System.identityHashCode(second).toString(16)
        val json = ProJson().toJson(listOf(first, second))

        assertTrue(json.contains("\"${'$'}id\":\"$firstId\""))
        assertTrue(json.contains("\"${'$'}id\":\"$secondId\""))
        assertTrue(!json.contains("\"${'$'}ref\":\"$firstId\""))
    }
}
