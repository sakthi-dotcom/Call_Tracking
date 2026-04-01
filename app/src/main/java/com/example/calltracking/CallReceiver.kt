package com.example.calltracking

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.util.Log
import com.example.calltracking.dataclass.CallLogEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CallReceiver : BroadcastReceiver() {

    companion object {
        private var lastState = TelephonyManager.CALL_STATE_IDLE
        private var saved = false
        private var recorder: CallRecorder? = null
    }

    override fun onReceive(context: Context, intent: Intent) {

        val stateStr = intent.getStringExtra(TelephonyManager.EXTRA_STATE)

        val state = when (stateStr) {
            TelephonyManager.EXTRA_STATE_IDLE -> TelephonyManager.CALL_STATE_IDLE
            TelephonyManager.EXTRA_STATE_OFFHOOK -> TelephonyManager.CALL_STATE_OFFHOOK
            TelephonyManager.EXTRA_STATE_RINGING -> TelephonyManager.CALL_STATE_RINGING
            else -> return
        }

        // Prevent duplicate triggers
        if (state == lastState) return

        when (state) {

            TelephonyManager.CALL_STATE_OFFHOOK -> {
                if (recorder == null) {
                    saved = false
                    recorder = CallRecorder()
                    recorder?.startRecording(context)
                    Log.d("CALL_RECORD", "Recording started")
                }
            }

            TelephonyManager.CALL_STATE_IDLE -> {

                val filePath = recorder?.getFilePath()
                Log.d("CALL_RECORD", "Before stop path: $filePath")

                if (recorder != null) {
                    recorder?.stopRecording()
                    recorder = null
                }

                Log.d("CALL_RECORD", "Recording stopped")
                Log.d("CALL_RECORD", "File path: $filePath")

                if (!saved) {

                    val repository = CallRepository(
                        AppDatabase.getDatabase(context).callLogDao()
                    )

                    CoroutineScope(Dispatchers.IO).launch {

                        delay(700)

                        val (number, callType, durationSec) = getLastCallDetails(context)

                        val type = when (callType) {
                            android.provider.CallLog.Calls.INCOMING_TYPE -> "INCOMING"
                            android.provider.CallLog.Calls.OUTGOING_TYPE -> "OUTGOING"
                            android.provider.CallLog.Calls.MISSED_TYPE -> "MISSED"
                            else -> "UNKNOWN"
                        }

                        val duration = durationSec * 1000
                        val time = System.currentTimeMillis()

                        Log.d("CALL_FINAL", "Number: $number")
                        Log.d("CALL_FINAL", "Type: $type")
                        Log.d("CALL_FINAL", "Duration(ms): $duration")
                        Log.d("CALL_FINAL", "Recording: $filePath")

                        repository.insert(
                            CallLogEntity(
                                number = number,
                                type = type,
                                startTime = time - duration,
                                endTime = time,
                                duration = duration
                            )
                        )
                    }

                    saved = true
                }
            }
        }

        lastState = state
    }

    private fun getLastCallDetails(context: Context): Triple<String, Int, Long> {

        val cursor = context.contentResolver.query(
            android.provider.CallLog.Calls.CONTENT_URI,
            null,
            null,
            null,
            android.provider.CallLog.Calls.DATE + " DESC"
        )

        cursor?.use {
            if (it.moveToFirst()) {

                val number = it.getString(
                    it.getColumnIndexOrThrow(android.provider.CallLog.Calls.NUMBER)
                )

                val type = it.getInt(
                    it.getColumnIndexOrThrow(android.provider.CallLog.Calls.TYPE)
                )

                val duration = it.getLong(
                    it.getColumnIndexOrThrow(android.provider.CallLog.Calls.DURATION)
                )

                Log.d("CALL_LOG_DEBUG", "Number: $number")
                Log.d("CALL_LOG_DEBUG", "Type: $type")
                Log.d("CALL_LOG_DEBUG", "Duration(sec): $duration")

                return Triple(number, type, duration)
            }
        }

        Log.d("CALL_LOG_DEBUG", "No call log found")

        return Triple("Unknown", -1, 0)
    }
}