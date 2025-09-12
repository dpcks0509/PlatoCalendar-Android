package pnu.plato.calendar.data.local.database

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class ScheduleCompleteDataStore
    @Inject
    constructor(
        private val context: Context,
    ) {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = SCHEDULE_COMPLETE_NAME)

        val completedScheduleIds: Flow<Set<String>> =
            context.dataStore.data
                .catch { exception ->
                    if (exception is IOException) {
                        emit(emptyPreferences())
                    } else {
                        throw exception
                    }
                }.map { preferences ->
                    preferences[COMPLETED_SCHEDULE_IDS] ?: emptySet()
                }

        suspend fun addCompletedSchedule(scheduleId: String) {
            context.dataStore.edit { preferences ->
                val currentSet = preferences[COMPLETED_SCHEDULE_IDS] ?: emptySet()
                preferences[COMPLETED_SCHEDULE_IDS] = currentSet + scheduleId
            }
        }

        suspend fun removeCompletedSchedule(scheduleId: String) {
            context.dataStore.edit { preferences ->
                val currentSet = preferences[COMPLETED_SCHEDULE_IDS] ?: emptySet()
                preferences[COMPLETED_SCHEDULE_IDS] = currentSet - scheduleId
            }
        }

        suspend fun isScheduleComplete(scheduleId: String): Boolean =
            completedScheduleIds
                .map { completedIds -> scheduleId in completedIds }
                .first()

        suspend fun clearAllCompletedSchedules() {
            context.dataStore.edit { preferences ->
                preferences.remove(COMPLETED_SCHEDULE_IDS)
            }
        }

        companion object {
            private const val SCHEDULE_COMPLETE_NAME = "schedule_complete"
            private val COMPLETED_SCHEDULE_IDS = stringSetPreferencesKey("completed_schedule_ids")
        }
    }
