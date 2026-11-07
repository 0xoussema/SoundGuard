package com.oussema.soundguard.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oussema.soundguard.data.NoiseReading
import com.oussema.soundguard.data.NoiseReadingRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SoundGuardViewModel(
    private val repository: NoiseReadingRepository
) : ViewModel() {

    val readings = repository.allReadings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun saveReading(decibelLevel: Float) {
        viewModelScope.launch {
            repository.insertReading(
                NoiseReading(decibelLevel = decibelLevel)
            )
        }
    }

    fun deleteHistory() {
        viewModelScope.launch {
            repository.deleteAllReadings()
        }
    }
}
