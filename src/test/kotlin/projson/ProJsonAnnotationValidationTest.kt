package projson

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import projson.classestest.WithReservedDollarId
import projson.classestest.WithReservedDollarRef
import projson.classestest.WithReservedDollarType
import projson.classestest.WithReservedId
import projson.classestest.WithReservedType
import kotlin.test.assertEquals

class ProJsonAnnotationValidationTest {

    @Test
    fun `JsonProperty com dollar id e rejeitado`() {
        val ex = assertThrows<IllegalArgumentException> {
            ProJson().toJsonString(WithReservedDollarId("x"))
        }
        assertEquals("Nome de propriedade reservado: \$id", ex.message)
    }

    @Test
    fun `JsonProperty com dollar ref e rejeitado`() {
        val ex = assertThrows<IllegalArgumentException> {
            ProJson().toJsonString(WithReservedDollarRef("x"))
        }
        assertEquals("Nome de propriedade reservado: \$ref", ex.message)
    }

    @Test
    fun `JsonProperty com dollar type e rejeitado`() {
        val ex = assertThrows<IllegalArgumentException> {
            ProJson().toJsonString(WithReservedDollarType("x"))
        }
        assertEquals("Nome de propriedade reservado: \$type", ex.message)
    }

    @Test
    fun `JsonProperty com id e rejeitado`() {
        val ex = assertThrows<IllegalArgumentException> {
            ProJson().toJsonString(WithReservedId("x"))
        }
        assertEquals("Nome de propriedade reservado: id", ex.message)
    }

    @Test
    fun `JsonProperty com type e rejeitado`() {
        val ex = assertThrows<IllegalArgumentException> {
            ProJson().toJsonString(WithReservedType("x"))
        }
        assertEquals("Nome de propriedade reservado: type", ex.message)
    }

    @Test
    fun `JsonProperty vazio e rejeitado`() {
        val ex = assertThrows<IllegalArgumentException> {
            ProJson().toJsonString(projson.classestest.WithEmptyJsonProperty("x"))
        }
        assertEquals("Nome de propriedade não pode ser vazio", ex.message)
    }

    @Test
    fun `JsonProperty so com espacos e rejeitado`() {
        val ex = assertThrows<IllegalArgumentException> {
            ProJson().toJsonString(projson.classestest.WithBlankJsonProperty("x"))
        }
        assertEquals("Nome de propriedade não pode ser vazio", ex.message)
    }
}
