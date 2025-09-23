package pnu.plato.calendar.data.local.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import pnu.plato.calendar.domain.repository.CourseRepository
import javax.inject.Inject

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

            Gson().fromJson(
                jsonString,
                object : TypeToken<Map<String, String>>() {}.type,
            )
        }

        override fun getCourseName(courseCode: String): String =
            courses.entries
                .find { course ->
                    (course.key.substring(0, 4) + course.key.substring(6, 9) == courseCode)
                }?.value
                .orEmpty()
    }
