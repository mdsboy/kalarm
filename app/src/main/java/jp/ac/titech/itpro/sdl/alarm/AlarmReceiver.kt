package jp.ac.titech.itpro.sdl.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi

class AlarmReceiver : BroadcastReceiver() {
    private val TAG = javaClass.simpleName

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {

        Log.d(TAG, "received")

        val alarmIntent = Intent(context, AlarmActivity::class.java)
        alarmIntent.addFlags(FLAG_ACTIVITY_NEW_TASK)

        val hour = intent.getIntExtra("HOUR", 0)
        val minute = intent.getIntExtra("MINUTE", 0)
        Log.v(TAG, String.format("%02d:%02d", hour, minute))

        intent.extras?.let {
            alarmIntent.putExtras(it)
        }

        context.startActivity(alarmIntent)
    }
}
