package com.example.calltracking.dataclass

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "call_logs")
data class CallLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val number: String,
    val type: String,
    val startTime: Long,
    val endTime: Long,
    val duration: Long
)