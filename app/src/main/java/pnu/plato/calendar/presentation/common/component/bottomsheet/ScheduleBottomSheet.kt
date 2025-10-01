package pnu.plato.calendar.presentation.common.component.bottomsheet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.google.android.gms.ads.AdView
import pnu.plato.calendar.domain.entity.Schedule.NewSchedule
import pnu.plato.calendar.domain.entity.Schedule.PersonalSchedule.CustomSchedule
import pnu.plato.calendar.presentation.common.component.bottomsheet.ScheduleBottomSheetContent.AcademicScheduleContent
import pnu.plato.calendar.presentation.common.component.bottomsheet.ScheduleBottomSheetContent.CourseScheduleContent
import pnu.plato.calendar.presentation.common.component.bottomsheet.ScheduleBottomSheetContent.CustomScheduleContent
import pnu.plato.calendar.presentation.common.component.bottomsheet.ScheduleBottomSheetContent.NewScheduleContent
import pnu.plato.calendar.presentation.common.theme.White
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleBottomSheet(
    content: ScheduleBottomSheetContent?,
    selectedDate: LocalDate,
    adView: AdView,
    sheetState: SheetState,
    makeSchedule: (NewSchedule) -> Unit,
    editSchedule: (CustomSchedule) -> Unit,
    deleteSchedule: (Long) -> Unit,
    toggleScheduleCompletion: (Long, Boolean) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismissRequest,
        dragHandle = null,
        modifier = modifier,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(White)
                    .clip(RoundedCornerShape(8.dp)),
        ) {
            when (content) {
                is AcademicScheduleContent ->
                    AcademicScheduleContent(
                        schedule = content.schedule,
                        adView = adView,
                        onDismissRequest = onDismissRequest,
                    )

                is CourseScheduleContent ->
                    CourseScheduleContent(
                        schedule = content.schedule,
                        adView = adView,
                        toggleScheduleCompletion = toggleScheduleCompletion,
                        onDismissRequest = onDismissRequest,
                    )

                is CustomScheduleContent ->
                    CustomScheduleContent(
                        schedule = content.schedule,
                        adView = adView,
                        editSchedule = editSchedule,
                        deleteSchedule = deleteSchedule,
                        toggleScheduleCompletion = toggleScheduleCompletion,
                        onDismissRequest = onDismissRequest,
                    )

                is NewScheduleContent ->
                    NewScheduleContent(
                        adView = adView,
                        selectedDate = selectedDate,
                        makeSchedule = makeSchedule,
                        onDismissRequest = onDismissRequest,
                    )

                null -> Unit
            }
        }
    }
}
