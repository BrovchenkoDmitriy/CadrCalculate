package com.example.cadrcalculate.presentation.datePicker

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.util.Calendar
import java.util.Date

private const val ARG_DATE = "date"

class MyDatePickerFragment: DialogFragment(), OnDateSetListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val date = arguments?.getSerializable(ARG_DATE) as Date
        val calendar = Calendar.getInstance()
        calendar.time = date
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val result = DatePickerDialog(requireContext(), this, year, month, day)
        result.datePicker.spinnersShown = true
        return result
    }
    override fun onDateSet(datePicker: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        datePicker?.spinnersShown
        val endCalendar = Calendar.getInstance()
        endCalendar.set(Calendar.YEAR, year)
        endCalendar.set(Calendar.MONTH, month)
        endCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
    }
    companion object {
        fun newInstance(date: Date): MyDatePickerFragment {
            val args = Bundle().apply {
                putSerializable(ARG_DATE, date)
            }
            return MyDatePickerFragment().apply{arguments = args}
        }
    }

}