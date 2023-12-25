package com.example.cadrcalculate.presentation.compensation

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.cadrcalculate.databinding.FragmentCompensationBinding
import java.time.LocalDate
import java.util.Calendar

class CompensationFragment : Fragment() {

    private var _binding: FragmentCompensationBinding? = null
    private val binding: FragmentCompensationBinding
        get() = _binding ?: throw RuntimeException("FragmentCompensationBinding is null")

    private val viewModel by lazy {
        ViewModelProvider(this)[CompensationViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentCompensationBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ResourceType")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setTextListeners(binding)
        viewModel.startDay.observe(viewLifecycleOwner) {
            with(binding) {
                val dateFormat = DateFormat.format("\"dd MMMM yyyy\"", getCalendarFromLocalDate(it))
                tvBeginWork.text?.clear()
                tvBeginWork.setText(dateFormat)
//                tvBeginWork.hint = dateFormat// append(dateFormat)
                // = dateFormat
            }
        }
        viewModel.endDay.observe(viewLifecycleOwner) {
            with(binding) {
                val dateFormat = DateFormat.format("\"dd MMMM yyyy\"", getCalendarFromLocalDate(it))
                tvEndWork.text?.clear()
                tvEndWork.hint = dateFormat//.append(dateFormat.toString())
            }
        }
        viewModel.answer.observe(viewLifecycleOwner) {
            binding.tvResult.text = it
        }

        val startDate = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            viewModel.setStartDay(LocalDate.of(year, month + 1, dayOfMonth))
        }
        binding.ivBeginWork.setOnClickListener {
            val dpd = DatePickerDialog(
                requireContext(),
                3,
                startDate,
                2020,
                0,
                1
            )
            dpd.setTitle("Дата приёма на работу")
            dpd.show()
        }

        val endDate = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            viewModel.setEndDay(LocalDate.of(year, month + 1, dayOfMonth))
        }

        binding.ivEndWork.setOnClickListener {
            val calendar = Calendar.getInstance()
            val dpd = DatePickerDialog(
                requireContext(),
                AlertDialog.THEME_HOLO_LIGHT,
                endDate,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            dpd.setTitle("Дата увольнения")
            dpd.show()
        }

        binding.checkBox.setOnCheckedChangeListener { buttonView, _ ->
            if (buttonView.isChecked) {
                binding.editTextText.visibility = View.VISIBLE
            } else {
                binding.editTextText.visibility = View.GONE
            }
        }

        binding.editTextText.addTextChangedListener {
            it?.let {
                if (it.isNotEmpty() && !it.contains("[^0-9]")) {
                    viewModel.setHolidays(it.toString().toInt())
                } else {
                    viewModel.setHolidays(0)
                }
            }
        }

        binding.button2.setOnClickListener {
            viewModel.calculateCompensation()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}

//@RequiresApi(Build.VERSION_CODES.O)
@RequiresApi(Build.VERSION_CODES.O)
private fun getCalendarFromLocalDate(localDate: LocalDate): Calendar {
    val calendar = Calendar.getInstance()
    calendar.set(localDate.year, localDate.monthValue - 1, localDate.dayOfMonth)
    return calendar
}

private fun textMask(value: String, current:String, mask:String, cal:Calendar): Pair<String, Int> {
    var clean = value.toString().replace("[^\\d.]|\\.".toRegex(), "")
    val cleanC = current.replace("[^\\d.]|\\.".toRegex(), "")

    val cl = clean.length
    var sel = cl
    var i = 2
    while (i <= cl && i < 6) {
        sel++
        i += 2
    }
    //Fix for pressing delete next to a forward slash
    if (clean == cleanC) sel--

    if (clean.length < 8) {
        clean += mask.substring(clean.length)
    } else {
        //This part makes sure that when we finish entering numbers
        //the date is correct, fixing it otherwise
        var day = clean.substring(0, 2).toInt()
        var mon = clean.substring(2, 4).toInt()
        var year = clean.substring(4, 8).toInt()

        if (mon < 1) mon = 1
        if (mon > 12) mon = 12
        cal[android.icu.util.Calendar.MONTH] = mon - 1

        if (year < 1900) year = 1900
        if (year > 2100) year = 2100
        cal[android.icu.util.Calendar.YEAR] = year
        // ^ first set year for the line below to work correctly
        //with leap years - otherwise, date e.g. 29/02/2012
        //would be automatically corrected to 28/02/2012

        day =
            if (day > cal.getActualMaximum(android.icu.util.Calendar.DATE)) cal.getActualMaximum(
                android.icu.util.Calendar.DATE
            ) else day
        clean = String.format("%02d%02d%02d", day, mon, year)
    }

    clean = String.format(
        "%s.%s.%s", clean.substring(0, 2),
        clean.substring(2, 4),
        clean.substring(4, 8)
    )

    sel = if (sel < 0) 0 else sel
    return Pair(clean, sel)

}

private fun setTextListeners(binding: FragmentCompensationBinding){
    binding.tvBeginWork.addTextChangedListener(object : TextWatcher {
        private var current = ""
        private val ddmmyyyy = "DDMMYYYY"
        private val cal: Calendar = Calendar.getInstance()
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            //TODO("Not yet implemented")
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            if (p0.toString() == current) {
                return
            }

            val fuck = textMask(p0.toString(), current, ddmmyyyy, cal)
            current = fuck.first
            binding.tvBeginWork.setText(current)
            if (current.matches("\\d\\d.\\d\\d.\\d\\d\\d\\d".toRegex())
            ) {
                Log.d("TAGIL", "SUCCESS")
            } else {
                Log.d("TAGIL", "FAILURE")
            }
            val pos = if (fuck.second < current.length) fuck.second else current.length
            binding.tvBeginWork.setSelection(pos)
        }

        override fun afterTextChanged(p0: Editable?) {
            //TODO
        }

    })
    binding.tvEndWork.addTextChangedListener(object : TextWatcher {
        private var current = ""
        private val ddmmyyyy = "DDMMYYYY"
        private val cal: Calendar = Calendar.getInstance()
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            //TODO("Not yet implemented")
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            if (p0.toString() == current) {
                return
            }

            val fuck = textMask(p0.toString(), current, ddmmyyyy, cal)
            current = fuck.first
            binding.tvEndWork.setText(current)
            if (current.matches("\\d\\d.\\d\\d.\\d\\d\\d\\d".toRegex())
            ) {
                Log.d("TAGIL", "SUCCESS")
            } else {
                Log.d("TAGIL", "FAILURE")
            }
            val pos = if (fuck.second < current.length) fuck.second else current.length
            binding.tvEndWork.setSelection(pos)
        }

        override fun afterTextChanged(p0: Editable?) {
            //TODO
        }

    })


}