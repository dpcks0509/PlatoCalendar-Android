package pnu.plato.calendar.presentation.calendar.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pnu.plato.calendar.domain.entity.DayOfWeek
import pnu.plato.calendar.presentation.calendar.model.DayOfWeekUiModel
import pnu.plato.calendar.presentation.common.theme.PlatoCalendarTheme

@Composable
fun DayOfWeekHeader(
    modifier: Modifier = Modifier
) {
    val dayOfWeekItems = DayOfWeek.entries

    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(items = dayOfWeekItems) { dayOfWeek ->
            DayOfWeekItem(
                DayOfWeekUiModel.from(dayOfWeek),
            )
        }
    }
}

@Composable
private fun DayOfWeekItem(
    dayOfWeek: DayOfWeekUiModel,
    modifier: Modifier = Modifier
) {
    Text(
        text = dayOfWeek.name,
        color = dayOfWeek.color,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun DayOfWeekHeaderPreview() {
    PlatoCalendarTheme {
        DayOfWeekHeader(modifier = Modifier.fillMaxWidth())
    }
}

@Preview(showBackground = true)
@Composable
fun DayOfWeekItemPreview() {
    PlatoCalendarTheme {
        DayOfWeekItem(
            DayOfWeekUiModel.from(DayOfWeek.SATURDAY),
            modifier = Modifier.padding(horizontal = 12.dp)
        )
    }
}