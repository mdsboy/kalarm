package jp.ac.titech.itpro.sdl.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest

class AlarmReceiver : BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {

        Log.d("debug", "received")
/*
        val alarmIntent = Intent(context, AlarmActivity::class.java)
        //alarmIntent.addFlags(FLAG_ACTIVITY_NEW_TASK)

        val hour = intent.getIntExtra("HOUR", 0)
        val minute = intent.getIntExtra("MINUTE", 0)
        Log.v("po", String.format("%02d:%02d", hour, minute))

        intent.extras?.let {
            alarmIntent.putExtras(it)
        }
*/
        val alarmWorkRequest: WorkRequest =
                OneTimeWorkRequestBuilder<AlarmWorker>()
                        .build()

        WorkManager.getInstance(context).enqueue(alarmWorkRequest)

    }
}
