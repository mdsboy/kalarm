package jp.ac.titech.itpro.sdl.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.media.MediaPlayer

class AlarmReceiver : BroadcastReceiver() {

    private var mediaPlayer: MediaPlayer? = null

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("debug", "recieved")

        mediaPlayer = MediaPlayer.create(context, R.raw.sound_file_1)
        mediaPlayer?.start()
    }
}
