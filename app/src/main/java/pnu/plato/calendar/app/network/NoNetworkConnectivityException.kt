package pnu.plato.calendar.app.network

import java.io.IOException

class NoNetworkConnectivityException : IOException(NETWORK_ERROR_MESSAGE) {
    companion object Companion {
        const val NETWORK_ERROR_MESSAGE = "네트워크 연결을 확인해 주세요."
    }
}
