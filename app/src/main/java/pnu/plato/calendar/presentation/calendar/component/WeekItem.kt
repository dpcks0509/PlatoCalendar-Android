package pnu.plato.calendar.presentation.calendar.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel
import pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel.AcademicScheduleUiModel
import pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel
import pnu.plato.calendar.presentation.calendar.model.YearMonth
import pnu.plato.calendar.presentation.common.theme.PlatoCalendarTheme
import java.time.LocalDate
import java.time.LocalDateTime

@Composable
fun WeekItem(
    weekDates: List<LocalDate>,
    today: LocalDate,
    selectedDate: LocalDate,
    currentYearMonth: YearMonth,
    schedules: List<ScheduleUiModel>,
    onClickDate: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
    ) {
        weekDates.forEach { date ->
            DayItem(
                date = date,
                today = today,
                selectedDate = selectedDate,
                currentYearMonth = currentYearMonth,
                schedules = schedules,
                onClickDate = { onClickDate(date) },
                modifier =
                    Modifier
                        .weight(1f)
                        .aspectRatio(3f / 3.6f),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WeekItemPreview() {
    PlatoCalendarTheme {
        WeekItem(
            weekDates =
                listOf(
                    LocalDate.of(2024, 1, 7),
                    LocalDate.of(2024, 1, 8),
                    LocalDate.of(2024, 1, 9),
                    LocalDate.of(2024, 1, 10),
                    LocalDate.of(2024, 1, 11),
                    LocalDate.of(2024, 1, 12),
                    LocalDate.of(2024, 1, 13),
                ),
            today = LocalDate.of(2024, 1, 8),
            selectedDate = LocalDate.of(2024, 1, 11),
            currentYearMonth = YearMonth(2024, 1),
            schedules =
                listOf(
                    PersonalScheduleUiModel(
                        id = 1L,
                        title = "개인 공부",
                        description = "코틀린 공부하기",
                        startAt = LocalDateTime.of(2024, 1, 7, 10, 0),
                        endAt = LocalDateTime.of(2024, 1, 7, 12, 0),
                        courseName = null,
                    ),
                    AcademicScheduleUiModel(
                        title = "컴퓨터공학과 개강",
                        startAt = LocalDate.of(2024, 1, 8),
                        endAt = LocalDate.of(2024, 1, 8),
                    ),
                    PersonalScheduleUiModel(
                        id = 2L,
                        title = "데이터베이스 과제",
                        description = "ER 다이어그램 작성",
                        startAt = LocalDateTime.of(2024, 1, 8, 14, 0),
                        endAt = LocalDateTime.of(2024, 1, 8, 16, 0),
                        courseName = "데이터베이스",
                    ),
                    PersonalScheduleUiModel(
                        id = 3L,
                        title = "(완료) 알고리즘 과제",
                        description = "정렬 알고리즘 구현",
                        startAt = LocalDateTime.of(2024, 1, 9, 9, 0),
                        endAt = LocalDateTime.of(2024, 1, 9, 11, 0),
                        courseName = "알고리즘",
                    ),
                    PersonalScheduleUiModel(
                        id = 4L,
                        title = "팀 미팅",
                        description = "프로젝트 진행 상황 공유",
                        startAt = LocalDateTime.of(2024, 1, 9, 15, 0),
                        endAt = LocalDateTime.of(2024, 1, 9, 17, 0),
                        courseName = null,
                    ),
                    AcademicScheduleUiModel(
                        title = "중간고사 기간",
                        startAt = LocalDate.of(2024, 1, 10),
                        endAt = LocalDate.of(2024, 1, 17),
                    ),
                    PersonalScheduleUiModel(
                        id = 5L,
                        title = "운영체제 시험 준비",
                        description = "프로세스와 쓰레드 복습",
                        startAt = LocalDateTime.of(2024, 1, 10, 13, 0),
                        endAt = LocalDateTime.of(2024, 1, 10, 18, 0),
                        courseName = "운영체제",
                    ),
                    PersonalScheduleUiModel(
                        id = 6L,
                        title = "네트워크 과제",
                        description = "TCP/IP 소켓 프로그래밍",
                        startAt = LocalDateTime.of(2024, 1, 11, 10, 0),
                        endAt = LocalDateTime.of(2024, 1, 11, 12, 0),
                        courseName = "컴퓨터네트워크",
                    ),
                    PersonalScheduleUiModel(
                        id = 7L,
                        title = "(완료) 도서관 반납",
                        description = "빌린 책 반납하기",
                        startAt = LocalDateTime.of(2024, 1, 11, 16, 0),
                        endAt = LocalDateTime.of(2024, 1, 11, 17, 0),
                        courseName = null,
                    ),
                    PersonalScheduleUiModel(
                        id = 8L,
                        title = "동아리 활동",
                        description = "프로그래밍 동아리 모임",
                        startAt = LocalDateTime.of(2024, 1, 11, 19, 0),
                        endAt = LocalDateTime.of(2024, 1, 11, 21, 0),
                        courseName = null,
                    ),
                    AcademicScheduleUiModel(
                        title = "캡스톤 발표회",
                        startAt = LocalDate.of(2024, 1, 12),
                        endAt = LocalDate.of(2024, 1, 12),
                    ),
                    PersonalScheduleUiModel(
                        id = 9L,
                        title = "소프트웨어공학 과제",
                        description = "요구사항 명세서 작성",
                        startAt = LocalDateTime.of(2024, 1, 12, 14, 0),
                        endAt = LocalDateTime.of(2024, 1, 12, 17, 0),
                        courseName = "소프트웨어공학",
                    ),
                    PersonalScheduleUiModel(
                        id = 10L,
                        title = "개인 프로젝트",
                        description = "안드로이드 앱 개발",
                        startAt = LocalDateTime.of(2024, 1, 13, 10, 0),
                        endAt = LocalDateTime.of(2024, 1, 13, 15, 0),
                        courseName = null,
                    ),
                    PersonalScheduleUiModel(
                        id = 11L,
                        title = "(완료) 친구와 약속",
                        description = "카페에서 만나기",
                        startAt = LocalDateTime.of(2024, 1, 13, 16, 0),
                        endAt = LocalDateTime.of(2024, 1, 13, 18, 0),
                        courseName = null,
                    ),
                ),
            onClickDate = {},
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
