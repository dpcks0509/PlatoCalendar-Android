package pnu.plato.calendar.data.remote.repository

import androidx.core.net.toUri
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import pnu.plato.calendar.data.remote.service.AcademicScheduleService
import pnu.plato.calendar.data.remote.service.PersonalScheduleService
import pnu.plato.calendar.domain.entity.AcademicSchedule
import pnu.plato.calendar.domain.entity.LoginStatus
import pnu.plato.calendar.domain.entity.PersonalSchedule
import pnu.plato.calendar.domain.repository.ScheduleRepository
import pnu.plato.calendar.presentation.common.function.parseHtmlToAcademicSchedules
import pnu.plato.calendar.presentation.common.function.parseIcsToPersonalSchedules
import pnu.plato.calendar.presentation.common.manager.LoginManager
import java.net.URLEncoder
import java.time.LocalDateTime
import javax.inject.Inject

class RemoteScheduleRepository
    @Inject
    constructor(
        private val personalScheduleService: PersonalScheduleService,
        private val academicScheduleService: AcademicScheduleService,
        private val loginManager: LoginManager,
    ) : ScheduleRepository {
        override suspend fun getAcademicSchedules(): Result<List<AcademicSchedule>> {
            val response = academicScheduleService.getAcademicSchedules()

            if (response.isSuccessful) {
                val body = response.body()?.string()
                if (body.isNullOrBlank()) {
                    return Result.success(emptyList())
                }

                val academicSchedules = body.parseHtmlToAcademicSchedules()
                return Result.success(academicSchedules)
            }

            return Result.failure(Exception(GET_SCHEDULES_FAILED_ERROR))
        }

        override suspend fun getPersonalSchedules(sessKey: String): Result<List<PersonalSchedule>> {
            val response = personalScheduleService.getPersonalSchedules(sessKey = sessKey)
            println(response)

            if (response.isSuccessful) {
                val body = response.body()?.string()
                if (body.isNullOrBlank()) {
                    return Result.success(emptyList())
                }

                val exportUrl =
                    response
                        .raw()
                        .request.url
                        .toString()
                val userId = exportUrl.toUri().getQueryParameter("userid")
                loginManager.setUserId(userId)

                val personalSchedules = body.parseIcsToPersonalSchedules()
                return Result.success(personalSchedules)
            }

            return Result.failure(Exception(GET_SCHEDULES_FAILED_ERROR))
        }

        override suspend fun createPersonalSchedule(
            title: String,
            description: String?,
            startAt: LocalDateTime,
            endAt: LocalDateTime,
        ): Result<Unit> {
            val loginStatus = loginManager.loginStatus.value

            if (loginStatus is LoginStatus.Login) {
                val body =
                    buildCreateScheduleBody(
                        userId = loginStatus.loginSession.userId.orEmpty(),
                        sessKey = loginStatus.loginSession.sessKey,
                        name = title,
                        startDateTime = startAt,
                        endDateTime = endAt,
                        description = description.orEmpty(),
                    )

                val response =
                    personalScheduleService.createPersonalSchedule(
                        sessKey = loginStatus.loginSession.sessKey,
                        body = body,
                    )

                println("body: $body")

                if (response.isSuccessful) {
                    return Result.success(Unit)
                }
            }

            return Result.failure(Exception(UPDATE_SCHEDULES_FAILED_ERROR))
        }

        override suspend fun updatePersonalSchedule(
            id: Long,
            title: String,
            description: String?,
            startAt: LocalDateTime,
            endAt: LocalDateTime,
        ): Result<Unit> {
            val loginStatus = loginManager.loginStatus.value

            if (loginStatus is LoginStatus.Login) {
                val response =
                    personalScheduleService.updatePersonalSchedule(
                        sessKey = loginStatus.loginSession.sessKey,
                        body =
                            buildUpdateScheduleBody(
                                id = id.toString(),
                                userId = loginStatus.loginSession.userId.orEmpty(),
                                sessKey = loginStatus.loginSession.sessKey,
                                name = title,
                                startDateTime = startAt,
                                endDateTime = endAt,
                                description = description.orEmpty(),
                            ),
                    )

                if (response.isSuccessful) {
                    return Result.success(Unit)
                }
            }

            return Result.failure(Exception(UPDATE_SCHEDULES_FAILED_ERROR))
        }

        override suspend fun deletePersonalSchedule(id: Long): Result<Unit> {
            TODO("Not yet implemented")
        }

        private fun buildCreateScheduleBody(
            userId: String,
            sessKey: String,
            name: String,
            startDateTime: LocalDateTime,
            endDateTime: LocalDateTime,
            description: String,
        ): JsonArray {
            val encodedDescription = URLEncoder.encode("<p>$description</p>", "UTF-8")

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
                    append("name=$name&")
                    append("timestart%5Byear%5D=${startDateTime.year}&")
                    append("timestart%5Bmonth%5D=${startDateTime.monthValue}&")
                    append("timestart%5Bday%5D=${startDateTime.dayOfMonth}&")
                    append("timestart%5Bhour%5D=${startDateTime.hour}&")
                    append("timestart%5Bminute%5D=${startDateTime.minute}&")
                    append("description%5Btext%5D=$encodedDescription&")
                    append("description%5Bformat%5D=1&")
                    append("description%5Bitemid%5D=759600809&")
                    append("duration=1&")
                    append("timedurationuntil%5Byear%5D=${endDateTime.year}&")
                    append("timedurationuntil%5Bmonth%5D=${endDateTime.monthValue}&")
                    append("timedurationuntil%5Bday%5D=${endDateTime.dayOfMonth}&")
                    append("timedurationuntil%5Bhour%5D=${endDateTime.hour}&")
                    append("timedurationuntil%5Bminute%5D=${endDateTime.minute}")
                }

            return buildJsonArray {
                add(
                    buildJsonObject {
                        put("index", 0)
                        put("methodname", "core_calendar_submit_create_update_form")
                        put(
                            "args",
                            buildJsonObject {
                                put("formdata", formData)
                            },
                        )
                    },
                )
            }
        }

        private fun buildUpdateScheduleBody(
            id: String,
            userId: String,
            sessKey: String,
            name: String,
            startDateTime: LocalDateTime,
            endDateTime: LocalDateTime,
            description: String,
        ): JsonArray {
            val encodedDescription = URLEncoder.encode("<p>$description</p>", "UTF-8")

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
                    append("name=$name&")
                    append("timestart%5Byear%5D=${startDateTime.year}&")
                    append("timestart%5Bmonth%5D=${startDateTime.monthValue}&")
                    append("timestart%5Bday%5D=${startDateTime.dayOfMonth}&")
                    append("timestart%5Bhour%5D=${startDateTime.hour}&")
                    append("timestart%5Bminute%5D=${startDateTime.minute}&")
                    append("description%5Btext%5D=$encodedDescription&")
                    append("description%5Bformat%5D=1&")
                    append("description%5Bitemid%5D=759600809&")
                    append("duration=1&")
                    append("timedurationuntil%5Byear%5D=${endDateTime.year}&")
                    append("timedurationuntil%5Bmonth%5D=${endDateTime.monthValue}&")
                    append("timedurationuntil%5Bday%5D=${endDateTime.dayOfMonth}&")
                    append("timedurationuntil%5Bhour%5D=${endDateTime.hour}&")
                    append("timedurationuntil%5Bminute%5D=${endDateTime.minute}")
                }

            return buildJsonArray {
                add(
                    buildJsonObject {
                        put("index", 0)
                        put("methodname", "core_calendar_submit_create_update_form")
                        put(
                            "args",
                            buildJsonObject {
                                put("formdata", formData)
                            },
                        )
                    },
                )
            }
        }

        /**
         * Create JSON body for deleting personal schedule
         */
        fun createDeleteScheduleBody(eventId: String): JsonArray =
            buildJsonArray {
                add(
                    buildJsonObject {
                        put("index", 0)
                        put("methodname", "core_calendar_delete_calendar_events")
                        put(
                            "args",
                            buildJsonObject {
                                put(
                                    "events",
                                    buildJsonArray {
                                        add(
                                            buildJsonObject {
                                                put("eventid", eventId)
                                                put("repeat", false)
                                            },
                                        )
                                    },
                                )
                            },
                        )
                    },
                )
            }

        companion object {
            private const val GET_SCHEDULES_FAILED_ERROR = "일정을 가져오는데 실패했습니다."
            private const val UPDATE_SCHEDULES_FAILED_ERROR = "일정을 등록하는데 실패했습니다."
        }
    }
