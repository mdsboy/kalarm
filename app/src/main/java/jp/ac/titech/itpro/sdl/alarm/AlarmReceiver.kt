package jp.ac.titech.itpro.sdl.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.util.Log
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
    private var audioRecord: AudioRecord = AudioRecord(
        MediaRecorder.AudioSource.MIC,
        samplingRate,
        AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_16BIT,
        bufSize
    )

    override fun onReceive(context: Context?, intent: Intent?) {

        Log.d("debug", "received")

        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, R.raw.sound_file_1)
        }

        mediaPlayer!!.start()

        audioRecord.positionNotificationPeriod = section

        audioRecord.notificationMarkerPosition = 40000

        audioRecord.setRecordPositionUpdateListener(object :
            AudioRecord.OnRecordPositionUpdateListener {
            override fun onPeriodicNotification(recorder: AudioRecord) {
                recorder.read(audioDataArray, 0, section) // 音声データ読込
                Log.v("AudioRecord", "onPeriodicNotification size=${audioDataArray.size}")

                val sumLevel = audioDataArray.map { abs(it.toDouble()) }.sum()
                val amplitude = sumLevel / section

                Log.v("AudioRecord", "amplitude=${amplitude}")
                if (amplitude > 1000)
                    mediaPlayer!!.stop()
            }

            override fun onMarkerReached(recorder: AudioRecord) {
                recorder.read(audioDataArray, 0, section) // 音声データ読込
                Log.v("AudioRecord", "onMarkerReached size=${audioDataArray.size}")
                // 好きに処理する
            }
        })

        audioRecord.startRecording()

        audioRecord.read(audioDataArray, 0, section)
    }
}
