package pnu.plato.calendar.domain.repository

interface CourseRepository {
    fun getCourseName(courseCode: String?): String
}