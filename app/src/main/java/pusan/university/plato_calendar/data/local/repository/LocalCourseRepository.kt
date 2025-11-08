package pusan.university.plato_calendar.data.local.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import pusan.university.plato_calendar.domain.repository.CourseRepository
import pusan.university.plato_calendar.presentation.common.extension.formatCourseCode
import javax.inject.Inject

private const val UNKNOWN_COURSE_NAME = "알 수 없는 교과목"

class LocalCourseRepository
@Inject
constructor(
    @ApplicationContext context: Context,
) : CourseRepository {
    private val courses: Map<String, String> by lazy {
        val jsonString =
            context.assets
                .open("courses.json")
                .bufferedReader()
                .use { it.readText() }

        // Create a concrete type to avoid ProGuard issues
        val type = object : TypeToken<Map<String, String>>() {}.type
        Gson().fromJson(jsonString, type)
    }

    override fun getCourseName(courseCode: String): String =
        courses.entries
            .find { course ->
                course.key.formatCourseCode() == courseCode
            }?.value ?: UNKNOWN_COURSE_NAME

    override fun getCourseCode(courseName: String): String =
        courses.entries
            .find { course ->
                course.value == courseName
            }?.key?.formatCourseCode().orEmpty()
}
