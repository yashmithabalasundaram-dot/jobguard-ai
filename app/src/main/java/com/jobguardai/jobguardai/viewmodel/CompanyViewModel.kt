package com.jobguardai.jobguardai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jobguardai.jobguardai.data.model.Company
import com.jobguardai.jobguardai.data.repository.CompanyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CompanyViewModel(private val repository: CompanyRepository = CompanyRepository()) : ViewModel() {

    private val _selectedCompany = MutableStateFlow<Company?>(null)
    val selectedCompany: StateFlow<Company?> = _selectedCompany.asStateFlow()

    fun loadCompany(companyId: String) {
        viewModelScope.launch {
            _selectedCompany.value = repository.getCompanyById(companyId)
        }
    }
}
