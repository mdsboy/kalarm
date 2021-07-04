package jp.ac.titech.itpro.sdl.alarm

import android.app.AlarmManager
import android.app.Dialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import java.util.*


class TimePickerFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    private var alarmMgr: AlarmManager? = null
    private lateinit var alarmIntent: PendingIntent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        alarmMgr = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmIntent = Intent(context, AlarmReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(context, 0, intent, 0)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current time as the default values for the picker
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        // Create a new instance of TimePickerDialog and return it
        return TimePickerDialog(activity, this, hour, minute, DateFormat.is24HourFormat(activity))
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        // Do something with the time chosen by the user

        Log.d("debug", String.format("%d:%d", hourOfDay, minute))

        // Set the alarm to start at 8:30 a.m.
        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, minute)
        }

        alarmMgr?.set(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                5*1000,/*
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,*/
                alarmIntent
        )

        /*
            val requestId = 100
            val intent = Intent()

            val alarmManager = this.context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val pendingIntent =
                    PendingIntent.getService(context, requestId, intent,
                            PendingIntent.FLAG_NO_CREATE)
            if (pendingIntent != null && alarmManager != null) {
                alarmManager.cancel(pendingIntent)
                Log.d("debug", "cancel")
            } else {
                Log.d("debug", String.format("%d:%d", hourOfDay, minute))
            }
    */
    }
}
