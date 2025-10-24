package pnu.plato.calendar.presentation.common.component.bottomsheet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.ads.AdView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import pnu.plato.calendar.domain.entity.Schedule.NewSchedule
import pnu.plato.calendar.presentation.calendar.model.PickerTarget
import pnu.plato.calendar.presentation.common.component.BannerAd
import pnu.plato.calendar.presentation.common.component.TimePickerDialog
import pnu.plato.calendar.presentation.common.eventbus.ToastEventBus
import pnu.plato.calendar.presentation.common.extension.formatTimeWithMidnightSpecialCase
import pnu.plato.calendar.presentation.common.extension.noRippleClickable
import pnu.plato.calendar.presentation.common.theme.Black
import pnu.plato.calendar.presentation.common.theme.Gray
import pnu.plato.calendar.presentation.common.theme.LightGray
import pnu.plato.calendar.presentation.common.theme.PrimaryColor
import pnu.plato.calendar.presentation.common.theme.White
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

private const val TITLE_INPUT_HINT = "제목을 입력해주세요."
private const val TITLE = "제목"
private const val DESCRIPTION = "설명"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewScheduleContent(
    selectedDate: LocalDate,
    adView: AdView,
    coroutineScope: CoroutineScope,
    makeSchedule: (NewSchedule) -> Unit,
    onDismissRequest: () -> Unit
) {
    val color = PrimaryColor

    var title: String by remember { mutableStateOf("") }
    var description: String by remember { mutableStateOf("") }

    val initialStartTime =
        remember(selectedDate) {
            val today = LocalDateTime.now()
            if (selectedDate == today.toLocalDate()) {
                today
            } else {
                LocalDateTime.of(selectedDate, LocalTime.of(9, 0))
            }
        }

    var startAt: LocalDateTime by remember { mutableStateOf(initialStartTime) }
    var endAt: LocalDateTime by remember { mutableStateOf(initialStartTime.plusHours(1)) }

    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    var timePickerFor by remember { mutableStateOf<PickerTarget?>(null) }

    val zoneId = ZoneId.systemDefault()
    val today = LocalDateTime.now().toLocalDate()
    val currentMonthStart = LocalDate.of(today.year, today.monthValue, 1)
    val minDate = minOf(today.minusDays(5), currentMonthStart)
    val maxDate = remember(today) { today.plusYears(1).minusDays(1) }

    val selectableDates =
        remember(minDate, maxDate) {
            object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    val date = Instant.ofEpochMilli(utcTimeMillis).atZone(zoneId).toLocalDate()
                    val notBefore = !date.isBefore(minDate)
                    val notAfter = !date.isAfter(maxDate)
                    return notBefore && notAfter
                }

                override fun isSelectableYear(year: Int): Boolean =
                    year in minDate.year..maxDate.year
            }
        }

    fun initialMillisFor(dateTime: LocalDateTime): Long {
        val date = dateTime.toLocalDate()
        val clamped =
            when {
                date.isBefore(minDate) -> minDate
                date.isAfter(maxDate) -> maxDate
                else -> date
            }

        return clamped.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
    }

    val dateFormatter = DateTimeFormatter.ofPattern("M월 d일 (E)", Locale.KOREAN)
    val formattedStartDate = remember(startAt) { startAt.format(dateFormatter) }
    val formattedStartTime = remember(startAt) { startAt.formatTimeWithMidnightSpecialCase() }
    val formattedEndDate = remember(endAt) { endAt.format(dateFormatter) }
    val formattedEndTime = remember(endAt) { endAt.formatTimeWithMidnightSpecialCase() }
    val formattedStartYear = remember(startAt) { "${startAt.year}년" }
    val formattedEndYear = remember(endAt) { "${endAt.year}년" }

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(color)
                .padding(top = 12.dp, bottom = 8.dp, start = 16.dp, end = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
            contentDescription = "Back",
            tint = White,
            modifier =
                Modifier
                    .size(32.dp)
                    .noRippleClickable(onDismissRequest),
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "일정 생성",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = White,
            modifier = Modifier.weight(1f),
        )

        ActionButton(
            text = "저장",
            onClick = {
                if (title.isNotEmpty()) {
                    makeSchedule(
                        NewSchedule(
                            title = title,
                            description = description,
                            startAt = startAt,
                            endAt = endAt,
                        ),
                    )
                } else {
                    coroutineScope.launch { ToastEventBus.sendError(TITLE_INPUT_HINT) }
                }
            },
        )
    }

    Spacer(modifier = Modifier.height(12.dp))

    Row(
        modifier =
            Modifier
                .padding(horizontal = 12.dp)
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .shadow(
                    elevation = 3.dp,
                    shape = RoundedCornerShape(16.dp),
                    clip = true,
                    ambientColor = Black,
                    spotColor = Black,
                )
                .background(White),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(modifier = Modifier.width(12.dp))

        Box(
            modifier =
                Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .padding(vertical = 12.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(color),
        )

        TextField(
            value = title,
            onValueChange = { newValue ->
                val filteredValue = newValue.replace("\n", "")
                if (filteredValue.length <= 67) {
                    title = filteredValue
                }
            },
            placeholder = {
                Text(
                    text = TITLE,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = color
                )
            },
            textStyle =
                TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Black,
                ),
            colors =
                TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    cursorColor = color,
                ),
            maxLines = 3,
            modifier = Modifier.fillMaxWidth(),
        )
    }

    Spacer(modifier = Modifier.height(12.dp))

    Column(
        modifier =
            Modifier
                .padding(horizontal = 12.dp)
                .fillMaxWidth()
                .shadow(
                    elevation = 3.dp,
                    shape = RoundedCornerShape(16.dp),
                    clip = true,
                    ambientColor = Black,
                    spotColor = Black,
                )
                .background(White)
                .padding(vertical = 18.dp, horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Description",
                tint = Black,
                modifier = Modifier.size(24.dp),
            )

            TextField(
                value = description,
                onValueChange = { newValue ->
                    val filteredValue = newValue.replace("\n", "")
                    if (filteredValue.length <= 63) {
                        description = filteredValue
                    }
                },
                placeholder = {
                    Text(
                        text = DESCRIPTION,
                        fontSize = 16.sp,
                        color = Gray,
                    )
                },
                textStyle =
                    TextStyle(
                        fontSize = 16.sp,
                        color = Black,
                    ),
                colors =
                    TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        cursorColor = color,
                    ),
                maxLines = 5,
                modifier = Modifier.weight(1f),
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = "Date",
                tint = Black,
                modifier = Modifier.size(24.dp),
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                modifier =
                    Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(LightGray)
                        .padding(vertical = 4.dp)
                        .noRippleClickable { showStartDatePicker = true },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = formattedStartYear,
                    fontSize = 14.sp,
                    color = Gray,
                )
                Text(
                    text = formattedStartDate,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Black,
                )
                Text(
                    text = formattedStartTime,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Black,
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Arrow",
                tint = Black,
                modifier = Modifier.size(24.dp),
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                modifier =
                    Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(LightGray)
                        .padding(vertical = 4.dp)
                        .noRippleClickable { showEndDatePicker = true },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = formattedEndYear,
                    fontSize = 14.sp,
                    color = Gray,
                )
                Text(
                    text = formattedEndDate,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Black,
                )
                Text(
                    text = formattedEndTime,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Black,
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    BannerAd(
        adView = adView,
        modifier = Modifier.fillMaxWidth(),
    )

    Spacer(modifier = Modifier.height(24.dp))

    if (showStartDatePicker) {
        val datePickerState =
            rememberDatePickerState(
                initialSelectedDateMillis = initialMillisFor(startAt),
                selectableDates = selectableDates,
            )
        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val millis = datePickerState.selectedDateMillis
                        if (millis != null) {
                            val pickedDate: LocalDate =
                                Instant.ofEpochMilli(millis).atZone(zoneId).toLocalDate()
                            startAt = LocalDateTime.of(pickedDate, startAt.toLocalTime())
                            if (endAt.isBefore(startAt)) endAt = startAt.plusHours(1)
                            timePickerFor = PickerTarget.START
                        }
                        showStartDatePicker = false
                    },
                ) { Text(text = "확인") }
            },
            dismissButton = {
                TextButton(onClick = { showStartDatePicker = false }) { Text(text = "취소") }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showEndDatePicker) {
        val datePickerState =
            rememberDatePickerState(
                initialSelectedDateMillis = initialMillisFor(endAt),
                selectableDates = selectableDates,
            )
        DatePickerDialog(
            onDismissRequest = { showEndDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val millis = datePickerState.selectedDateMillis
                        if (millis != null) {
                            val pickedDate: LocalDate =
                                Instant.ofEpochMilli(millis).atZone(zoneId).toLocalDate()
                            endAt = LocalDateTime.of(pickedDate, endAt.toLocalTime())
                            if (endAt.isBefore(startAt)) startAt = endAt.minusHours(1)
                            timePickerFor = PickerTarget.END
                        }
                        showEndDatePicker = false
                    },
                ) { Text(text = "확인") }
            },
            dismissButton = {
                TextButton(onClick = { showEndDatePicker = false }) { Text(text = "취소") }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }

    timePickerFor?.let { target ->
        val initialDateTime = if (target == PickerTarget.START) startAt else endAt

        TimePickerDialog(
            initialHour = initialDateTime.hour,
            initialMinute = initialDateTime.minute,
            onDismiss = { timePickerFor = null },
            onConfirm = { hour, minute ->
                val updated =
                    initialDateTime
                        .withHour(hour)
                        .withMinute(minute)
                if (target == PickerTarget.START) {
                    startAt = updated
                    if (endAt.isBefore(startAt)) endAt = startAt.plusHours(1)
                } else {
                    endAt = updated
                    if (endAt.isBefore(startAt)) startAt = endAt.minusHours(1)
                }
                timePickerFor = null
            },
        )
    }
}
