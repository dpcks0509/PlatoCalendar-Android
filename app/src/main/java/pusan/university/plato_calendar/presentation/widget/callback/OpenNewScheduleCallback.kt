package pusan.university.plato_calendar.presentation.widget.callback

import android.content.Context
import android.content.Intent
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import pusan.university.plato_calendar.presentation.PlatoCalendarActivity

class OpenNewScheduleCallback : ActionCallback {
    companion object {
        const val ACTION_OPEN_NEW_SCHEDULE = "pusan.university.plato_calendar.OPEN_NEW_SCHEDULE"
    }

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters,
    ) {
        val intent =
            Intent(context, PlatoCalendarActivity::class.java).apply {
                action = ACTION_OPEN_NEW_SCHEDULE
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
        context.startActivity(intent)
    }
}
