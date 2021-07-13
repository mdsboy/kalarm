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
                alarmMgr.cancel(alarmIntent)
                button.text = SET_ALARM
                darkView.visibility = View.GONE
            }
            else -> {
                Log.d("debug", "unexpected text")
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

        Log.v("debug", String.format("set alarm %d:%d", timePicker.hour, timePicker.minute))

        val intent = Intent(this, AlarmReceiver::class.java)
        intent.putExtra("HOUR", timePicker.hour)
        intent.putExtra("MINUTE", timePicker.minute)

        alarmIntent = PendingIntent.getBroadcast(this, 0, intent, FLAG_CANCEL_CURRENT)

        alarmMgr.setExactAndAllowWhileIdle(
                /*
                 * for debug
                 */
/*
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            0,
            */
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,

                alarmIntent
        )
    }

    override fun onResume() {
        super.onResume()
        Log.v("debug", "onResume")

        /*
        button.text = "set alarm"
        darkView.visibility = View.GONE
        */
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.v("debug", "onSaveInstanceState")

        outState.putInt(Keys.HOUR, timePicker.hour)
        outState.putInt(Keys.MINUTE, timePicker.minute)

        outState.putBoolean(Keys.IS_ALARM_SET, button.text == CANCEL_ALARM)

        if (button.text == CANCEL_ALARM) {
            alarmMgr.cancel(alarmIntent)
        }
    }
}
