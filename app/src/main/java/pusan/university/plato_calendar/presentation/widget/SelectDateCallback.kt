package pusan.university.plato_calendar.presentation.widget

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.state.updateAppWidgetState
import java.time.LocalDate

class SelectDateCallback : ActionCallback {
    companion object {
        val dateKey = ActionParameters.Key<String>("selected_date")
    }

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters,
    ) {
        val selectedDate = parameters[dateKey] ?: LocalDate.now().toString()

        updateAppWidgetState(context, glanceId) { prefs ->
            prefs[stringPreferencesKey("selected_date")] = selectedDate
        }

        CalendarWidget.update(context, glanceId)
    }
}
