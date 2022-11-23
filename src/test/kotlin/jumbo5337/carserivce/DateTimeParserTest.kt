package jumbo5337.carserivce

import jumbo5337.carserivce.service.DateTimeParser
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test


class DateTimeParserTest {


    @Test
    fun testParser() {
        val pattern1 = "2020-12-01T12:22:15.122"
        Assertions.assertNotNull(DateTimeParser.parse(pattern1))

        val pattern2 = "2020-12-01T12:22:15"
        Assertions.assertNotNull(DateTimeParser.parse(pattern2))

        val pattern3 = "2020-12-01T12:22"
        Assertions.assertNotNull(DateTimeParser.parse(pattern3))

        val pattern4 = "2020-12-01 12:22:15.122"
        Assertions.assertNotNull(DateTimeParser.parse(pattern4))

        val pattern5 = "2020-12-01 12:22:15"
        Assertions.assertNotNull(DateTimeParser.parse(pattern5))

        val pattern6 = "2020-12-01 12:22"
        Assertions.assertNotNull(DateTimeParser.parse(pattern6))

        val pattern7 = "2020-12-01"
        Assertions.assertNotNull(DateTimeParser.parse(pattern7))

        val pattern8 = "2020/12/01"
        Assertions.assertNotNull(DateTimeParser.parse(pattern6))
    }

}