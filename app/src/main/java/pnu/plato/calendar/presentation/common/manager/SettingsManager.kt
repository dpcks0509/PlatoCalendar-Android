package pnu.plato.calendar.presentation.common.manager

import kotlinx.coroutines.flow.Flow
import pnu.plato.calendar.data.local.database.SettingsDataStore
import pnu.plato.calendar.domain.entity.AppSettings
import pnu.plato.calendar.presentation.setting.model.NotificationTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsManager
@Inject
constructor(
    private val settingsDataStore: SettingsDataStore,
) {
    val appSettings: Flow<AppSettings> = settingsDataStore.settings

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        settingsDataStore.setNotificationsEnabled(enabled)
    }

    suspend fun setAcademicScheduleEnabled(enabled: Boolean) {
        settingsDataStore.setAcademicScheduleEnabled(enabled)
    }

    suspend fun setFirstReminderTime(time: NotificationTime) {
        settingsDataStore.setFirstReminderTime(time)
    }

    suspend fun setSecondReminderTime(time: NotificationTime) {
        settingsDataStore.setSecondReminderTime(time)
    }
}
