package com.example.resepapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.resepapp.data.Resep
import com.example.resepapp.data.ResepRepository
import kotlinx.coroutines.launch

class ResepViewModel(private val repository: ResepRepository) : ViewModel() {

    // Cache all resep from the repository using LiveData.
    val allResep = repository.allResep.asLiveData()

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(resep: Resep) = viewModelScope.launch {
        repository.insert(resep)
    }

    fun update(resep: Resep) = viewModelScope.launch {
        repository.update(resep)
    }

    fun delete(resep: Resep) = viewModelScope.launch {
        repository.delete(resep)
    }

    fun getResepById(id: Int) = repository.getResepById(id).asLiveData()

    fun searchResep(query: String) = repository.searchResep(query).asLiveData()
}

class ResepViewModelFactory(private val repository: ResepRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ResepViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ResepViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}