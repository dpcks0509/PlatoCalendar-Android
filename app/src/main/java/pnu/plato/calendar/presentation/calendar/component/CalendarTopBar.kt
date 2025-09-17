package pnu.plato.calendar.presentation.calendar.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pnu.plato.calendar.presentation.calendar.model.YearMonth
import pnu.plato.calendar.presentation.common.theme.PlatoCalendarTheme
import pnu.plato.calendar.presentation.common.theme.PrimaryColor
import java.time.LocalDate

@Composable
fun CalendarTopBar(
    today: LocalDate,
    selectedDate: LocalDate,
    currentYearMonth: YearMonth,
    moveToToday: () -> Unit,
    showMakePersonalScheduleBottomSheet: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "${currentYearMonth.year}년 ${currentYearMonth.month}월",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
        )

        Spacer(modifier = Modifier.weight(1f))

        if (today != selectedDate) {
            OutlinedButton(
                onClick = moveToToday,
                border = BorderStroke(2.dp, Color.White),
                shape = RoundedCornerShape(40.dp),
            ) {
                Text(
                    text = "TODAY",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(vertical = 4.dp),
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add",
            tint = Color.White,
            modifier =
                Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .clickable {
                        showMakePersonalScheduleBottomSheet()
                    },
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CalendarTopBarPreview() {
    PlatoCalendarTheme {
        CalendarTopBar(
            today = LocalDate.now(),
            selectedDate = LocalDate.now(),
            currentYearMonth = YearMonth(year = 2025, month = 9),
            moveToToday = {},
            showMakePersonalScheduleBottomSheet = {},
            modifier =
                Modifier
                    .background(PrimaryColor)
                    .statusBarsPadding()
                    .padding(all = 16.dp)
                    .fillMaxWidth()
                    .height(50.dp),
        )
    }
}
