package com.c242_ps009.habitsaga.ui.utils

import android.app.DatePickerDialog
import android.content.Context
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*

object DatePickerUtil {
    // private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    fun showDatePickerDialog(context: Context, editText: TextView, onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            context,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = Calendar.getInstance().apply {
                    set(Calendar.YEAR, selectedYear)
                    set(Calendar.MONTH, selectedMonth)
                    set(Calendar.DAY_OF_MONTH, selectedDay)
                }.time

                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val formattedDate = dateFormat.format(selectedDate)
                editText.text = formattedDate

                onDateSelected(formattedDate)
            },
            year, month, day
        )

        datePickerDialog.show()
    }
}
