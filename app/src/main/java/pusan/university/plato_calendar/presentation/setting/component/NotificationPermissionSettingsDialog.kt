package pusan.university.plato_calendar.presentation.setting.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun NotificationPermissionSettingsDialog(
    onDismissRequest: () -> Unit,
    onNavigateToSettings: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = "알림 권한") },
        text = { Text(text = "일정 알림을 받기 위해서는 알림 권한이 필요합니다.") },
        confirmButton = {
            TextButton(onClick = onNavigateToSettings) {
                Text(text = "설정")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = "취소")
            }
        },
    )
}
