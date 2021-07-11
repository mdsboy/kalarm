package jp.ac.titech.itpro.sdl.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_CANCEL_CURRENT
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
    private lateinit var darkView: View

    private lateinit var alarmMgr: AlarmManager
    private var alarmIntent: PendingIntent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        timePicker = findViewById(R.id.timePicker)

        button = findViewById(R.id.button)

        darkView = findViewById(R.id.view)
        darkView.visibility = View.GONE
        darkView.setOnTouchListener { _, _ -> true }

        alarmMgr = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun clickButton(v: View) {
        if (button.text == "set alarm") {
            setAlarm()
            button.text = "cancel alarm"
            darkView.visibility = View.VISIBLE
        }
        /*
        else {
            alarmMgr.cancel(alarmIntent)
            button.text = "set alarm"
            darkView.visibility = View.GONE
        }
        */
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setAlarm() {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, timePicker.hour)
            set(Calendar.MINUTE, timePicker.minute)
        }

        Log.v("debug", String.format("set alarm %d:%d", timePicker.hour, timePicker.minute))

        val intent = Intent(this, AlarmReceiver::class.java)
        intent.putExtra("HOUR", timePicker.hour)
        intent.putExtra("MINUTE", timePicker.minute)

        alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0)

        alarmMgr.setExact(
            /*
             * for debug
             */

            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            0,
            /*
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            */
            alarmIntent
        )
    }

    override fun onResume() {
        super.onResume()

        Log.v("po", "onResume")

        button.text = "set alarm"
        darkView.visibility = View.GONE
    }
}
