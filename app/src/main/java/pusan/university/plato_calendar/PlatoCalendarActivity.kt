package pusan.university.plato_calendar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import pusan.university.plato_calendar.domain.repository.CalendarRepository
import pusan.university.plato_calendar.domain.repository.LoginRepository
import pusan.university.plato_calendar.domain.repository.SubjectRepository
import pusan.university.plato_calendar.ui.theme.PlatoCalendarAOSTheme
import javax.inject.Inject

@AndroidEntryPoint
class PlatoCalendarActivity : ComponentActivity() {
    private lateinit var moodleSession: String
    private lateinit var sessKey: String

    @Inject
    lateinit var loginRepository: LoginRepository

    @Inject
    lateinit var subjectRepository: SubjectRepository

    @Inject
    lateinit var calendarRepository: CalendarRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        lifecycleScope.launch {
            loginRepository.login("202055643", "mxkuy0508!")
                .onSuccess { session ->
                    moodleSession = session
                    println("Login Success. MoodleSession=$session")
                }.onFailure {
                    println("Login Failed : $it")
                }
        }

        setContent {
            PlatoCalendarAOSTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PlatoCalendarAOSTheme {
        Greeting("Android")
    }
}