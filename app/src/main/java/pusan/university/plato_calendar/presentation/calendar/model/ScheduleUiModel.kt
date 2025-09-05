package pusan.university.plato_calendar.presentation.calendar.model

import androidx.compose.ui.graphics.Color
import pusan.university.plato_calendar.domain.entity.Schedule
import pusan.university.plato_calendar.presentation.common.function.parseIcsToLocalDateTime
import java.time.LocalDateTime

data class ScheduleUiModel(
    val id: String,
    val isComplete: Boolean,
    val deadLine: LocalDateTime?,
    val courseName: String?,
    val title: String?,
    val color: Color?
) {
    constructor(domain: Schedule, courseName: String?) : this(
        id = domain.uid,
        isComplete = false,
        deadLine = domain.end?.parseIcsToLocalDateTime(),
        courseName = courseName,
        title = domain.summary,
        color = null
    )
}

/*
UID:515011@plato.pusan.ac.kr
SUMMARY:마감 기한
DESCRIPTION:좋은 질문으로 인정받은 질문들을 모은 하나의 파일 업로드
CLASS:PUBLIC
LAST-MODIFIED:20210619T144119Z
DTSTAMP:20210625T175035Z
DTSTART:20210621T150000Z
DTEND:20210621T150000Z
CATEGORIES:2021_10_CB16556_061
END:VEVENT

BEGIN:VEVENT
UID:382434@plato.pusan.ac.kr
SUMMARY:마감 기한
DESCRIPTION:
CLASS:PUBLIC
LAST-MODIFIED:20210222T054523Z
DTSTAMP:20210520T060216Z
DTSTART:20210502T145900Z
DTEND:20210502T145900Z
CATEGORIES:2021_10_TL12080_001
 */