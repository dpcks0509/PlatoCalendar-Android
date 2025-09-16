package pnu.plato.calendar.presentation.calendar.model

import androidx.compose.ui.graphics.Color
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale

data class DayOfWeekUiModel(
    val title: String,
    val color: Color
) {
    companion object {
        fun from(dayOfWeek: DayOfWeek): DayOfWeekUiModel {
            return DayOfWeekUiModel(
                title = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREAN),
                color = if (dayOfWeek.isWeekend()) Color.Red else Color.Black
            )
        }

        fun DayOfWeek.isWeekend(): Boolean {
            return this == DayOfWeek.SATURDAY || this == DayOfWeek.SUNDAY
        }

        fun dayOfWeeksEntries(): List<DayOfWeek> = listOf(
            DayOfWeek.SUNDAY,
            DayOfWeek.MONDAY,
            DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY,
            DayOfWeek.FRIDAY,
            DayOfWeek.SATURDAY,
        )
    }
}