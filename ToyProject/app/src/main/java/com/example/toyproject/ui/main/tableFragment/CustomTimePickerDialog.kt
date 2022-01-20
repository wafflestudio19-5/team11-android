package com.example.toyproject.ui.main.tableFragment

import android.annotation.SuppressLint
import android.widget.NumberPicker
import android.widget.TimePicker
import android.content.DialogInterface
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.toyproject.R
import java.lang.Exception
import java.lang.reflect.Field


class CustomTimePickerDialog(
    context: Context?, private val mTimeSetListener: OnTimeSetListener?,
    hourOfDay: Int, minute: Int, is24HourView: Boolean
) :
    TimePickerDialog(
        context,  R.style.time_picker_dialog_style_light, mTimeSetListener, hourOfDay,
        minute / TIME_PICKER_INTERVAL, is24HourView
    ) {
    private var mTimePicker: TimePicker? = null

    override fun updateTime(hourOfDay: Int, minuteOfHour: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mTimePicker!!.hour = hourOfDay
            mTimePicker!!.minute =
                minuteOfHour / TIME_PICKER_INTERVAL
        }
        else {
            mTimePicker!!.currentHour = hourOfDay
            mTimePicker!!.currentMinute =
                minuteOfHour / TIME_PICKER_INTERVAL
        }
    }

    override fun onClick(dialog: DialogInterface, which: Int) {
        when (which) {
            BUTTON_POSITIVE -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mTimeSetListener?.onTimeSet(
                    mTimePicker, mTimePicker!!.hour,
                    mTimePicker!!.minute * TIME_PICKER_INTERVAL
                )
            }
            else {
                mTimeSetListener?.onTimeSet(
                mTimePicker, mTimePicker!!.currentHour,
                mTimePicker!!.currentMinute * TIME_PICKER_INTERVAL
                )
            }


            BUTTON_NEGATIVE -> cancel()
        }
    }

    @SuppressLint("PrivateApi")
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        try {
            val timePicker: TimePicker = findViewById(
                Resources.getSystem().getIdentifier(
                    "timePicker",
                    "id",
                    "android"
                )
            )

            val minutePicker: NumberPicker = timePicker.findViewById(
                Resources.getSystem().getIdentifier(
                    "minute",
                    "id",
                    "android"
                )
            )

            val hourPicker: NumberPicker = timePicker.findViewById(
                Resources.getSystem().getIdentifier(
                    "hour",
                    "id",
                    "android"
                )
            )

            // val classForid = Class.forName("com.android.internal.R\$id")
            // val timePickerField: Field = classForid.getField("timePicker")
            mTimePicker =timePicker//  findViewById(timePickerField.getInt(null)) as TimePicker
            // val field: Field = classForid.getField("minute")
            //val minuteSpinner = mTimePicker!!
            //     .findViewById(field.getInt(null)) as NumberPicker
            // minuteSpinner.minValue = 0
            // minuteSpinner.maxValue = 60 /TIME_PICKER_INTERVAL - 1
            minutePicker.minValue = 0
            minutePicker.maxValue = 60 /TIME_PICKER_INTERVAL - 1
            val displayedValues: MutableList<String> = ArrayList()
            var i = 0
            while (i < 60) {
                displayedValues.add(String.format("%02d", i))
                i += TIME_PICKER_INTERVAL
            }
            // minuteSpinner.displayedValues = displayedValues
            minutePicker.displayedValues = displayedValues
                .toTypedArray()
        } catch (e: Exception) {
            Toast.makeText(context, "아오", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val TIME_PICKER_INTERVAL = 15
    }
}