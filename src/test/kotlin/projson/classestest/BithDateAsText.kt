package projson.classestest

import projson.JsonStringSerializer
import projson.annotation.JsonString

class BirthDateAsText : JsonStringSerializer<BirthDateText> {
    override fun serialize(value: BirthDateText): String {
        return "%02d/%02d/%04d".format(
            value.dayOfMonth,
            value.monthOfYear,
            value.yearValue
        )
    }
}

@JsonString(BirthDateAsText::class)
data class BirthDateText(
    val dayOfMonth: Int,
    val monthOfYear: Int,
    val yearValue: Int
)