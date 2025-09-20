package pnu.plato.calendar.presentation.calendar.model

data class YearMonth(
    val year: Int,
    val month: Int,
) {
    fun plusMonths(monthsToAdd: Int): YearMonth {
        val newMonth = this.month + monthsToAdd
        val newYear = this.year + (newMonth - 1) / 12
        return YearMonth(newYear, (newMonth - 1) % 12 + 1)
    }
}
