package jp.ac.titech.itpro.sdl.alarm

import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.concurrent.futures.CallbackToFutureAdapter
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import kotlin.math.abs

class AlarmWorker(appContext: Context, workerParams: WorkerParameters) :
    ListenableWorker(appContext, workerParams) {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var vibrator: Vibrator

    @RequiresApi(Build.VERSION_CODES.O)
    override fun startWork(): com.google.common.util.concurrent.ListenableFuture<Result> {
        return CallbackToFutureAdapter.getFuture { completer ->

            mediaPlayer = MediaPlayer.create(applicationContext, R.raw.sound_file_1)
            vibrator = applicationContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

            mediaPlayer.isLooping = true

            mediaPlayer.start()

            val vibrationEffect = VibrationEffect.createWaveform(
                longArrayOf(300, 300),
                intArrayOf(0, VibrationEffect.DEFAULT_AMPLITUDE),
                0
            )
            vibrator.vibrate(vibrationEffect)

            audioRecording(completer)
        }
    }

    private fun audioRecording(completer: CallbackToFutureAdapter.Completer<Result>) {

        val samplingRate = 44100
        val frameRate = 10
        val section = samplingRate / frameRate * 30
        val bufSize = AudioRecord.getMinBufferSize(
            samplingRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )

        val audioDataArray = ShortArray(section)

        val audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            samplingRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufSize
        )

        audioRecord.positionNotificationPeriod = section

        audioRecord.notificationMarkerPosition = 40000

        val callback = object :
            AudioRecord.OnRecordPositionUpdateListener {
            override fun onPeriodicNotification(recorder: AudioRecord) {
                recorder.read(audioDataArray, 0, section)
                Log.v("AudioRecord", "onPeriodicNotification size=${audioDataArray.size}")

                val sumLevel = audioDataArray.map { abs(it.toDouble()) }.sum()
                val amplitude = sumLevel / section

                Log.v("AudioRecord", "amplitude=${amplitude}")
                if (amplitude > 1000) {
                    mediaPlayer.stop()
                    vibrator.cancel()
                    audioRecord.stop()

                    //finish()
                    completer.set(Result.success())
                }
            }

            override fun onMarkerReached(recorder: AudioRecord) {
                recorder.read(audioDataArray, 0, section)
                Log.v("AudioRecord", "onMarkerReached size=${audioDataArray.size}")
            }
        }

        audioRecord.setRecordPositionUpdateListener(callback)

        audioRecord.startRecording()

        audioRecord.read(audioDataArray, 0, section)

        callback

    }
}
