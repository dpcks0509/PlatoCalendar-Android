package pnu.plato.calendar.presentation.calendar.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import pnu.plato.calendar.presentation.calendar.model.DayUiModel
import pnu.plato.calendar.presentation.common.theme.PlatoCalendarTheme

@Composable
fun MonthItem(
    month: List<List<DayUiModel>>,
    modifier: Modifier = Modifier
) {

}

@Preview(showBackground = true)
@Composable
fun MonthItemPreview() {
    PlatoCalendarTheme {
        MonthItem(month = emptyList(), modifier = Modifier)
    }
}