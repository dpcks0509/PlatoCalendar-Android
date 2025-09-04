package pusan.university.plato_calendar.presentation.calendar.model

import pusan.university.plato_calendar.domain.entity.Schedule

data class ScheduleUiModel(
    val uid: String,
    val summary: String?,
    val description: String?,
    val classification: String?,
    val lastModified: String?,
    val timestamp: String?,
    val start: String?,
    val end: String?,
    val categories: String?,
    val subjectName: String?
) {
    constructor(domain: Schedule, subjectName: String?) : this(
        uid = domain.uid,
        summary = domain.summary,
        description = domain.description,
        classification = domain.classification,
        lastModified = domain.lastModified,
        timestamp = domain.timestamp,
        start = domain.start,
        end = domain.end,
        categories = domain.categories,
        subjectName = subjectName
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