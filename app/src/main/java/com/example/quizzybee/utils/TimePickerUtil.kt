package com.example.quizzybee.utils

import android.app.TimePickerDialog
import android.content.Context
import android.text.format.DateFormat
import java.util.Calendar

object TimePickerUtil {
    fun showTimePickerDialog(
        context: Context,
        onTimeSelected: (hour: Int, minute: Int) -> Unit,
    ) {
        // Get the current time
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)

        // Create a TimePickerDialog
        val timePickerDialog =
            TimePickerDialog(
                context,
                { _, hourOfDay, minute ->
                    // Pass the selected time back
                    onTimeSelected(hourOfDay, minute)
                },
                currentHour,
                currentMinute,
                DateFormat.is24HourFormat(context), // Use system preference for 24-hour format
            )

        // Show the dialog
        timePickerDialog.show()
    }
}
