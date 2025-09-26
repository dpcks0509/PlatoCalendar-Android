package pnu.plato.calendar.presentation.calendar.component.bottomsheet

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
import pnu.plato.calendar.presentation.calendar.component.bottomsheet.ScheduleBottomSheetContent.AcademicScheduleContent
import pnu.plato.calendar.presentation.calendar.component.bottomsheet.ScheduleBottomSheetContent.CourseScheduleContent
import pnu.plato.calendar.presentation.calendar.component.bottomsheet.ScheduleBottomSheetContent.CustomScheduleContent
import pnu.plato.calendar.presentation.calendar.component.bottomsheet.ScheduleBottomSheetContent.NewScheduleContent
import pnu.plato.calendar.presentation.common.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleBottomSheet(
    content: ScheduleBottomSheetContent?,
    adView: AdView,
    sheetState: SheetState,
    makeSchedule: (NewSchedule) -> Unit,
    editSchedule: (CustomSchedule) -> Unit,
    deleteSchedule: (Long) -> Unit,
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
                        editSchedule = editSchedule,
                        onDismissRequest = onDismissRequest,
                    )

                is CustomScheduleContent ->
                    CustomScheduleContent(
                        schedule = content.schedule,
                        adView = adView,
                        editSchedule = editSchedule,
                        deleteSchedule = deleteSchedule,
                        onDismissRequest = onDismissRequest,
                    )

                is NewScheduleContent ->
                    NewScheduleContent(
                        adView = adView,
                        makeSchedule = makeSchedule,
                        onDismissRequest = onDismissRequest,
                    )

                null -> Unit
            }
        }
    }
}
