package com.example.calltracking

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.calltracking.dataclass.CallLogEntity

@Dao
interface CallLogDao {

    @Insert
    suspend fun insert(callLog: CallLogEntity)

    @Query("SELECT * FROM call_logs ORDER BY id DESC")
    fun getAllLogs(): LiveData<List<CallLogEntity>>
}