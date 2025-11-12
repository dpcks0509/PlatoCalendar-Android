package pusan.university.plato_calendar.presentation.widget.callback

import android.content.Context
import android.content.Intent
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import pusan.university.plato_calendar.presentation.PlatoCalendarActivity
import pusan.university.plato_calendar.presentation.common.notification.AlarmScheduler

class OpenScheduleDetailCallback : ActionCallback {
    companion object {
        val scheduleIdKey = ActionParameters.Key<Long>("schedule_id")
    }

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters,
    ) {
        val scheduleId = parameters[scheduleIdKey] ?: return

        val intent =
            Intent(context, PlatoCalendarActivity::class.java).apply {
                putExtra(AlarmScheduler.EXTRA_SCHEDULE_ID, scheduleId)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }

        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
