package pnu.plato.calendar.data.remote.repository

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import pnu.plato.calendar.data.remote.service.AcademicScheduleService
import pnu.plato.calendar.data.remote.service.PersonalScheduleService
import pnu.plato.calendar.data.request.CreatePersonalScheduleArgs
import pnu.plato.calendar.data.request.CreatePersonalScheduleRequest
import pnu.plato.calendar.data.request.DeletePersonalScheduleArgs
import pnu.plato.calendar.data.request.DeletePersonalScheduleEvent
import pnu.plato.calendar.data.request.DeletePersonalScheduleRequest
import pnu.plato.calendar.data.request.UpdatePersonalScheduleArgs
import pnu.plato.calendar.data.request.UpdatePersonalScheduleRequest
import pnu.plato.calendar.domain.entity.LoginStatus
import pnu.plato.calendar.domain.entity.Schedule.AcademicSchedule
import pnu.plato.calendar.domain.entity.Schedule.NewSchedule
import pnu.plato.calendar.domain.entity.Schedule.PersonalSchedule
import pnu.plato.calendar.domain.entity.Schedule.PersonalSchedule.CourseSchedule
import pnu.plato.calendar.domain.entity.Schedule.PersonalSchedule.CustomSchedule
import pnu.plato.calendar.domain.repository.ScheduleRepository
import pnu.plato.calendar.presentation.common.manager.LoginManager
import java.net.URLEncoder
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.inject.Inject

