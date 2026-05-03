package projson

import org.junit.jupiter.api.Test
import projson.classestest.BirthDateText
import projson.classestest.Course
import projson.classestest.CourseWithJsonProperty
import projson.classestest.Track
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import projson.modelo.*

class ProJsonUnitTest {
    @Test
    fun `serializa null`() {
        assertEquals("null", ProJson().toJsonString(null))
    }

    @Test
    fun `serializa string`() {
        assertEquals("\"ola\"", ProJson().toJsonString("ola"))
    }

    @Test
    fun `serializa string com caracteres escapados`() {
        assertEquals("\"linha 1\\nlinha 2\"", ProJson().toJsonString("linha 1\nlinha 2"))
    }

    @Test
    fun `serializa numero`() {
        assertEquals("10", ProJson().toJsonString(10))
    }

    @Test
    fun `serializa boolean`() {
        assertEquals("true", ProJson().toJsonString(true))
    }

    @Test
    fun `serializa char como string`() {
        assertEquals("\"a\"", ProJson().toJsonString('a'))
    }

    @Test
    fun `serializa data como string`() {
        assertEquals("\"2026-01-01\"", ProJson().toJsonString(LocalDate.of(2026, 1, 1)))
    }

    @Test
    fun `serializa array de objetos`() {
        assertEquals("[\"a\",\"b\"]", ProJson().toJsonString(arrayOf("a", "b")))
    }

    @Test
    fun `serializa int array`() {
        assertEquals("[1,2,3]", ProJson().toJsonString(intArrayOf(1, 2, 3)))
    }

    @Test
    fun `serializa boolean array`() {
        assertEquals("[true,false]", ProJson().toJsonString(booleanArrayOf(true, false)))
    }

    @Test
    fun `serializa lista`() {
        assertEquals("[\"x\",\"y\"]", ProJson().toJsonString(listOf("x", "y")))
    }

    @Test
    fun `serializa map como json object`() {
        assertEquals("{\"a\":1,\"b\":true}", ProJson().toJsonString(mapOf("b" to true, "a" to 1)))
    }

    @Test
    fun `serializa objeto kotlin`() {
        val track = Track(title = "Great I Am", duration = 3.5)

        assertEquals(
            """{"${'$'}type":"Track","duration":3.5,"title":"Great I Am"}""",
            ProJson().toJsonString(track)
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
            ProJson().toJsonString(listOf(course, course))
        )
    }

    @Test
    fun `objetos iguais com instancias diferentes recebem ids diferentes`() {
        val first = Course("PA", null, emptyList())
        val second = Course("PA", null, emptyList())

        val firstId = System.identityHashCode(first).toString(16)
        val secondId = System.identityHashCode(second).toString(16)
        val json = ProJson().toJsonString(listOf(first, second))

        assertTrue(json.contains("\"${'$'}id\":\"$firstId\""))
        assertTrue(json.contains("\"${'$'}id\":\"$secondId\""))
        assertTrue(!json.contains("\"${'$'}ref\":\"$firstId\""))
    }

    @Test
    fun `toJson devolve modelo json manipulavel`() {
        val track = Track(title = "Great I Am", duration = 3.5)

        val json = ProJson().toJson(track) as JsonObject

        json.setProperty("duration", 4.0)

        assertEquals(
            """{"${'$'}type":"Track","duration":4.0,"title":"Great I Am"}""",
            json.toString()
        )
    }

    @Test
    fun `json object permite adicionar alterar ler e remover propriedades`() {
        val obj = JsonObject()

        obj.setProperty("name", "Ana")
        obj.setProperty("age", 20)

        assertEquals("\"Ana\"", obj.getProperty("name").toString())
        assertEquals("20", obj.getProperty("age").toString())

        obj.setProperty("age", 21)

        assertEquals("21", obj.getProperty("age").toString())
        assertTrue(obj.containsProperty("age"))

        obj.removeProperty("age")

        assertFalse(obj.containsProperty("age"))
        assertEquals(1, obj.size())
    }

    @Test
    fun `json array permite adicionar alterar ler e remover elementos`() {
        val array = JsonArray()

        array.add("a")
        array.add(null)
        array.add("b")

        assertEquals(3, array.size())
        assertEquals("\"a\"", array.get(0).toString())
        assertEquals("null", array.get(1).toString())

        array.set(1, "x")

        assertEquals("\"x\"", array.get(1).toString())

        array.remove(0)

        assertEquals(2, array.size())
        assertEquals("\"x\"", array.get(0).toString())
        assertEquals("\"b\"", array.get(1).toString())
    }

    @Test
    fun `JsonProperty e jsonIgnore`() {
        val course = CourseWithJsonProperty(
            description = "T1",
            deadline = "2026-04-30",
            dependencies = emptyList()
        )

        val json = ProJson().toJsonString(course)

        assertEquals(
            """{"${'$'}type":"CourseWithJsonProperty","deps":[],"desc":"T1"}""",
            json
        )
    }

    @Test
    fun `JsonString serializa objeto como string`() {
        val dates = listOf(
            BirthDateText(30, 2, 2026),
            BirthDateText(31, 4, 2026)
        )

        val json = ProJson().toJsonString(dates)

        assertEquals(
            """["30/02/2026","31/04/2026"]""",
            json
        )
    }

    @Test
    fun `serializa map com valores complexos`() {
        val value = mapOf(
            "name" to "playlist",
            "items" to listOf("a", "b"),
            "track" to Track("Song", 3.0),
            "extra" to null
        )

        assertEquals(
            """{"extra":null,"items":["a","b"],"name":"playlist","track":{"${'$'}type":"Track","duration":3.0,"title":"Song"}}""",
            ProJson().toJsonString(value)
        )
    }

    @Test
    fun `serializa string com escapes especiais`() {
        val text = "ola \"Ana\" \\ teste\tfim\rnovo"

        assertEquals(
            """"ola \"Ana\" \\ teste\tfim\rnovo"""",
            ProJson().toJsonString(text)
        )
    }

    @Test
    fun `serializa caracteres de controlo como unicode`() {
        val text = "a\u0001b"

        assertEquals(
            """"a\u0001b"""",
            ProJson().toJsonString(text)
        )
    }


}
