package projson

import org.junit.jupiter.api.Test
import projson.classestest.BirthDate
import projson.classestest.UserProfile
import kotlin.test.assertEquals

class ProJsonTestCenario1 {
    @Test
    fun testUserProfileCase() {
        val user = UserProfile(
            fullName = "Alice Johnson",
            yearsOld = 30,
            dateOfBirth = BirthDate(
                dayOfMonth = 15,
                monthOfYear = 6,
                yearValue = 1995
            )
        )

        val json = ProJson().toJsonObject(user)

        assertEquals(
            $$"""{"$type":"UserProfile","dateOfBirth":{"$type":"BirthDate","dayOfMonth":15,"monthOfYear":6,"yearValue":1995},"fullName":"Alice Johnson","yearsOld":30}""",
            json
        )

    }
}


