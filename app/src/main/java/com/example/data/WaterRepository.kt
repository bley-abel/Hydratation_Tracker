package com.example.data

import kotlinx.coroutines.flow.Flow

class WaterRepository(private val waterLogDao: WaterLogDao) {
    val allLogs: Flow<List<WaterLog>> = waterLogDao.getAllLogs()

    suspend fun insertLog(log: WaterLog) {
        waterLogDao.insertLog(log)
    }

    suspend fun deleteLog(log: WaterLog) {
        waterLogDao.deleteLog(log)
    }

    suspend fun clearAllLogs() {
        waterLogDao.deleteAllLogs()
    }
}
