package com.jobguardai.jobguardai.data.repository

import com.jobguardai.jobguardai.data.model.Company
import com.jobguardai.jobguardai.data.remote.ApiService
import com.jobguardai.jobguardai.data.remote.RetrofitClient

class CompanyRepository(
    private val apiService: ApiService = RetrofitClient.getApiService()
) {
    private val demoCompanies = listOf(
        Company(
            id = "comp_1",
            name = "TechCorp Solutions",
            location = "San Francisco, CA",
            industry = "Software & AI Solutions",
            description = "TechCorp Solutions is a global leader in cloud and mobile technology solutions powering fortune 500 enterprises with cutting edge AI products.",
            website = "https://techcorp-solutions.example.com",
            rating = 4.8,
            employeeCount = "1,000-5,000",
            openPositionsCount = 14
        ),
        Company(
            id = "comp_2",
            name = "InnovateX Labs",
            location = "Remote / Palo Alto, CA",
            industry = "NextGen Mobile Technology",
            description = "InnovateX Labs creates intuitive mobile tools and design frameworks used by millions of developers worldwide.",
            website = "https://innovatex.example.com",
            rating = 4.6,
            employeeCount = "250-500",
            openPositionsCount = 8
        )
    )

    suspend fun getCompanyById(id: String): Company? {
        try {
            val response = apiService.getCompanyById(id)
            if (response.isSuccessful && response.body()?.success == true) {
                val comp = response.body()!!.data
                if (comp != null) return comp
            }
        } catch (e: Exception) {
            // Fallback
        }
        return demoCompanies.find { it.id == id } ?: demoCompanies.first()
    }
}
