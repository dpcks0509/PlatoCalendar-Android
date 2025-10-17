package pnu.plato.calendar.data.local.database

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import pnu.plato.calendar.domain.entity.AppSettings
import pnu.plato.calendar.presentation.setting.model.NotificationTime
import java.io.IOException
import javax.inject.Inject

class SettingsDataStore
@Inject
constructor(
    private val context: Context,
) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = SETTINGS_NAME)

    val settings: Flow<AppSettings> =
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
                val notificationsEnabled = preferences[KEY_NOTIFICATIONS_ENABLED] ?: false
                val firstReminderName =
                    preferences[KEY_FIRST_REMINDER_TIME_NAME] ?: NotificationTime.ONE_HOUR.name
                val secondReminderName =
                    preferences[KEY_SECOND_REMINDER_TIME_NAME] ?: NotificationTime.NONE.name

                val firstReminderTime = runCatching { NotificationTime.valueOf(firstReminderName) }
                    .getOrDefault(NotificationTime.ONE_HOUR)
                val secondReminderTime =
                    runCatching { NotificationTime.valueOf(secondReminderName) }
                        .getOrDefault(NotificationTime.NONE)

                AppSettings(
                    notificationsEnabled = notificationsEnabled,
                    firstReminderTime = firstReminderTime,
                    secondReminderTime = secondReminderTime,
                )
            }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_NOTIFICATIONS_ENABLED] = enabled
        }
    }

    suspend fun setAcademicScheduleEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_ACADEMIC_SCHEDULE_ENABLED] = enabled
        }
    }

    suspend fun setFirstReminderTime(time: NotificationTime) {
        context.dataStore.edit { prefs ->
            prefs[KEY_FIRST_REMINDER_TIME_NAME] = time.name
        }
    }

    suspend fun setSecondReminderTime(time: NotificationTime) {
        context.dataStore.edit { prefs ->
            prefs[KEY_SECOND_REMINDER_TIME_NAME] = time.name
        }
    }

    companion object {
        private const val SETTINGS_NAME = "app_settings"
        private val KEY_NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        private val KEY_ACADEMIC_SCHEDULE_ENABLED =
            booleanPreferencesKey("academic_schedule_enabled")
        private val KEY_FIRST_REMINDER_TIME_NAME = stringPreferencesKey("first_reminder_time_name")
        private val KEY_SECOND_REMINDER_TIME_NAME =
            stringPreferencesKey("second_reminder_time_name")
    }
}
