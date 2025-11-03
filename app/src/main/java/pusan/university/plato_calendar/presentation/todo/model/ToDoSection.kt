package pusan.university.plato_calendar.presentation.todo.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.ui.graphics.vector.ImageVector

enum class ToDoSection(val title: String, val icon: ImageVector) {
    WITHIN_7_DAYS("7일 이내", Icons.Default.DateRange),
    COMPLETED("완료", Icons.Default.CheckCircle),
    COURSE("강의 일정", Icons.Default.DateRange),
    CUSTOM("개인 일정", Icons.Default.DateRange),
    ACADEMIC("학사 일정", Icons.Default.DateRange),
}