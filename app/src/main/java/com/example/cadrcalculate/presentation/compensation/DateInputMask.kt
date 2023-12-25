package com.example.cadrcalculate.presentation.compensation

import android.icu.util.Calendar
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText


class DateInputMask(val input : EditText) {

    fun listen():String {
        input.addTextChangedListener(mDateEntryWatcher)
        return current
    }

    private var current = ""
    private val ddmmyyyy = "DDMMYYYY"
    private val cal: Calendar = Calendar.getInstance()

    private val mDateEntryWatcher = object : TextWatcher {

        var edited = false
        val dividerCharacter = "."

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if (s.toString() == current) {
                return
            }

            var clean = s.toString().replace("[^\\d.]|\\.".toRegex(), "")
            val cleanC = current.replace ("[^\\d.]|\\.".toRegex(), "")

            val cl = clean.length;
            var sel = cl
            var i = 2
            while (i <= cl && i < 6) {
                sel++
                i += 2
            }
            //Fix for pressing delete next to a forward slash
            if (clean == cleanC) sel--

            if (clean.length < 8) {
                clean += ddmmyyyy.substring(clean.length)
            } else {
                //This part makes sure that when we finish entering numbers
                //the date is correct, fixing it otherwise
                var day =  clean.substring(0, 2).toInt()
                var mon =  clean.substring(2, 4).toInt()
                var year =  clean.substring(4, 8).toInt()

                if (mon<1) mon = 1
                if (mon>12) mon = 12
                cal[Calendar.MONTH] = mon - 1

                if (year<1900) year = 1900
                if (year>2100) year = 2100
                cal[Calendar.YEAR] = year
                // ^ first set year for the line below to work correctly
                //with leap years - otherwise, date e.g. 29/02/2012
                //would be automatically corrected to 28/02/2012

                day = if ( day > cal.getActualMaximum(Calendar.DATE)) cal.getActualMaximum(Calendar.DATE) else day
                clean = String.format("%02d%02d%02d", day, mon, year)
            }

            clean = String.format(
                "%s.%s.%s", clean.substring(0, 2),
                clean.substring(2, 4),
                clean.substring(4, 8)
            )

            sel = if (sel < 0) 0 else sel
            current = clean
            input.setText(current)
            val pos = if (sel < current.length) sel else current.length
            input.setSelection(pos)
//            Log.d("TAGIL", )
        }
//        {
//            if (edited) {
//                edited = false
//                return
//            }
//
//            var working = getEditText()
//
//            working = manageDateDivider(working, 2, start, before)
//            working = manageDateDivider(working, 5, start, before)
//
//            edited = true
//            input.setText(working)
//            input.setSelection(input.text.length)
//        }

        private fun manageDateDivider(working: String, position : Int, start: Int, before: Int) : String{
            Log.d("TAGIL", "working: $working, position: $position , start: $start, before: $before")
            if (working.length == position) {
                return if (before <= position && start < position)
                    working + dividerCharacter
                else
                    working.dropLast(1)
            }
            return working
        }

        private fun getEditText() : String {
            return if (input.text.length >= 10)
                input.text.toString().substring(0,10)
            else
                input.text.toString()
        }

        override fun afterTextChanged(s: Editable) {
//            s?.let {
//                if (s.isNotEmpty() && s.toString().matches("\\d\\d.\\d\\d.\\d\\d\\d\\d".toRegex())) {
//                    Log.d("TAGIL","SUCCESS")
//                } else {
//                    Log.d("TAGIL","FAILURE")
//                }
//            }
        }
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
    }
}