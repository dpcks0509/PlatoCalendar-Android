package pnu.plato.calendar.presentation.calendar.model

import androidx.compose.ui.graphics.Color
import pnu.plato.calendar.domain.entity.DayOfWeek

data class DayOfWeekUiModel(
    val title: String,
    val color: Color
) {
    companion object {
        fun from(dayOfWeek: DayOfWeek): DayOfWeekUiModel {
            return DayOfWeekUiModel(
                title = dayOfWeek.title(),
                color = if (dayOfWeek.isWeekend) Color.Red else Color.Black
            )
        }
    }
}

private fun DayOfWeek.title(): String {
    return when (this) {
        DayOfWeek.SUNDAY -> "일"
        DayOfWeek.MONDAY -> "월"
        DayOfWeek.TUESDAY -> "화"
        DayOfWeek.WEDNESDAY -> "수"
        DayOfWeek.THURSDAY -> "목"
        DayOfWeek.FRIDAY -> "금"
        DayOfWeek.SATURDAY -> "토"
    }
}
