package pnu.plato.calendar.presentation.calendar.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import pnu.plato.calendar.presentation.calendar.model.DayUiModel
import pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel.AcademicScheduleUiModel
import pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel
import pnu.plato.calendar.presentation.common.theme.PlatoCalendarTheme
import java.time.LocalDate
import java.time.LocalDateTime

@Composable
fun MonthItem(
    month: List<List<DayUiModel>>,
    onClickDate: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        month.forEach { week ->
            WeekItem(
                week = week,
                onClickDate = onClickDate,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MonthItemPreview() {
    PlatoCalendarTheme {
        MonthItem(
            month =
                listOf(
                    listOf(
                        DayUiModel(
                            date = LocalDate.of(2023, 12, 31),
                            isToday = false,
                            isSelected = false,
                            isInMonth = false,
                            schedules = emptyList(),
                        ),
                        DayUiModel(
                            date = LocalDate.of(2024, 1, 1),
                            isToday = false,
                            isSelected = false,
                            isInMonth = true,
                            schedules =
                                listOf(
                                    AcademicScheduleUiModel(
                                        title = "신정",
                                        startAt = LocalDate.of(2024, 1, 1),
                                        endAt = LocalDate.of(2024, 1, 1),
                                    ),
                                ),
                        ),
                        DayUiModel(
                            date = LocalDate.of(2024, 1, 2),
                            isToday = false,
                            isSelected = false,
                            isInMonth = true,
                            schedules = emptyList(),
                        ),
                        DayUiModel(
                            date = LocalDate.of(2024, 1, 3),
                            isToday = false,
                            isSelected = false,
                            isInMonth = true,
                            schedules =
                                listOf(
                                    PersonalScheduleUiModel(
                                        id = 1L,
                                        title = "새해 계획 세우기",
                                        description = "목표 설정하기",
                                        startAt = LocalDateTime.of(2024, 1, 3, 14, 0),
                                        endAt = LocalDateTime.of(2024, 1, 3, 16, 0),
                                        courseName = null,
                                    ),
                                ),
                        ),
                        DayUiModel(
                            date = LocalDate.of(2024, 1, 4),
                            isToday = false,
                            isSelected = false,
                            isInMonth = true,
                            schedules = emptyList(),
                        ),
                        DayUiModel(
                            date = LocalDate.of(2024, 1, 5),
                            isToday = false,
                            isSelected = false,
                            isInMonth = true,
                            schedules = emptyList(),
                        ),
                        DayUiModel(
                            date = LocalDate.of(2024, 1, 6),
                            isToday = false,
                            isSelected = false,
                            isInMonth = true,
                            schedules = emptyList(),
                        ),
                    ),
                    listOf(
                        DayUiModel(
                            date = LocalDate.of(2024, 1, 7),
                            isToday = false,
                            isSelected = false,
                            isInMonth = true,
                            schedules =
                                listOf(
                                    PersonalScheduleUiModel(
                                        id = 2L,
                                        title = "개인 공부",
                                        description = "코틀린 공부하기",
                                        startAt = LocalDateTime.of(2024, 1, 7, 10, 0),
                                        endAt = LocalDateTime.of(2024, 1, 7, 12, 0),
                                        courseName = null,
                                    ),
                                ),
                        ),
                        DayUiModel(
                            date = LocalDate.of(2024, 1, 8),
                            isToday = true,
                            isSelected = false,
                            isInMonth = true,
                            schedules =
                                listOf(
                                    AcademicScheduleUiModel(
                                        title = "컴퓨터공학과 개강",
                                        startAt = LocalDate.of(2024, 1, 8),
                                        endAt = LocalDate.of(2024, 1, 8),
                                    ),
                                    PersonalScheduleUiModel(
                                        id = 3L,
                                        title = "데이터베이스 과제",
                                        description = "ER 다이어그램 작성",
                                        startAt = LocalDateTime.of(2024, 1, 8, 14, 0),
                                        endAt = LocalDateTime.of(2024, 1, 8, 16, 0),
                                        courseName = "데이터베이스",
                                    ),
                                ),
                        ),
                        DayUiModel(
                            date = LocalDate.of(2024, 1, 9),
                            isToday = false,
                            isSelected = false,
                            isInMonth = true,
                            schedules =
                                listOf(
                                    PersonalScheduleUiModel(
                                        id = 4L,
                                        title = "알고리즘 과제",
                                        description = "정렬 알고리즘 구현",
                                        startAt = LocalDateTime.of(2024, 1, 9, 9, 0),
                                        endAt = LocalDateTime.of(2024, 1, 9, 11, 0),
                                        courseName = "알고리즘",
                                    ),
                                ),
                        ),
                        DayUiModel(
                            date = LocalDate.of(2024, 1, 10),
                            isToday = false,
                            isSelected = false,
                            isInMonth = true,
                            schedules =
                                listOf(
                                    PersonalScheduleUiModel(
                                        id = 5L,
                                        title = "팀 미팅",
                                        description = "프로젝트 진행 상황 공유",
                                        startAt = LocalDateTime.of(2024, 1, 10, 15, 0),
                                        endAt = LocalDateTime.of(2024, 1, 10, 17, 0),
                                        courseName = null,
                                    ),
                                ),
                        ),
                        DayUiModel(
                            date = LocalDate.of(2024, 1, 11),
                            isToday = false,
                            isSelected = true,
                            isInMonth = true,
                            schedules =
                                listOf(
                                    PersonalScheduleUiModel(
                                        id = 6L,
                                        title = "네트워크 과제",
                                        description = "TCP/IP 소켓 프로그래밍",
                                        startAt = LocalDateTime.of(2024, 1, 11, 10, 0),
                                        endAt = LocalDateTime.of(2024, 1, 11, 12, 0),
                                        courseName = "컴퓨터네트워크",
                                    ),
                                ),
                        ),
                        DayUiModel(
                            date = LocalDate.of(2024, 1, 12),
                            isToday = false,
                            isSelected = false,
                            isInMonth = true,
                            schedules =
                                listOf(
                                    AcademicScheduleUiModel(
                                        title = "캡스톤 발표회",
                                        startAt = LocalDate.of(2024, 1, 12),
                                        endAt = LocalDate.of(2024, 1, 12),
                                    ),
                                ),
                        ),
                        DayUiModel(
                            date = LocalDate.of(2024, 1, 13),
                            isToday = false,
                            isSelected = false,
                            isInMonth = true,
                            schedules =
                                listOf(
                                    PersonalScheduleUiModel(
                                        id = 7L,
                                        title = "개인 프로젝트",
                                        description = "안드로이드 앱 개발",
                                        startAt = LocalDateTime.of(2024, 1, 13, 10, 0),
                                        endAt = LocalDateTime.of(2024, 1, 13, 15, 0),
                                        courseName = null,
                                    ),
                                ),
                        ),
                    ),
                    listOf(
                        DayUiModel(
                            date = LocalDate.of(2024, 1, 14),
                            isToday = false,
                            isSelected = false,
                            isInMonth = true,
                            schedules = emptyList(),
                        ),
                        DayUiModel(
                            date = LocalDate.of(2024, 1, 15),
                            isToday = false,
                            isSelected = false,
                            isInMonth = true,
                            schedules =
                                listOf(
                                    PersonalScheduleUiModel(
                                        id = 8L,
                                        title = "운영체제 시험 준비",
                                        description = "프로세스와 쓰레드 복습",
                                        startAt = LocalDateTime.of(2024, 1, 15, 13, 0),
                                        endAt = LocalDateTime.of(2024, 1, 15, 18, 0),
                                        courseName = "운영체제",
                                    ),
                                ),
                        ),
                        DayUiModel(
                            date = LocalDate.of(2024, 1, 16),
                            isToday = false,
                            isSelected = false,
                            isInMonth = true,
                            schedules = emptyList(),
                        ),
                        DayUiModel(
                            date = LocalDate.of(2024, 1, 17),
                            isToday = false,
                            isSelected = false,
                            isInMonth = true,
                            schedules =
                                listOf(
                                    AcademicScheduleUiModel(
                                        title = "중간고사 기간",
                                        startAt = LocalDate.of(2024, 1, 17),
                                        endAt = LocalDate.of(2024, 1, 24),
                                    ),
                                ),
                        ),
                        DayUiModel(
                            date = LocalDate.of(2024, 1, 18),
                            isToday = false,
                            isSelected = false,
                            isInMonth = true,
                            schedules =
                                listOf(
                                    PersonalScheduleUiModel(
                                        id = 9L,
                                        title = "소프트웨어공학 과제",
                                        description = "요구사항 명세서 작성",
                                        startAt = LocalDateTime.of(2024, 1, 18, 14, 0),
                                        endAt = LocalDateTime.of(2024, 1, 18, 17, 0),
                                        courseName = "소프트웨어공학",
                                    ),
                                ),
                        ),
                        DayUiModel(
                            date = LocalDate.of(2024, 1, 19),
                            isToday = false,
                            isSelected = false,
                            isInMonth = true,
                            schedules =
                                listOf(
                                    PersonalScheduleUiModel(
                                        id = 10L,
                                        title = "동아리 활동",
                                        description = "프로그래밍 동아리 모임",
                                        startAt = LocalDateTime.of(2024, 1, 19, 19, 0),
                                        endAt = LocalDateTime.of(2024, 1, 19, 21, 0),
                                        courseName = null,
                                    ),
                                ),
                        ),
                        DayUiModel(
                            date = LocalDate.of(2024, 1, 20),
                            isToday = false,
                            isSelected = false,
                            isInMonth = true,
                            schedules = emptyList(),
                        ),
                    ),
                    listOf(
                        DayUiModel(
                            date = LocalDate.of(2024, 1, 21),
                            isToday = false,
                            isSelected = false,
                            isInMonth = true,
                            schedules =
                                listOf(
                                    PersonalScheduleUiModel(
                                        id = 11L,
                                        title = "친구와 약속",
                                        description = "카페에서 만나기",
                                        startAt = LocalDateTime.of(2024, 1, 21, 16, 0),
                                        endAt = LocalDateTime.of(2024, 1, 21, 18, 0),
                                        courseName = null,
                                    ),
                                ),
                        ),
                        DayUiModel(
                            date = LocalDate.of(2024, 1, 22),
                            isToday = false,
                            isSelected = false,
                            isInMonth = true,
                            schedules =
                                listOf(
                                    PersonalScheduleUiModel(
                                        id = 12L,
                                        title = "데이터베이스 시험",
                                        description = "중간고사",
                                        startAt = LocalDateTime.of(2024, 1, 22, 10, 0),
                                        endAt = LocalDateTime.of(2024, 1, 22, 12, 0),
                                        courseName = "데이터베이스",
                                    ),
                                ),
                        ),
                        DayUiModel(
                            date = LocalDate.of(2024, 1, 23),
                            isToday = false,
                            isSelected = false,
                            isInMonth = true,
                            schedules =
                                listOf(
                                    PersonalScheduleUiModel(
                                        id = 13L,
                                        title = "알고리즘 시험",
                                        description = "중간고사",
                                        startAt = LocalDateTime.of(2024, 1, 23, 14, 0),
                                        endAt = LocalDateTime.of(2024, 1, 23, 16, 0),
                                        courseName = "알고리즘",
                                    ),
                                ),
                        ),
                        DayUiModel(
                            date = LocalDate.of(2024, 1, 24),
                            isToday = false,
                            isSelected = false,
                            isInMonth = true,
                            schedules =
                                listOf(
                                    PersonalScheduleUiModel(
                                        id = 14L,
                                        title = "운영체제 시험",
                                        description = "중간고사",
                                        startAt = LocalDateTime.of(2024, 1, 24, 10, 0),
                                        endAt = LocalDateTime.of(2024, 1, 24, 12, 0),
                                        courseName = "운영체제",
                                    ),
                                ),
                        ),
                        DayUiModel(
                            date = LocalDate.of(2024, 1, 25),
                            isToday = false,
                            isSelected = false,
                            isInMonth = true,
                            schedules =
                                listOf(
                                    PersonalScheduleUiModel(
                                        id = 15L,
                                        title = "컴퓨터네트워크 시험",
                                        description = "중간고사",
                                        startAt = LocalDateTime.of(2024, 1, 25, 14, 0),
                                        endAt = LocalDateTime.of(2024, 1, 25, 16, 0),
                                        courseName = "컴퓨터네트워크",
                                    ),
                                ),
                        ),
                        DayUiModel(
                            date = LocalDate.of(2024, 1, 26),
                            isToday = false,
                            isSelected = false,
                            isInMonth = true,
                            schedules =
                                listOf(
                                    PersonalScheduleUiModel(
                                        id = 16L,
                                        title = "소프트웨어공학 시험",
                                        description = "중간고사",
                                        startAt = LocalDateTime.of(2024, 1, 26, 10, 0),
                                        endAt = LocalDateTime.of(2024, 1, 26, 12, 0),
                                        courseName = "소프트웨어공학",
                                    ),
                                ),
                        ),
                        DayUiModel(
                            date = LocalDate.of(2024, 1, 27),
                            isToday = false,
                            isSelected = false,
                            isInMonth = true,
                            schedules = emptyList(),
                        ),
                    ),
                    listOf(
                        DayUiModel(
                            date = LocalDate.of(2024, 1, 28),
                            isToday = false,
                            isSelected = false,
                            isInMonth = true,
                            schedules =
                                listOf(
                                    PersonalScheduleUiModel(
                                        id = 17L,
                                        title = "시험 끝 축하 파티",
                                        description = "친구들과 함께",
                                        startAt = LocalDateTime.of(2024, 1, 28, 18, 0),
                                        endAt = LocalDateTime.of(2024, 1, 28, 22, 0),
                                        courseName = null,
                                    ),
                                ),
                        ),
                        DayUiModel(
                            date = LocalDate.of(2024, 1, 29),
                            isToday = false,
                            isSelected = false,
                            isInMonth = true,
                            schedules =
                                listOf(
                                    PersonalScheduleUiModel(
                                        id = 18L,
                                        title = "방학 계획 세우기",
                                        description = "여행 및 인턴십 준비",
                                        startAt = LocalDateTime.of(2024, 1, 29, 14, 0),
                                        endAt = LocalDateTime.of(2024, 1, 29, 16, 0),
                                        courseName = null,
                                    ),
                                ),
                        ),
                        DayUiModel(
                            date = LocalDate.of(2024, 1, 30),
                            isToday = false,
                            isSelected = false,
                            isInMonth = true,
                            schedules = emptyList(),
                        ),
                        DayUiModel(
                            date = LocalDate.of(2024, 1, 31),
                            isToday = false,
                            isSelected = false,
                            isInMonth = true,
                            schedules =
                                listOf(
                                    PersonalScheduleUiModel(
                                        id = 19L,
                                        title = "1월 마무리",
                                        description = "한 달 회고하기",
                                        startAt = LocalDateTime.of(2024, 1, 31, 20, 0),
                                        endAt = LocalDateTime.of(2024, 1, 31, 22, 0),
                                        courseName = null,
                                    ),
                                ),
                        ),
                        DayUiModel(
                            date = LocalDate.of(2024, 2, 1),
                            isToday = false,
                            isSelected = false,
                            isInMonth = false,
                            schedules =
                                listOf(
                                    PersonalScheduleUiModel(
                                        id = 20L,
                                        title = "2월 시작",
                                        description = "새로운 목표 설정",
                                        startAt = LocalDateTime.of(2024, 2, 1, 10, 0),
                                        endAt = LocalDateTime.of(2024, 2, 1, 12, 0),
                                        courseName = null,
                                    ),
                                ),
                        ),
                        DayUiModel(
                            date = LocalDate.of(2024, 2, 2),
                            isToday = false,
                            isSelected = false,
                            isInMonth = false,
                            schedules = emptyList(),
                        ),
                        DayUiModel(
                            date = LocalDate.of(2024, 2, 3),
                            isToday = false,
                            isSelected = false,
                            isInMonth = false,
                            schedules = emptyList(),
                        ),
                    ),
                    // 여섯 번째 주 (2월 4일 - 10일)
                    listOf(
                        DayUiModel(
                            date = LocalDate.of(2024, 2, 4),
                            isToday = false,
                            isSelected = false,
                            isInMonth = false,
                            schedules = emptyList(),
                        ),
                        DayUiModel(
                            date = LocalDate.of(2024, 2, 5),
                            isToday = false,
                            isSelected = false,
                            isInMonth = false,
                            schedules = emptyList(),
                        ),
                        DayUiModel(
                            date = LocalDate.of(2024, 2, 6),
                            isToday = false,
                            isSelected = false,
                            isInMonth = false,
                            schedules = emptyList(),
                        ),
                        DayUiModel(
                            date = LocalDate.of(2024, 2, 7),
                            isToday = false,
                            isSelected = false,
                            isInMonth = false,
                            schedules = emptyList(),
                        ),
                        DayUiModel(
                            date = LocalDate.of(2024, 2, 8),
                            isToday = false,
                            isSelected = false,
                            isInMonth = false,
                            schedules = emptyList(),
                        ),
                        DayUiModel(
                            date = LocalDate.of(2024, 2, 9),
                            isToday = false,
                            isSelected = false,
                            isInMonth = false,
                            schedules =
                                listOf(
                                    AcademicScheduleUiModel(
                                        title = "설날",
                                        startAt = LocalDate.of(2024, 2, 9),
                                        endAt = LocalDate.of(2024, 2, 9),
                                    ),
                                ),
                        ),
                        DayUiModel(
                            date = LocalDate.of(2024, 2, 10),
                            isToday = false,
                            isSelected = false,
                            isInMonth = false,
                            schedules =
                                listOf(
                                    AcademicScheduleUiModel(
                                        title = "설날",
                                        startAt = LocalDate.of(2024, 2, 10),
                                        endAt = LocalDate.of(2024, 2, 10),
                                    ),
                                ),
                        ),
                    ),
                ),
            onClickDate = {},
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
