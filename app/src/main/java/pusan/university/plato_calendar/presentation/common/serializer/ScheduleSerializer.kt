package pusan.university.plato_calendar.presentation.common.serializer

import org.json.JSONArray
import org.json.JSONObject
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel
import java.time.LocalDate
import java.time.LocalDateTime

object ScheduleSerializer {
    fun serializeSchedules(schedules: List<ScheduleUiModel>): String {
        val jsonArray = JSONArray()

        schedules.forEach { schedule ->
            val jsonObject = JSONObject()

            when (schedule) {
                is ScheduleUiModel.AcademicScheduleUiModel -> {
                    jsonObject.put("type", "academic")
                    jsonObject.put("title", schedule.title)
                    jsonObject.put("startAt", schedule.startAt.toString())
                    jsonObject.put("endAt", schedule.endAt.toString())
                }

                is ScheduleUiModel.PersonalScheduleUiModel.CourseScheduleUiModel -> {
                    jsonObject.put("type", "course")
                    jsonObject.put("id", schedule.id)
                    jsonObject.put("title", schedule.title)
                    jsonObject.put("description", schedule.description ?: "")
                    jsonObject.put("startAt", schedule.startAt.toString())
                    jsonObject.put("endAt", schedule.endAt.toString())
                    jsonObject.put("isCompleted", schedule.isCompleted)
                    jsonObject.put("courseName", schedule.courseName)
                }

                is ScheduleUiModel.PersonalScheduleUiModel.CustomScheduleUiModel -> {
                    jsonObject.put("type", "custom")
                    jsonObject.put("id", schedule.id)
                    jsonObject.put("title", schedule.title)
                    jsonObject.put("description", schedule.description ?: "")
                    jsonObject.put("startAt", schedule.startAt.toString())
                    jsonObject.put("endAt", schedule.endAt.toString())
                    jsonObject.put("isCompleted", schedule.isCompleted)
                }
            }

            jsonArray.put(jsonObject)
        }

        return jsonArray.toString()
    }

    fun deserializeSchedules(json: String): List<ScheduleUiModel> {
        if (json.isEmpty()) return emptyList()

        val jsonArray = JSONArray(json)
        val schedules = mutableListOf<ScheduleUiModel>()

        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val type = jsonObject.getString("type")

            val schedule =
                when (type) {
                    "academic" -> {
                        ScheduleUiModel.AcademicScheduleUiModel(
                            title = jsonObject.getString("title"),
                            startAt = LocalDate.parse(jsonObject.getString("startAt")),
                            endAt = LocalDate.parse(jsonObject.getString("endAt")),
                        )
                    }

                    "course" -> {
                        ScheduleUiModel.PersonalScheduleUiModel.CourseScheduleUiModel(
                            id = jsonObject.getLong("id"),
                            title = jsonObject.getString("title"),
                            description = jsonObject.getString("description").takeIf { it.isNotEmpty() },
                            startAt = LocalDateTime.parse(jsonObject.getString("startAt")),
                            endAt = LocalDateTime.parse(jsonObject.getString("endAt")),
                            isCompleted = jsonObject.getBoolean("isCompleted"),
                            courseName = jsonObject.getString("courseName"),
                        )
                    }

                    "custom" -> {
                        ScheduleUiModel.PersonalScheduleUiModel.CustomScheduleUiModel(
                            id = jsonObject.getLong("id"),
                            title = jsonObject.getString("title"),
                            description = jsonObject.getString("description").takeIf { it.isNotEmpty() },
                            startAt = LocalDateTime.parse(jsonObject.getString("startAt")),
                            endAt = LocalDateTime.parse(jsonObject.getString("endAt")),
                            isCompleted = jsonObject.getBoolean("isCompleted"),
                        )
                    }

                    else -> null
                }

            schedule?.let { schedules.add(it) }
        }

        return schedules
    }
}
