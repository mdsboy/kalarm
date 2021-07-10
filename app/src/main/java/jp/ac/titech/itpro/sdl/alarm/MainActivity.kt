package jp.ac.titech.itpro.sdl.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class MainActivity : AppCompatActivity(), TimePickerDialog.OnTimeSetListener {

    private var alarmMgr: AlarmManager? = null
    private var alarmIntent: PendingIntent? = null

    private var setTimeView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setTimeView = findViewById(R.id.set_time)

        alarmMgr = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmIntent = Intent(this, AlarmReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(this, 0, intent, 0)
        }
    }

    fun showTimePickerDialog(v: View) {
        TimePickerFragment().show(supportFragmentManager, "timePicker")
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {

        Log.d("debug", String.format("%d:%d", hourOfDay, minute))

        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, minute)
        }

        alarmMgr?.set(
            /*
             * for debug
             */
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            1 * 1000,
            /*
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            */
            alarmIntent
        )

        setTimeView?.text = String.format("%d:%d", hourOfDay, minute)
    }
}
