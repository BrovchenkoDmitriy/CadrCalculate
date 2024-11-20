package com.example.cadrcalculate.presentation.compensation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.time.LocalDate
import java.time.Period

class CompensationViewModel : ViewModel() {
    private val _startDay = MutableLiveData<LocalDate?>()
    val startDay: LiveData<LocalDate?>
        get() = _startDay
    private val _endDay = MutableLiveData<LocalDate?>()
    val endDay: LiveData<LocalDate?>
        get() = _endDay
    private val _holidays = MutableLiveData<Int>()
    private val holidays: LiveData<Int>
        get() = _holidays

    private val _answer = MutableLiveData<String?>()
    val answer: LiveData<String?>
        get() = _answer

    fun setStartDay(startDay: LocalDate?) {
        _startDay.value = startDay
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setStartDay(startDay: String) {
        val splitByDot = startDay.split(".")
        val localD =
            LocalDate.of(splitByDot[2].toInt(), splitByDot[1].toInt(), splitByDot[0].toInt())
        if (this.startDay.value != localD) _startDay.value = localD
    }

    fun setEndDay(endDay: LocalDate?) {
        _endDay.value = endDay
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setEndDay(endDay: String) {
        val splitByDot = endDay.split(".")
        val localD =
            LocalDate.of(splitByDot[2].toInt(), splitByDot[1].toInt(), splitByDot[0].toInt())
        if (this.endDay.value != localD) _endDay.value = localD
    }

    fun setHolidays(holidays: Int?) {
        _holidays.value = holidays
    }

    fun setAnswer(value:String){
        _answer.value = value
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun calculateCompensation() {
        if (startDay.value == null || endDay.value == null) {
            _answer.value = "Укажите даты приёма на работу и увольнения."
        } else {
            val amountOfMonth = (holidays.value ?: 0) / 3
            val endWithCorrect = endDay.value?.minusMonths(amountOfMonth.toLong())
            val period = Period.between(startDay.value, endWithCorrect)
            if (period.isNegative) {
                _answer.value = "Некорректно указаны даты приёма на работу и увольнения."
            } else {
                _answer.value = "${period.years} ${period.months} ${period.days}"
            }
        }
    }
}
