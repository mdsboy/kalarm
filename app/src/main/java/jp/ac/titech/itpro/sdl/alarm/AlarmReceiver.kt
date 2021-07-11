package jp.ac.titech.itpro.sdl.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.os.VibrationEffect
import android.os.VibrationEffect.DEFAULT_AMPLITUDE
import android.os.Vibrator
import android.util.Log
import androidx.annotation.RequiresApi
import kotlin.math.abs


class AlarmReceiver : BroadcastReceiver() {

    private val samplingRate = 44100
    private val frameRate = 10
    private val section = samplingRate / frameRate * 10
    private val bufSize = AudioRecord.getMinBufferSize(
        samplingRate,
        AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_16BIT
    )

    val audioDataArray = ShortArray(section)

    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null

    private var audioRecord: AudioRecord = AudioRecord(
        MediaRecorder.AudioSource.MIC,
        samplingRate,
        AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_16BIT,
        bufSize
    )


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context?, intent: Intent?) {

        Log.d("debug", "received")

        if (mediaPlayer == null && vibrator == null) {
            mediaPlayer = MediaPlayer.create(context, R.raw.sound_file_1)
            vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        } else {
            return
        }

        mediaPlayer?.start()

        val vibrationEffect = VibrationEffect.createWaveform(
            longArrayOf(300, 300),
            intArrayOf(0, DEFAULT_AMPLITUDE),
            0
        )
        vibrator?.vibrate(vibrationEffect)

        audioRecord.positionNotificationPeriod = section

        audioRecord.notificationMarkerPosition = 40000

        audioRecord.setRecordPositionUpdateListener(object :
            AudioRecord.OnRecordPositionUpdateListener {
            override fun onPeriodicNotification(recorder: AudioRecord) {
                recorder.read(audioDataArray, 0, section)
                Log.v("AudioRecord", "onPeriodicNotification size=${audioDataArray.size}")

                val sumLevel = audioDataArray.map { abs(it.toDouble()) }.sum()
                val amplitude = sumLevel / section

                Log.v("AudioRecord", "amplitude=${amplitude}")
                if (amplitude > 1000) {
                    mediaPlayer?.stop()
                    vibrator?.cancel()
                    audioRecord.stop()
                }
            }

            override fun onMarkerReached(recorder: AudioRecord) {
                recorder.read(audioDataArray, 0, section)
                Log.v("AudioRecord", "onMarkerReached size=${audioDataArray.size}")
            }
        })

        audioRecord.startRecording()

        audioRecord.read(audioDataArray, 0, section)
    }
}
