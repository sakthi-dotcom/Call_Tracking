package com.example.calltracking

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.calltracking.dataclass.CallLogEntity

class CallViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CallRepository
    val allLogs: LiveData<List<CallLogEntity>>

    init {
        val dao = AppDatabase.getDatabase(application).callLogDao()
        repository = CallRepository(dao)
        allLogs = repository.allLogs
    }
}