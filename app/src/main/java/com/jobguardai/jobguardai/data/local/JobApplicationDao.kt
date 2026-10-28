package com.jobguardai.jobguardai.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jobguardai.jobguardai.data.model.JobApplication
import kotlinx.coroutines.flow.Flow

@Dao
interface JobApplicationDao {
    @Query("SELECT * FROM job_applications ORDER BY appliedDate DESC")
    fun getAllApplications(): Flow<List<JobApplication>>

    @Query("SELECT * FROM job_applications WHERE id = :id")
    suspend fun getApplicationById(id: String): JobApplication?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApplication(application: JobApplication)
}
