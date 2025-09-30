package pnu.plato.calendar.presentation.setting

import dagger.hilt.android.lifecycle.HiltViewModel
import pnu.plato.calendar.presentation.common.base.BaseViewModel
import pnu.plato.calendar.presentation.common.manager.LoginManager
import pnu.plato.calendar.presentation.setting.intent.SettingEvent
import pnu.plato.calendar.presentation.setting.intent.SettingSideEffect
import pnu.plato.calendar.presentation.setting.intent.SettingState
import javax.inject.Inject

@HiltViewModel
class SettingViewModel
    @Inject
    constructor(
        val loginManager: LoginManager,
    ) : BaseViewModel<SettingState, SettingEvent, SettingSideEffect>(SettingState()) {
        override suspend fun handleEvent(event: SettingEvent) {
            when (event) {
                else -> Unit // TODO
            }
        }
    }
