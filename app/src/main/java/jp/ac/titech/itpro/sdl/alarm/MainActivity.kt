package jp.ac.titech.itpro.sdl.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TimePicker
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var timePicker: TimePicker
    private lateinit var button: Button

    private lateinit var alarmMgr: AlarmManager
    private lateinit var alarmIntent: PendingIntent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        timePicker = findViewById(R.id.timePicker)

        button = findViewById(R.id.button)

        alarmMgr = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmIntent = Intent(this, AlarmReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(this, 0, intent, 0)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun clickButton(v: View) {
        if (button.text == "set alarm") {
            setAlarm()
            button.text = "cancel alarm"
        } else {
            alarmMgr.cancel(alarmIntent)
            button.text = "set alarm"
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setAlarm() {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, timePicker.hour)
            set(Calendar.MINUTE, timePicker.minute)
        }

        Log.v("debug", String.format("set alarm %d:%d", timePicker.hour, timePicker.minute))

        alarmMgr.set(
            /*
             * for debug
             */
            /*
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            1 * 1000,
            */
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            alarmIntent
        )
    }
}
