package com.example.cadrcalculate.presentation.compensation

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.startDay.observe(viewLifecycleOwner) {
            with(binding) {
                val dateFormat = DateFormat.format("\"dd MMMM yyyy\"", getCalendarFromLocalDate(it))
                tvBeginWork.text = dateFormat
            }
        }
        viewModel.endDay.observe(viewLifecycleOwner) {
            with(binding) {
                val dateFormat = DateFormat.format("\"dd MMMM yyyy\"", getCalendarFromLocalDate(it))
                tvEndWork.text = dateFormat
            }
        }
        viewModel.answer.observe(viewLifecycleOwner) {
            binding.tvResult.text = it
        }

        val startDate = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            val startCalendar = Calendar.getInstance()
            startCalendar.set(Calendar.YEAR, year)
            startCalendar.set(Calendar.MONTH, month)
            startCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            viewModel.setStartDay(LocalDate.of(year, month + 1, dayOfMonth))
        }

        binding.ivBeginWork.setOnClickListener {
            DatePickerDialog(requireContext(), startDate, 2020, 0, 1).show()
        }

        val endDate = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            val endCalendar = Calendar.getInstance()
            endCalendar.set(Calendar.YEAR, year)
            endCalendar.set(Calendar.MONTH, month)
            endCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            viewModel.setEndDay(LocalDate.of(year, month + 1, dayOfMonth))
        }

        binding.ivEndWork.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                requireContext(),
                endDate,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
                .show()
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

@RequiresApi(Build.VERSION_CODES.O)
private fun getCalendarFromLocalDate(localDate: LocalDate): Calendar {
    val calendar = Calendar.getInstance()
    calendar.set(localDate.year, localDate.monthValue - 1, localDate.dayOfMonth)
    return calendar
}