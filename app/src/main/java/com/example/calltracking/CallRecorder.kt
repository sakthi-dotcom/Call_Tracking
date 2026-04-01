package com.example.calltracking

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.os.Environment
import android.util.Log
import java.io.File

class CallRecorder {

    private var recorder: MediaRecorder? = null
    private var filePath: String? = null

    fun startRecording(context: Context) {

        try {
            val file = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                "call_${System.currentTimeMillis()}.mp4"
            )

            filePath = file.absolutePath
            Log.d("REC_DEBUG", "File path: $filePath")

            recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                MediaRecorder()
            }

            recorder?.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)

                setAudioEncodingBitRate(256000)
                setAudioSamplingRate(44100)

                setOutputFile(filePath)

                prepare()
                start()
            }

            Log.d("REC_DEBUG", "Recording started")

        } catch (e: Exception) {
            Log.e("REC_DEBUG", "Error: ${e.message}")
            filePath = null
        }
    }

    fun stopRecording() {
        try {
            recorder?.stop()
        } catch (e: Exception) {
            Log.e("REC_DEBUG", "Stop failed: ${e.message}")
        }
        recorder?.release()
        recorder = null
    }

    fun getFilePath(): String? {
        return filePath
    }
}