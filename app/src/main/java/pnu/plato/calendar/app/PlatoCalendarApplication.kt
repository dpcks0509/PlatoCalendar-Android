package pnu.plato.calendar.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import pnu.plato.calendar.presentation.common.manager.NotificationSyncManager
import javax.inject.Inject

@HiltAndroidApp
class PlatoCalendarApplication : Application() {

    @Inject
    lateinit var notificationSyncManager: NotificationSyncManager

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()

        notificationSyncManager.startSync(applicationScope)
    }
}