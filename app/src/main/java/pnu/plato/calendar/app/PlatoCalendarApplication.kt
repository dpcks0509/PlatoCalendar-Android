package pnu.plato.calendar.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import pnu.plato.calendar.presentation.common.manager.NotificationSyncManager
import pnu.plato.calendar.presentation.common.notification.NotificationHelper.Companion.CHANNEL_ID
import pnu.plato.calendar.presentation.common.notification.NotificationHelper.Companion.CHANNEL_NAME
import javax.inject.Inject

@HiltAndroidApp
class PlatoCalendarApplication : Application() {
    @Inject
    lateinit var notificationSyncManager: NotificationSyncManager

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()

        createNotificationChannel(applicationContext)
        notificationSyncManager.startSync(applicationScope)
    }

    private fun createNotificationChannel(context: Context) {
        val notificationManager =
            context.getSystemService(
                NotificationManager::class.java,
            )

        val channel =
            NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH,
            ).apply {
                enableLights(true)
                enableVibration(true)
                setShowBadge(true)
            }

        notificationManager.createNotificationChannel(channel)
    }
}
