package pnu.plato.calendar.domain.entity

import java.time.LocalDateTime

data class Schedule(
    val id: String,
    val title: String,
    val description: String?,
    val deadLine: LocalDateTime,
    val courseCode: String?
)

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
 */