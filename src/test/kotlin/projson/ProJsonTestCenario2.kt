package projson

import org.junit.jupiter.api.Test
import projson.classestest.Course
import java.time.LocalDate
import kotlin.test.assertEquals

class ProJsonTestCenario2 {
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


        val json = ProJson().toJson(allCourses)

        assertEquals(
            $$"""[{"$id":"c1","$type":"Course","prerequisites":[],"startDate":"2026-01-01","title":"Course 1"},{"$id":"c2","$type":"Course","prerequisites":[],"startDate":"2026-02-02","title":"Course 2"},{"$id":"c3","$type":"Course","prerequisites":[],"startDate":null,"title":"Fundamentals"},{"$id":"c4","$type":"Course","prerequisites":[{"$ref":"c3"},{"$ref":"c1"}],"startDate":null,"title":"Intermediate Concepts"},{"$id":"c5","$type":"Course","prerequisites":[{"$ref":"c1"},{"$ref":"c2"},{"$ref":"c3"}],"startDate":"2026-04-04","title":"Advanced Topics"},{"$id":"c6","$type":"Course","prerequisites":[{"$ref":"c4"},{"$ref":"c5"},{"$ref":"c3"}],"startDate":null,"title":"Master Program"}]""",
            json
        )
    }
}