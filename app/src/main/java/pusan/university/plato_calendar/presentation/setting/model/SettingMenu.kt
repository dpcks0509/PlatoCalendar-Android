package pusan.university.plato_calendar.presentation.setting.model

import pusan.university.plato_calendar.presentation.setting.model.SettingMenu.SettingContent.ACCOUNT_INFO
import pusan.university.plato_calendar.presentation.setting.model.SettingMenu.SettingContent.ANNOUNCEMENTS
import pusan.university.plato_calendar.presentation.setting.model.SettingMenu.SettingContent.CONTACT_US
import pusan.university.plato_calendar.presentation.setting.model.SettingMenu.SettingContent.FIRST_REMINDER
import pusan.university.plato_calendar.presentation.setting.model.SettingMenu.SettingContent.PRIVACY_POLICY
import pusan.university.plato_calendar.presentation.setting.model.SettingMenu.SettingContent.SECOND_REMINDER
import pusan.university.plato_calendar.presentation.setting.model.SettingMenu.SettingContent.TERMS_OF_SERVICE

enum class SettingMenu(
    val title: String,
    val items: List<SettingContent>,
) {
    ACCOUNT("계정", listOf(ACCOUNT_INFO)),
    NOTIFICATIONS(
        "알림",
        listOf(SettingContent.NOTIFICATIONS_ENABLED, FIRST_REMINDER, SECOND_REMINDER)
    ),
    USER_SUPPORT("사용자 지원", listOf(ANNOUNCEMENTS, CONTACT_US)),
    USAGE_GUIDE("이용 안내", listOf(TERMS_OF_SERVICE, PRIVACY_POLICY)),
    ;

    enum class SettingContent {
        ACCOUNT_INFO,
        NOTIFICATIONS_ENABLED,
        FIRST_REMINDER,
        SECOND_REMINDER,
        ANNOUNCEMENTS,
        CONTACT_US,
        TERMS_OF_SERVICE,
        PRIVACY_POLICY,
        ;

        fun getLabel(): String =
            when (this) {
                ACCOUNT_INFO -> ""
                NOTIFICATIONS_ENABLED -> "알림 허용하기"
                FIRST_REMINDER -> "알림"
                SECOND_REMINDER -> "두 번째 알림"
                ANNOUNCEMENTS -> "공지"
                CONTACT_US -> "문의하기"
                TERMS_OF_SERVICE -> "서비스 이용약관"
                PRIVACY_POLICY -> "개인정보 처리방침"
            }

        fun getUrl(): String? =
            when (this) {
                ANNOUNCEMENTS -> "https://glaze-mustang-7cf.notion.site/28057846cad680089524ea45cb9afce1"
                CONTACT_US -> "https://open.kakao.com/o/ge5fZ0Uh"
                TERMS_OF_SERVICE -> "https://glaze-mustang-7cf.notion.site/28057846cad680e9bc0bf5949bcc2d70"
                PRIVACY_POLICY -> "https://glaze-mustang-7cf.notion.site/28057846cad6803fb345cd2122efcd34"
                else -> null
            }
    }
}
