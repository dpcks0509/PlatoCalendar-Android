package pusan.university.plato_calendar.domain.repository

interface SubjectRepository {
    fun getSubjectName(subjectId: String): String
}