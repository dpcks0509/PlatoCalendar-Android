package pusan.university.plato_calendar.presentation

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import pusan.university.plato_calendar.domain.repository.LoginRepository
import javax.inject.Inject

@HiltViewModel
class PlatoCalendarViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
    private val prefs: SharedPreferences
) : ViewModel() {
    init {

    }
}