package com.jobguardai.jobguardai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jobguardai.jobguardai.data.model.Placement
import com.jobguardai.jobguardai.data.repository.PlacementRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlacementViewModel(private val repository: PlacementRepository = PlacementRepository()) : ViewModel() {

    private val _placements = MutableStateFlow<List<Placement>>(emptyList())
    val placements: StateFlow<List<Placement>> = _placements.asStateFlow()

    private val _selectedPlacement = MutableStateFlow<Placement?>(null)
    val selectedPlacement: StateFlow<Placement?> = _selectedPlacement.asStateFlow()

    init {
        loadPlacements()
    }

    fun loadPlacements() {
        viewModelScope.launch {
            _placements.value = repository.getPlacements()
        }
    }

    fun selectPlacement(id: String) {
        viewModelScope.launch {
            _selectedPlacement.value = repository.getPlacementById(id)
        }
    }
}
