package pnu.plato.calendar.presentation.calendar.model

import androidx.compose.ui.graphics.Color
import pnu.plato.calendar.domain.entity.PersonalSchedule
import pnu.plato.calendar.presentation.common.theme.CalendarGraphite
import pnu.plato.calendar.presentation.common.theme.CalendarSage
import java.time.LocalDateTime

data class PersonalScheduleUiModel(
    val id: Long,
    val title: String,
    val description: String?,
    val startAt: LocalDateTime,
    val endAt: LocalDateTime,
    val courseName: String?,
) {
    constructor(domain: PersonalSchedule, courseName: String?) : this(
        id = domain.id,
        title = if (startsWithComplete(domain.title)) {
            domain.title.removePrefix(COMPLETE)
        } else {
            domain.title
        },
        description = domain.description,
        startAt = domain.startAt,
        endAt = domain.endAt,
        courseName = courseName,
    )

    val isComplete: Boolean get() = startsWithComplete(title)

    val color: Color get() = if (!isComplete) CalendarSage else CalendarGraphite

    companion object {
        const val COMPLETE = "(완료) "

        private fun startsWithComplete(title: String): Boolean {
            return title.startsWith(COMPLETE)
        }
    }
}
