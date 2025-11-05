package pusan.university.plato_calendar.data.local.database

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class CompletedScheduleDataStore
@Inject
constructor(
    private val context: Context,
) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = COMPLETED_SCHEDULES_NAME)

    val completedScheduleIds: Flow<Set<Long>> =
        context
            .dataStore
            .data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                preferences[KEY_COMPLETED_SCHEDULE_IDS]
                    ?.mapNotNull { it.toLongOrNull() }
                    ?.toSet()
                    ?: emptySet()
            }

    suspend fun addCompletedSchedule(id: Long) {
        context.dataStore.edit { prefs ->
            val currentIds = prefs[KEY_COMPLETED_SCHEDULE_IDS] ?: emptySet()
            prefs[KEY_COMPLETED_SCHEDULE_IDS] = currentIds + id.toString()
        }
    }

    suspend fun removeCompletedSchedule(id: Long) {
        context.dataStore.edit { prefs ->
            val currentIds = prefs[KEY_COMPLETED_SCHEDULE_IDS] ?: emptySet()
            prefs[KEY_COMPLETED_SCHEDULE_IDS] = currentIds - id.toString()
        }
    }

    companion object {
        private const val COMPLETED_SCHEDULES_NAME = "completed_schedules"
        private val KEY_COMPLETED_SCHEDULE_IDS = stringSetPreferencesKey("completed_schedule_ids")
    }
}
