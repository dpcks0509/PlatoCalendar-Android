package pusan.university.plato_calendar.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import pusan.university.plato_calendar.domain.entity.LoginInfo
import pusan.university.plato_calendar.domain.repository.LoginRepository
import pusan.university.plato_calendar.presentation.theme.PlatoCalendarAOSTheme
import javax.inject.Inject

@AndroidEntryPoint
class PlatoCalendarActivity : ComponentActivity() {

    @Inject
    lateinit var loginRepository: LoginRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            PlatoCalendarAOSTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LoginLogoutScreen(
                        loginRepository = loginRepository,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun LoginLogoutScreen(
    loginRepository: LoginRepository,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var statusMessage by remember { mutableStateOf("Ready") }
    var loginInfo by remember { mutableStateOf<LoginInfo?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = {
                statusMessage = "Logging in..."
                coroutineScope.launch {
                    loginRepository.login("202055643", "mxkuy0508!")
                        .onSuccess { info ->
                            loginInfo = info
                            statusMessage = "Login Success: $info"
                        }
                        .onFailure { throwable ->
                            statusMessage = "Login Failed: ${throwable.message}"
                        }
                }
            },
        ) {
            Text(text = "Login")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                statusMessage = "Logging out..."
                coroutineScope.launch {
                    coroutineScope.launch {
                        loginRepository.logout(loginInfo?.sessKey ?: "123").onSuccess {
                            statusMessage = "Logout Success"
                            loginInfo = null
                        }.onFailure { throwable ->
                            statusMessage = "Logout Failed: ${throwable.message}"
                        }
                    }
                }
            },
        ) {
            Text(text = "Logout")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = statusMessage)
    }
}