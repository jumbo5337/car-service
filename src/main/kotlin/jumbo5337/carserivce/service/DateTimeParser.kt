package jumbo5337.carserivce.service

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object DateTimeParser {

    fun parse(string: String) : LocalDateTime {
        var result : LocalDateTime? = null
        for ((reg, parser) in patterns) {
            if (reg.matches(string)) {
                result = kotlin.runCatching { parser(string) }.getOrNull()
                if (result != null) {
                    break
                }
            }
        }
        return result ?: error("Failed to parser $string as DateTime")
    }

    private val patterns = listOf<Pair<Regex, (String)-> LocalDateTime >>(
        "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3}".toRegex() to {
            val formater = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")
            LocalDateTime.parse(it, formater)
        },
        "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}".toRegex() to {
            val formater = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
            LocalDateTime.parse(it, formater)
        },
        "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}".toRegex() to {
            val formater = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
            LocalDateTime.parse(it, formater)
        },
        "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{3}".toRegex() to {
            val formater = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
            LocalDateTime.parse(it, formater)
        },
        "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}".toRegex() to {
            val formater = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            LocalDateTime.parse(it, formater)
        },
        "\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}".toRegex() to {
            val formater = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            LocalDateTime.parse(it, formater)
        },
        "\\d{4}-\\d{2}-\\d{2}".toRegex() to {
            val formater = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            LocalDate.parse(it, formater).atStartOfDay()
        },
        "\\d{4}/\\d{2}/\\d{2}".toRegex() to {
            val formater = DateTimeFormatter.ofPattern("yyyy/MM/dd")
            LocalDate.parse(it, formater).atStartOfDay()
        }
    )
}