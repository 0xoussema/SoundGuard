package com.oussema.soundguard.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.oussema.soundguard.data.NoiseReadingRepository

class SoundGuardViewModelFactory(
    private val repository: NoiseReadingRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SoundGuardViewModel::class.java)) {
            return SoundGuardViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
