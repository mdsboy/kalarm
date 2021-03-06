package jp.ac.titech.itpro.sdl.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_CANCEL_CURRENT
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.provider.AlarmClock
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TimePicker
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class MainActivity : AppCompatActivity() {
    private val TAG = javaClass.simpleName

    private lateinit var timePicker: TimePicker
    private lateinit var button: Button
    private lateinit var darkView: View

    private lateinit var alarmMgr: AlarmManager
    private var alarmIntent: PendingIntent? = null

    private object Keys {
        const val HOUR = "HOUR"
        const val MINUTE = "MINUTE"
        const val IS_ALARM_SET = "IS_ALARM_SET"
    }

    private val SET_ALARM = "set alarm"
    private val CANCEL_ALARM = "cancel alarm"

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        timePicker = findViewById(R.id.timePicker)

        button = findViewById(R.id.button)

        darkView = findViewById(R.id.view)
        darkView.visibility = View.GONE
        darkView.setOnTouchListener { _, _ -> true }

        alarmMgr = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        savedInstanceState?.let { savedInstanceState ->
            timePicker.hour = savedInstanceState.getInt(Keys.HOUR, 0)
            timePicker.minute = savedInstanceState.getInt(Keys.MINUTE, 0)

            if (savedInstanceState.getBoolean(Keys.IS_ALARM_SET)) {
                button.text = CANCEL_ALARM
                darkView.visibility = View.VISIBLE
            } else {
                button.text = SET_ALARM
                darkView.visibility = View.GONE
            }

            setAlarm()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun clickButton(v: View) {
        when (button.text) {
            SET_ALARM -> {
                setAlarm()
                button.text = CANCEL_ALARM
                darkView.visibility = View.VISIBLE
            }
            CANCEL_ALARM -> {
                if (alarmIntent != null) {
                    alarmMgr.cancel(alarmIntent)
                } else {
                    Log.d(TAG, "alarmIntent is null")
                }
                button.text = SET_ALARM
                darkView.visibility = View.GONE
            }
            else -> {
                Log.d(TAG, "unexpected text")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setAlarm() {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, timePicker.hour)
            set(Calendar.MINUTE, timePicker.minute)
        }

        Log.v(TAG, String.format("set alarm %d:%d", timePicker.hour, timePicker.minute))

        val intent = Intent(this, AlarmReceiver::class.java)
        intent.putExtra("HOUR", timePicker.hour)
        intent.putExtra("MINUTE", timePicker.minute)

        alarmIntent = PendingIntent.getBroadcast(this, 0, intent, FLAG_CANCEL_CURRENT)

        val alarmClock = AlarmManager.AlarmClockInfo(calendar.timeInMillis, alarmIntent)

        alarmMgr.setAlarmClock(alarmClock, alarmIntent)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onResume() {
        super.onResume()
        Log.v(TAG, "onResume")

        if (button.text == CANCEL_ALARM) {
            val calendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
            }
            val nowHour = calendar.get(Calendar.HOUR_OF_DAY)
            val nowMinute = calendar.get(Calendar.MINUTE)

            if (timePicker.minute <= nowMinute && timePicker.hour <= nowHour) {
                Log.d(TAG, String.format("now:%2d%2d", nowHour, nowMinute))
                Log.d(TAG, String.format("timePicker:%2d%2d", timePicker.hour, timePicker.minute))
                Log.d(TAG, "alarm is not set")
                button.text = SET_ALARM
                darkView.visibility = View.GONE
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.v(TAG, "onSaveInstanceState")

        outState.putInt(Keys.HOUR, timePicker.hour)
        outState.putInt(Keys.MINUTE, timePicker.minute)

        outState.putBoolean(Keys.IS_ALARM_SET, button.text == CANCEL_ALARM)

        if (button.text == CANCEL_ALARM) {
            if (alarmIntent != null) {
                alarmMgr.cancel(alarmIntent)
            } else {
                Log.d(TAG, "alarmIntent is null")
            }
        }
    }
}
