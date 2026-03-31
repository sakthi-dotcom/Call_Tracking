package com.example.calltracking

import com.example.calltracking.dataclass.CallLogEntity

class CallRepository(private val dao: CallLogDao) {

    val allLogs = dao.getAllLogs()

    suspend fun insert(call: CallLogEntity) {
        dao.insert(call)
    }
}