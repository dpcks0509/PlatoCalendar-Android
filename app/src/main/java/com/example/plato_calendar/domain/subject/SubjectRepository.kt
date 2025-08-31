package com.example.plato_calendar.domain.subject

interface SubjectRepository {
    fun getSubjectName(subjectId: String): String
}