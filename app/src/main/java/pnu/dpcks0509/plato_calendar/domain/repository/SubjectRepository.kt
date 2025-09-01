package pnu.dpcks0509.plato_calendar.domain.repository

interface SubjectRepository {
    fun getSubjectName(subjectId: String): String
}