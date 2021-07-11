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

    private var alarmMgr: AlarmManager? = null
    private var alarmIntent: PendingIntent? = null

    private var timePicker: TimePicker? = null
    private var button: Button? = null

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
    fun setAlarm(v: View) {
        timePicker?.let { picker ->

            val calendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, picker.hour)
                set(Calendar.MINUTE, picker.minute)
            }

            Log.v("debug", String.format("set alarm %d:%d", picker.hour, picker.minute))

            alarmMgr?.set(
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

            button?.isEnabled = false
        }
    }
}