class RemoteScheduleRepository
    @Inject
    constructor(
        private val personalScheduleService: PersonalScheduleService,
        private val academicScheduleService: AcademicScheduleService,
        private val loginManager: LoginManager,
    ) : ScheduleRepository {
        override suspend fun getAcademicSchedules(): Result<List<AcademicSchedule>> {
            val response = academicScheduleService.readAcademicSchedules()

            if (response.isSuccessful) {
                val responseBody = response.body()?.string()
                if (responseBody.isNullOrBlank()) {
                    return Result.success(emptyList())
                }

                val academicSchedules = responseBody.parseHtmlToAcademicSchedules()
                return Result.success(academicSchedules)
            }

            return Result.failure(Exception(GET_SCHEDULES_FAILED_ERROR))
        }

        override suspend fun getPersonalSchedules(sessKey: String): Result<List<PersonalSchedule>> =
            coroutineScope {
                val currentMonthSchedules = async { getCurrentMonthPersonalSchedules(sessKey) }.await()
                val yearSchedules = async { getYearPersonalSchedules(sessKey) }.await()
                val personalSchedules =
                    (currentMonthSchedules + yearSchedules).distinctBy { schedule -> schedule.id }

                Result.success(personalSchedules)
            }

        override suspend fun makeCustomSchedule(newSchedule: NewSchedule): Result<Long> {
            val loginStatus = loginManager.loginStatus.value

            if (loginStatus is LoginStatus.Login) {
                val sessKey = loginStatus.loginSession.sessKey

                val body =
                    buildCreatePersonalScheduleRequest(
                        userId = loginStatus.loginSession.userId,
                        sessKey = sessKey,
                        newSchedule = newSchedule,
                    )

                val response =
                    personalScheduleService.createCustomSchedule(
                        sessKey = sessKey,
                        request = body,
                    )

                if (response.isSuccessful) {
                    val responseBody =
                        response.body()?.firstOrNull() ?: return Result.failure(
                            Exception(CREATE_SCHEDULE_FAILED_ERROR),
                        )
                    if (responseBody.error) {
                        return Result.failure(Exception(CREATE_SCHEDULE_FAILED_ERROR))
                    }
                    val id = responseBody.data.event.id

                    return Result.success(id)
                }
            }

            return Result.failure(Exception(CREATE_SCHEDULE_FAILED_ERROR))
        }

        override suspend fun editPersonalSchedule(personalSchedule: PersonalSchedule): Result<Unit> {
            val loginStatus = loginManager.loginStatus.value

            if (loginStatus is LoginStatus.Login) {
                val sessKey = loginStatus.loginSession.sessKey

                val response =
                    personalScheduleService.updatePersonalSchedule(
                        sessKey = sessKey,
                        request =
                            buildUpdatePersonalScheduleRequest(
                                id = personalSchedule.id,
                                userId = loginStatus.loginSession.userId,
                                sessKey = sessKey,
                                name = personalSchedule.title,
                                startDateTime = personalSchedule.startAt,
                                endDateTime = personalSchedule.endAt,
                                description = personalSchedule.description.orEmpty(),
                            ),
                    )

                if (response.isSuccessful) {
                    val responseBody =
                        response.body()?.firstOrNull() ?: return Result.failure(
                            Exception(UPDATE_SCHEDULE_FAILED_ERROR),
                        )
                    if (responseBody.error) {
                        return Result.failure(Exception(UPDATE_SCHEDULE_FAILED_ERROR))
                    }

                    return Result.success(Unit)
                }
            }

            return Result.failure(Exception(UPDATE_SCHEDULE_FAILED_ERROR))
        }

        override suspend fun deleteCustomSchedule(id: Long): Result<Unit> {
            val loginStatus = loginManager.loginStatus.value

            if (loginStatus is LoginStatus.Login) {
                val sessKey = loginStatus.loginSession.sessKey

                val response =
                    personalScheduleService.deleteCustomSchedule(
                        sessKey = sessKey,
                        request = buildDeletePersonalScheduleRequest(eventId = id),
                    )

                if (response.isSuccessful) {
                    val responseBody =
                        response.body()?.firstOrNull() ?: return Result.failure(
                            Exception(DELETE_SCHEDULE_FAILED_ERROR),
                        )
                    if (responseBody.error) {
                        return Result.failure(Exception(DELETE_SCHEDULE_FAILED_ERROR))
                    }

                    return Result.success(Unit)
                }
            }

            return Result.failure(Exception(DELETE_SCHEDULE_FAILED_ERROR))
        }

        private suspend fun getCurrentMonthPersonalSchedules(sessKey: String): List<PersonalSchedule> {
            val currentMonthResponse =
                personalScheduleService.readCurrentMonthPersonalSchedules(sessKey = sessKey)
            return if (currentMonthResponse.isSuccessful) {
                val currentMonthResponseBody = currentMonthResponse.body()?.string()
                if (currentMonthResponseBody.isNullOrBlank()) {
                    emptyList()
                } else {
                    currentMonthResponseBody.parseIcsToPersonalSchedules()
                }
            } else {
                emptyList()
            }
        }

        private suspend fun getYearPersonalSchedules(sessKey: String): List<PersonalSchedule> {
            val yearResponse =
                personalScheduleService.readYearPersonalSchedules(sessKey = sessKey)
            return if (yearResponse.isSuccessful) {
                val yearResponseBody = yearResponse.body()?.string()
                if (yearResponseBody.isNullOrBlank()) {
                    emptyList()
                } else {
                    yearResponseBody.parseIcsToPersonalSchedules()
                }
            } else {
                emptyList()
            }
        }

        companion object {
            private const val GET_SCHEDULES_FAILED_ERROR = "일정을 가져오는데 실패했습니다."
            private const val CREATE_SCHEDULE_FAILED_ERROR = "일정을 등록하는데 실패했습니다."
            private const val UPDATE_SCHEDULE_FAILED_ERROR = "일정을 수정하는데 실패했습니다."
            private const val DELETE_SCHEDULE_FAILED_ERROR = "일정을 삭제하는데 실패했습니다."

            private fun String.parseIcsToPersonalSchedules(): List<PersonalSchedule> {
                val unfoldedLines = mutableListOf<String>()
                lines().forEach { rawLine ->
                    if (rawLine.startsWith(" ") && unfoldedLines.isNotEmpty()) {
                        val previous = unfoldedLines.removeAt(unfoldedLines.lastIndex)
                        unfoldedLines.add(previous + rawLine.trimStart())
                    } else {
                        unfoldedLines.add(rawLine)
                    }
                }

                val personalSchedules = mutableListOf<PersonalSchedule>()
                var inEvent = false
                val currentFields = mutableMapOf<String, String>()

                unfoldedLines.forEach { line ->
                    val trimmed = line.trim()
                    when {
                        trimmed.equals("BEGIN:VEVENT", ignoreCase = true) -> {
                            inEvent = true
                            currentFields.clear()
                        }

                        trimmed.equals("END:VEVENT", ignoreCase = true) -> {
                            if (inEvent) {
                                personalSchedules.add(buildScheduleFromFields(currentFields.toMap()))
                            }
                            inEvent = false
                            currentFields.clear()
                        }

                        inEvent -> {
                            val colonIndex = trimmed.indexOf(':')
                            if (colonIndex > 0) {
                                val key = trimmed.substring(0, colonIndex).substringBefore(';').uppercase()
                                val value = trimmed.substring(colonIndex + 1)
                                currentFields[key] = value
                            }
                        }
                    }
                }

                return personalSchedules
            }

            private fun buildScheduleFromFields(fields: Map<String, String>): PersonalSchedule {
                val courseCode = fields["CATEGORIES"]?.split("_")[2]
                val description = fields["DESCRIPTION"]?.processIcsDescription()

                return if (courseCode == null) {
                    CustomSchedule(
                        id = fields["UID"].orEmpty().split("@")[0].toLong(),
                        title = fields["SUMMARY"].orEmpty(),
                        description = description,
                        startAt = fields["DTSTART"].orEmpty().parseUtcToKstLocalDateTime(),
                        endAt = fields["DTEND"].orEmpty().parseUtcToKstLocalDateTime(),
                    )
                } else {
                    CourseSchedule(
                        id = fields["UID"].orEmpty().split("@")[0].toLong(),
                        title = fields["SUMMARY"].orEmpty(),
                        description = description,
                        startAt = fields["DTSTART"].orEmpty().parseUtcToKstLocalDateTime(),
                        endAt = fields["DTEND"].orEmpty().parseUtcToKstLocalDateTime(),
                        courseCode = fields["CATEGORIES"]?.split("_")[2].toString(),
                    )
                }
            }

            private fun String.processIcsDescription(): String {
                return this
                    .replace(Regex("(\\\\n){2,}"), "\\\\n")
                    .replace(Regex("^(\\\\n)+"), "")
                    .replace(Regex("(\\\\n)+$"), "")
                    .replace("\\\\", "\u0001")
                    .replace("\\n", "\n")
                    .replace("\\r", "\r")
                    .replace("\\t", "\t")
                    .replace("\\;", ";")
                    .replace("\\,", ",")
                    .replace("\u0001", "\\")
            }

            private fun String.parseUtcToKstLocalDateTime(): LocalDateTime {
                val year = substring(0, 4).toInt()
                val month = substring(4, 6).toInt()
                val day = substring(6, 8).toInt()
                val hour = substring(9, 11).toInt()
                val minute = substring(11, 13).toInt()

                val utcTime = LocalDateTime.of(year, month, day, hour, minute)
                val kstTime = utcTime.atOffset(ZoneOffset.UTC).plusHours(9).toLocalDateTime()

                return kstTime
            }

            private fun String.parseHtmlToAcademicSchedules(): List<AcademicSchedule> {
                val academicSchedules = mutableListOf<AcademicSchedule>()

                val tableRows = this.split("<tr>").drop(1)

                tableRows.forEach { row ->
                    if (row.contains("class=\"term\"") && row.contains("class=\"text\"")) {
                        val termMatch = Regex("class=\"term\"[^>]*>([^<]+)</").find(row)
                        val termText = termMatch?.groupValues?.get(1)?.trim()

                        val textMatch = Regex("class=\"text\"[^>]*>([^<]+)</").find(row)
                        val textContent = textMatch?.groupValues?.get(1)?.trim()

                        if (termText != null && textContent != null) {
                            val (startAt, endAt) = termText.parseDateRange() ?: return@forEach

                            academicSchedules.add(
                                AcademicSchedule(
                                    title = textContent,
                                    startAt = startAt,
                                    endAt = endAt,
                                ),
                            )
                        }
                    }
                }

                return academicSchedules
            }

            private fun String.parseDateRange(): Pair<LocalDate, LocalDate>? {
                val dates = this.split(" - ").map { it.trim() }
                if (dates.size != 2) return null

                val startDate = dates[0].parseKoreanDateToLocalDate() ?: return null
                val endDate = dates[1].parseKoreanDateToLocalDate() ?: return null

                return startDate to endDate
            }

            private fun String.parseKoreanDateToLocalDate(): LocalDate? {
                val parts = this.split(".")
                if (parts.size != 3) return null

                val year = parts.getOrNull(0)?.toIntOrNull() ?: return null
                val month = parts.getOrNull(1)?.toIntOrNull() ?: return null
                val day = parts.getOrNull(2)?.toIntOrNull() ?: return null

                if (month !in 1..12) return null
                if (day !in 1..31) return null

                return if (day <= LocalDate.of(year, month, 1).lengthOfMonth()) {
                    LocalDate.of(year, month, day)
                } else {
                    null
                }
            }

            private fun buildCreatePersonalScheduleRequest(
                userId: String,
                sessKey: String,
                newSchedule: NewSchedule,
            ): List<CreatePersonalScheduleRequest> {
                val encodedName = URLEncoder.encode(newSchedule.title, "UTF-8").replace("+", "%20")
                val encodedDescription =
                    URLEncoder.encode("<p>${newSchedule.description.orEmpty()}</p>", "UTF-8").replace("+", "%20")

                val formData =
                    buildString {
                        append("id=0&")
                        append("userid=$userId&")
                        append("modulename=&")
                        append("instance=0&")
                        append("visible=1&")
                        append("eventtype=user&")
                        append("sesskey=$sessKey&")
                        append("_qf__core_calendar_local_event_forms_create=1&")
                        append("mform_showmore_id_general=1&")
                        append("name=$encodedName&")
                        append("timestart%5Byear%5D=${newSchedule.startAt.year}&")
                        append("timestart%5Bmonth%5D=${newSchedule.startAt.monthValue}&")
                        append("timestart%5Bday%5D=${newSchedule.startAt.dayOfMonth}&")
                        append("timestart%5Bhour%5D=${newSchedule.startAt.hour}&")
                        append("timestart%5Bminute%5D=${newSchedule.startAt.minute}&")
                        append("description%5Btext%5D=$encodedDescription&")
                        append("description%5Bformat%5D=1&")
                        append("description%5Bitemid%5D=0&")
                        append("duration=1&")
                        append("timedurationuntil%5Byear%5D=${newSchedule.endAt.year}&")
                        append("timedurationuntil%5Bmonth%5D=${newSchedule.endAt.monthValue}&")
                        append("timedurationuntil%5Bday%5D=${newSchedule.endAt.dayOfMonth}&")
                        append("timedurationuntil%5Bhour%5D=${newSchedule.endAt.hour}&")
                        append("timedurationuntil%5Bminute%5D=${newSchedule.endAt.minute}")
                    }

                return listOf(CreatePersonalScheduleRequest(args = CreatePersonalScheduleArgs(formData = formData)))
            }

            private fun buildUpdatePersonalScheduleRequest(
                id: Long,
                userId: String,
                sessKey: String,
                name: String,
                startDateTime: LocalDateTime,
                endDateTime: LocalDateTime,
                description: String,
            ): List<UpdatePersonalScheduleRequest> {
                val encodedName = URLEncoder.encode(name, "UTF-8").replace("+", "%20")
                val encodedDescription =
                    URLEncoder.encode("<p>$description</p>", "UTF-8").replace("+", "%20")

                val formData =
                    buildString {
                        append("id=$id&")
                        append("userid=$userId&")
                        append("modulename=0&")
                        append("instance=0&")
                        append("visible=1&")
                        append("eventtype=user&")
                        append("repeatid=0&")
                        append("sesskey=$sessKey&")
                        append("_qf__core_calendar_local_event_forms_update=1&")
                        append("mform_showmore_id_general=0&")
                        append("name=$encodedName&")
                        append("timestart%5Byear%5D=${startDateTime.year}&")
                        append("timestart%5Bmonth%5D=${startDateTime.monthValue}&")
                        append("timestart%5Bday%5D=${startDateTime.dayOfMonth}&")
                        append("timestart%5Bhour%5D=${startDateTime.hour}&")
                        append("timestart%5Bminute%5D=${startDateTime.minute}&")
                        append("description%5Btext%5D=$encodedDescription&")
                        append("description%5Bformat%5D=1&")
                        append("description%5Bitemid%5D=0&")
                        append("duration=1&")
                        append("timedurationuntil%5Byear%5D=${endDateTime.year}&")
                        append("timedurationuntil%5Bmonth%5D=${endDateTime.monthValue}&")
                        append("timedurationuntil%5Bday%5D=${endDateTime.dayOfMonth}&")
                        append("timedurationuntil%5Bhour%5D=${endDateTime.hour}&")
                        append("timedurationuntil%5Bminute%5D=${endDateTime.minute}")
                    }

                return listOf(UpdatePersonalScheduleRequest(args = UpdatePersonalScheduleArgs(formData = formData)))
            }

            private fun buildDeletePersonalScheduleRequest(eventId: Long): List<DeletePersonalScheduleRequest> =
                listOf(
                    DeletePersonalScheduleRequest(
                        args =
                            DeletePersonalScheduleArgs(
                                events =
                                    listOf(
                                        DeletePersonalScheduleEvent(
                                            eventId = eventId,
                                            repeat = false,
                                        ),
                                    ),
                            ),
                    ),
                )
        }
    }
