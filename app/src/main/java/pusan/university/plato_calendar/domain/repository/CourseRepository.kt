package pusan.university.plato_calendar.domain.repository

interface CourseRepository {
    fun getCourseName(courseCode: String): String

    fun getCourseCode(courseName: String): String
}
