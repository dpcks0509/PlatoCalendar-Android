package com.example.platocalendar_aos.domain.subject

interface SubjectRepository {
    fun getSubjectName(subjectId: String): String
}