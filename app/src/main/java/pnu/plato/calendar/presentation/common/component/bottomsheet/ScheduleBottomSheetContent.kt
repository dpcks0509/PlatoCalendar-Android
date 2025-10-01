package pnu.plato.calendar.presentation.common.component.bottomsheet

import pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel.AcademicScheduleUiModel
import pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel.CourseScheduleUiModel
import pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel.CustomScheduleUiModel

sealed interface ScheduleBottomSheetContent {
    data class AcademicScheduleContent(
        val schedule: AcademicScheduleUiModel,
    ) : ScheduleBottomSheetContent

    data class CourseScheduleContent(
        val schedule: CourseScheduleUiModel,
    ) : ScheduleBottomSheetContent

    data class CustomScheduleContent(
        val schedule: CustomScheduleUiModel,
    ) : ScheduleBottomSheetContent

    data object NewScheduleContent : ScheduleBottomSheetContent
}
