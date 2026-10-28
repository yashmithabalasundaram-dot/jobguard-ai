package com.jobguardai.jobguardai.data.remote

import com.jobguardai.jobguardai.data.model.Company
import com.jobguardai.jobguardai.data.model.Job
import com.jobguardai.jobguardai.data.model.Placement
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("jobs")
    suspend fun getJobs(@Query("query") query: String? = null): Response<List<Job>>

    @GET("jobs/{id}")
    suspend fun getJobById(@Path("id") id: String): Response<Job>

    @GET("companies/{id}")
    suspend fun getCompanyById(@Path("id") id: String): Response<Company>

    @GET("placements")
    suspend fun getPlacements(): Response<List<Placement>>
}
