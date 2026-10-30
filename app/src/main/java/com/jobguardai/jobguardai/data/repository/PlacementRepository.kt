package com.jobguardai.jobguardai.data.repository

import com.jobguardai.jobguardai.data.model.Placement
import com.jobguardai.jobguardai.data.remote.ApiService
import com.jobguardai.jobguardai.data.remote.RetrofitClient

class PlacementRepository(
    private val apiService: ApiService = RetrofitClient.getApiService()
) {
    private val demoPlacements = listOf(
        Placement(
            id = "place_1",
            companyName = "Microsoft Placement Drive 2025",
            driveTitle = "Campus Placement Drive - Software Engineer",
            role = "Software Development Engineer (SDE-1)",
            packageAmount = "$120,000 / annum",
            eligibilityCriteria = "CGPA 7.5+ in B.Tech / M.Tech CS/IT (Batch 2025/2026)",
            driveDate = "15 Nov 2025",
            location = "On-Campus & Virtual",
            status = "Ongoing",
            description = "Microsoft Campus Recruitment Drive for final year software engineering students. Multiple roles available in Mobile, Cloud, and Systems.",
            rounds = listOf("Aptitude & Coding Assessment", "Technical Interview Round 1", "Technical Interview Round 2", "HR & Culture Round"),
            registrationDeadline = "10 Nov 2025"
        ),
        Placement(
            id = "place_2",
            companyName = "Google University Recruitment",
            driveTitle = "Associate Android Developer Drive",
            role = "Associate Mobile Software Engineer",
            packageAmount = "$135,000 / annum",
            eligibilityCriteria = "B.E / B.Tech / B.Sc CS graduates with strong DSA and Kotlin knowledge",
            driveDate = "22 Nov 2025",
            location = "Virtual Drive",
            status = "Upcoming",
            description = "Google is hiring upcoming graduates for Android ecosystem teams. Candidates will work on Android OS components and Google Play services.",
            rounds = listOf("Online Coding Challenge (2 Hours)", "System Design & DSA Interview", "Googleiness Leadership Round"),
            registrationDeadline = "18 Nov 2025"
        )
    )

    suspend fun getPlacements(): List<Placement> {
        try {
            val response = apiService.getPlacements()
            if (response.isSuccessful && response.body()?.success == true) {
                val list = response.body()!!.data
                if (!list.isNullOrEmpty()) return list
            }
        } catch (e: Exception) {
            // Fallback
        }
        return demoPlacements
    }

    suspend fun getPlacementById(id: String): Placement? {
        try {
            val response = apiService.getPlacementById(id)
            if (response.isSuccessful && response.body()?.success == true) {
                val p = response.body()!!.data
                if (p != null) return p
            }
        } catch (e: Exception) {
            // Fallback
        }
        return demoPlacements.find { it.id == id } ?: demoPlacements.first()
    }
}
