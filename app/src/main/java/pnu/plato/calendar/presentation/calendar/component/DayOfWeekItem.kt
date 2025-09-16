package pnu.plato.calendar.presentation.calendar.component

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pnu.plato.calendar.domain.entity.DayOfWeek
import pnu.plato.calendar.presentation.calendar.model.DayOfWeekUiModel
import pnu.plato.calendar.presentation.common.theme.PlatoCalendarTheme

@Composable
fun DayOfWeekItem(
    dayOfWeek: DayOfWeekUiModel,
    modifier: Modifier = Modifier,
) {
    Text(
        text = dayOfWeek.title,
        color = dayOfWeek.color,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = modifier,
    )
}

@Preview(showBackground = true)
@Composable
fun DayOfWeekItemWeekendPreview() {
    PlatoCalendarTheme {
        DayOfWeekItem(
            DayOfWeekUiModel.from(DayOfWeek.SATURDAY),
            modifier = Modifier.padding(horizontal = 12.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DayOfWeekItemWeekDayPreview() {
    PlatoCalendarTheme {
        DayOfWeekItem(
            DayOfWeekUiModel.from(DayOfWeek.MONDAY),
            modifier = Modifier.padding(horizontal = 12.dp),
        )
    }
}
