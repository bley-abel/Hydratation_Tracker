package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.WaterLog
import com.example.data.WaterRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

class WaterViewModel(private val repository: WaterRepository) : ViewModel() {

    // Target daily hydration goal in ml (default is 2000ml = 2L)
    private val _dailyGoalMl = MutableStateFlow(2000)
    val dailyGoalMl: StateFlow<Int> = _dailyGoalMl.asStateFlow()

    // Retrieve all logs from the database
    val allLogs: StateFlow<List<WaterLog>> = repository.allLogs
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Filter logs that occurred today
    val todayLogs: StateFlow<List<WaterLog>> = repository.allLogs
        .map { logs ->
            logs.filter { isToday(it.timestamp) }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Calculate sum of water logged today
    val todayTotalMl: StateFlow<Int> = todayLogs
        .map { logs ->
            logs.sumOf { it.amountMl }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    // Check if hydration goal is completed
    val isGoalAchieved: StateFlow<Boolean> = todayTotalMl
        .map { total ->
            total >= _dailyGoalMl.value
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    // Add water in ml (default is 250ml)
    fun addWater(amountMl: Int = 250) {
        viewModelScope.launch {
            val log = WaterLog(amountMl = amountMl)
            repository.insertLog(log)
        }
    }

    // Delete a specific water log
    fun deleteLog(log: WaterLog) {
        viewModelScope.launch {
            repository.deleteLog(log)
        }
    }

    // Reset all logs for today
    fun resetToday() {
        viewModelScope.launch {
            // Delete all logs to reset
            repository.clearAllLogs()
        }
    }

    // Update the daily goal
    fun updateDailyGoal(goalMl: Int) {
        if (goalMl > 0) {
            _dailyGoalMl.value = goalMl
        }
    }

    private fun isToday(timestamp: Long): Boolean {
        val calendar = Calendar.getInstance()
        val todayDay = calendar.get(Calendar.DAY_OF_YEAR)
        val todayYear = calendar.get(Calendar.YEAR)

        calendar.timeInMillis = timestamp
        val targetDay = calendar.get(Calendar.DAY_OF_YEAR)
        val targetYear = calendar.get(Calendar.YEAR)

        return todayDay == targetDay && todayYear == targetYear
    }
}

class WaterViewModelFactory(private val repository: WaterRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WaterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WaterViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
