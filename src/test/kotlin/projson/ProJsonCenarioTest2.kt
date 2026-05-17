package projson

import org.junit.jupiter.api.Test
import projson.classestest.Course
import java.time.LocalDate
import kotlin.test.assertEquals

class ProJsonCenarioTest2 {
    @Test
    fun testCourses() {
        val c1 = Course(
            title = "Course 1",
            startDate = LocalDate.of(2026, 1, 1),
            prerequisites = emptyList()
        )

        val c2 = Course(
            title = "Course 2",
            startDate = LocalDate.of(2026, 2, 2),
            prerequisites = emptyList()
        )

        val shared = Course(
            title = "Fundamentals",
            startDate = null,
            prerequisites = emptyList()
        )

        val c3 = Course(
            title = "Intermediate Concepts",
            startDate = null,
            prerequisites = listOf(shared, c1)
        )

        val c4 = Course(
            title = "Advanced Topics",
            startDate = LocalDate.of(2026, 4, 4),
            prerequisites = listOf(c1, c2, shared)
        )

        val c5 = Course(
            title = "Master Program",
            startDate = null,
            prerequisites = listOf(c3, c4, shared)
        )

        val allCourses = listOf(c1, c2, shared, c3, c4, c5)

        val json = ProJson().toJsonString(allCourses)

        val id1 = System.identityHashCode(c1).toString(16)
        val id2 = System.identityHashCode(c2).toString(16)
        val sharedId = System.identityHashCode(shared).toString(16)
        val id3 = System.identityHashCode(c3).toString(16)
        val id4 = System.identityHashCode(c4).toString(16)
        val id5 = System.identityHashCode(c5).toString(16)

        assertEquals(
            """[{"${'$'}id":"$id1","${'$'}type":"Course","prerequisites":[],"startDate":"2026-01-01","title":"Course 1"},{"${'$'}id":"$id2","${'$'}type":"Course","prerequisites":[],"startDate":"2026-02-02","title":"Course 2"},{"${'$'}id":"$sharedId","${'$'}type":"Course","prerequisites":[],"startDate":null,"title":"Fundamentals"},{"${'$'}id":"$id3","${'$'}type":"Course","prerequisites":[{"${'$'}ref":"$sharedId"},{"${'$'}ref":"$id1"}],"startDate":null,"title":"Intermediate Concepts"},{"${'$'}id":"$id4","${'$'}type":"Course","prerequisites":[{"${'$'}ref":"$id1"},{"${'$'}ref":"$id2"},{"${'$'}ref":"$sharedId"}],"startDate":"2026-04-04","title":"Advanced Topics"},{"${'$'}id":"$id5","${'$'}type":"Course","prerequisites":[{"${'$'}ref":"$id3"},{"${'$'}ref":"$id4"},{"${'$'}ref":"$sharedId"}],"startDate":null,"title":"Master Program"}]""",
            json
        )
    }
}