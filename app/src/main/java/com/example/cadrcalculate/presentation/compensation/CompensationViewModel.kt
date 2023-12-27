package com.example.cadrcalculate.presentation.compensation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.time.LocalDate
import java.time.Period

class CompensationViewModel : ViewModel() {
    private val _startDay = MutableLiveData<LocalDate>()
    val startDay: LiveData<LocalDate>
        get() = _startDay
    private val _endDay = MutableLiveData<LocalDate>()
    val endDay: LiveData<LocalDate>
        get() = _endDay
    private val _holidays = MutableLiveData<Int>()
    private val holidays: LiveData<Int>
        get() = _holidays

    private val _answer = MutableLiveData<String>()
    val answer: LiveData<String>
        get() = _answer

    fun setStartDay(startDay: LocalDate) {
        _startDay.value = startDay
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setStartDay(startDay: String) {
        val splitByDot = startDay.split(".")
        val localD =
            LocalDate.of(splitByDot[2].toInt(), splitByDot[1].toInt(), splitByDot[0].toInt())
        if (this.startDay.value != localD) _startDay.value = localD
    }

    fun setEndDay(endDay: LocalDate) {
        _endDay.value = endDay
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setEndDay(endDay: String) {
        val splitByDot = endDay.split(".")
        val localD =
            LocalDate.of(splitByDot[2].toInt(), splitByDot[1].toInt(), splitByDot[0].toInt())
        if (this.endDay.value != localD) _endDay.value = localD
    }

    fun setHolidays(holidays: Int) {
        _holidays.value = holidays
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun calculateCompensation() {

        if (startDay.value == null || endDay.value == null) {
            _answer.value = "Укажите даты приёма на работу и увольнения."
        } else {
            val amountOfMonth = (holidays.value ?: 0) / 3
            val endWithCorrect = endDay.value?.minusMonths(amountOfMonth.toLong())
            val period = Period.between(startDay.value, endWithCorrect)
            if (period.years < 0) {
                _answer.value = "Некорректно указаны даты приёма на работу и увольнения."
            } else {
                _answer.value =
                    "${period.years}  ${correctYear(period.years)}," +
                            "\n${period.months} ${correctMonth(period.months)}," +
                            "\n${period.days} ${correctDays(period.days)}"
            }
        }
    }
}

private fun correctYear(int: Int): String {
    return when (int) {
        0 -> "лет"
        1 -> "год"
        in 2..4 -> "года"
        in 5..20 -> "лет"
        in 21..24 -> "года"
        in 25..30 -> "лет"
        in 31..34 -> "года"
        in 35..40 -> "лет"
        in 41..44 -> "года"
        in 45..50 -> "лет"
        else -> "моё почтение"
    }
}

private fun correctMonth(int: Int): String {
    return when (int) {
        0, in 5..12 -> "месяцев"
        1 -> "месяц"
        2, 3, 4 -> "месяца"
        else -> ""
    }
}

private fun correctDays(int: Int): String {
    return when (int) {
        0 -> "дней"
        1, 21, 31 -> "день"
        in 2..4 -> "дня"
        in 5..20 -> "дней"
        in 22..24 -> "дня"
        in 25..30 -> "дней"
        else -> ""
    }
}

