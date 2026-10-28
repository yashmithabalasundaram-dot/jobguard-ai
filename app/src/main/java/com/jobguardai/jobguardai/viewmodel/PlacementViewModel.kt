package com.jobguardai.jobguardai.viewmodel

import androidx.lifecycle.ViewModel
import com.jobguardai.jobguardai.data.model.Placement
import com.jobguardai.jobguardai.data.repository.PlacementRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PlacementViewModel(private val repository: PlacementRepository = PlacementRepository()) : ViewModel() {

    private val _placements = MutableStateFlow<List<Placement>>(repository.getPlacements())
    val placements: StateFlow<List<Placement>> = _placements.asStateFlow()

    private val _selectedPlacement = MutableStateFlow<Placement?>(null)
    val selectedPlacement: StateFlow<Placement?> = _selectedPlacement.asStateFlow()

    fun selectPlacement(id: String) {
        _selectedPlacement.value = repository.getPlacementById(id)
    }
}
