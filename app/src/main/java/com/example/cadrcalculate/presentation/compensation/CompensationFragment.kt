package com.example.cadrcalculate.presentation.compensation

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
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

    @SuppressLint("ResourceType", "ServiceCast")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setEditTextChangedListeners()
        setEditTextOnClickListeners()
        setObservers()


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
                binding.etHolidays.visibility = View.VISIBLE
            } else {
                binding.etHolidays.visibility = View.GONE
            }
        }

        binding.etHolidays.addTextChangedListener {
            it?.let {
                if (it.isNotEmpty() && !it.contains("[^0-9]")) {
                    viewModel.setHolidays(it.toString().toInt())
                } else {
                    viewModel.setHolidays(0)
                }
            }
        }

        binding.calculateButton.setOnClickListener {
            viewModel.calculateCompensation()
            it.hideKeyboard()

        }
        binding.resetButton.setOnClickListener {
            resetUiState()
        }
    }

    private fun resetUiState() {
        with(binding) {
            tvBeginWork.clearFocus()
            viewModel.setStartDay(null)
            tvBeginWork.setText("")
            tvEndWork.clearFocus()
            viewModel.setEndDay(null)
            tvEndWork.setText("")
            checkBox.isChecked = false
            tvResult.text = ""
            etHolidays.clearFocus()
            viewModel.setHolidays(null)
            etHolidays.setText("")
            viewModel.setAnswer("")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setEditTextOnClickListeners() {
        binding.tvBeginWork.setOnFocusChangeListener { view, b ->
            if (!b && !(view as EditText).text.matches("\\d\\d.\\d\\d.\\d\\d\\d\\d".toRegex())) {
                binding.tilBeginWork.error = "формат даты должен быть ДД.ММ.ГГГГ"
            }
        }
        binding.tvBeginWork.setOnClickListener {
            val text = binding.tvBeginWork.text.toString()
            val lengthOfNumber = text.indexOfLast {
                it.isDigit()
            }
            binding.tvBeginWork.setSelection(lengthOfNumber + 1)
        }
    }

    private fun setEditTextChangedListeners() {
        binding.tvBeginWork.addTextChangedListener(object : TextWatcher {
            private var current = ""
            private val ddmmyyyy = "DDMMYYYY"
            private val cal: Calendar = Calendar.getInstance()
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //TODO("Not yet implemented")
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString() == current) {
                    return
                }
                binding.tilBeginWork.isErrorEnabled = false
                val parse = textMask(p0.toString(), current, ddmmyyyy, cal)
                current = parse.first
                binding.tvBeginWork.setText(current)
                if (current.matches("\\d\\d.\\d\\d.\\d\\d\\d\\d".toRegex())
                ) {
                    viewModel.setStartDay(current)
                }
                val pos = if (parse.second < current.length) parse.second else current.length
                binding.tvBeginWork.setSelection(pos)
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })
        binding.tvEndWork.setOnFocusChangeListener { view, b ->
            if (!b && !(view as EditText).text.matches("\\d\\d.\\d\\d.\\d\\d\\d\\d".toRegex())) {
                binding.tilEndWork.error = "формат даты должен быть ДД.ММ.ГГГГ"
            }
        }
        binding.tvEndWork.addTextChangedListener(object : TextWatcher {
            private var current = ""
            private val ddmmyyyy = "DDMMYYYY"
            private val cal: Calendar = Calendar.getInstance()
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //TODO("Not yet implemented")
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString() == current) {
                    return
                }
                binding.tilEndWork.isErrorEnabled = false
                val parse = textMask(p0.toString(), current, ddmmyyyy, cal)
                current = parse.first
                binding.tvEndWork.setText(current)
                if (current.matches("\\d\\d.\\d\\d.\\d\\d\\d\\d".toRegex())
                ) {
                    viewModel.setEndDay(current)
                }
                val pos = if (parse.second < current.length) parse.second else current.length
                binding.tvEndWork.setSelection(pos)
            }

            override fun afterTextChanged(p0: Editable?) {
                //TODO
            }

        })


    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setObservers() {
        viewModel.startDay.observe(viewLifecycleOwner) {
            it?.let {
                with(binding) {
                    val dateFormat =
                        DateFormat.format("\"dd.MM.yyyy\"", getCalendarFromLocalDate(it))
                    tvBeginWork.text?.clear()
                    tvBeginWork.setText(dateFormat)
                }
            }
        }
        viewModel.endDay.observe(viewLifecycleOwner) {
            it?.let {
                with(binding) {
                    val dateFormat =
                        DateFormat.format("\"dd.MM.yyyy\"", getCalendarFromLocalDate(it))
                    tvEndWork.text?.clear()
                    tvEndWork.setText(dateFormat)
                }
            }

        }
        viewModel.answer.observe(viewLifecycleOwner) {
            binding.tvResult.text = it ?: ""
        }
    }

    fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }


}

@RequiresApi(Build.VERSION_CODES.O)
private fun getCalendarFromLocalDate(localDate: LocalDate): Calendar {
    val calendar = Calendar.getInstance()
    calendar.set(localDate.year, localDate.monthValue - 1, localDate.dayOfMonth)
    return calendar
}

private fun textMask(
    value: String,
    current: String,
    mask: String,
    cal: Calendar
): Pair<String, Int> {
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



