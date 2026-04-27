package projson.classestest

import projson.annotation.Reference
import java.time.LocalDate

data class Course(
    val title: String,
    val startDate: LocalDate?,

    @property:Reference
    val prerequisites: List<Course>
)