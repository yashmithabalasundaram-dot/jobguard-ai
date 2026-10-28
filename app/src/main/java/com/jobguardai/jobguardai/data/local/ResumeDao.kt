package com.jobguardai.jobguardai.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.jobguardai.jobguardai.data.model.Resume
import kotlinx.coroutines.flow.Flow

@Dao
interface ResumeDao {
    @Query("SELECT * FROM resumes")
    fun getAllResumes(): Flow<List<Resume>>

    @Query("SELECT * FROM resumes WHERE isDefault = 1 LIMIT 1")
    suspend fun getDefaultResume(): Resume?

    @Query("SELECT * FROM resumes WHERE id = :id")
    suspend fun getResumeById(id: String): Resume?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResume(resume: Resume)

    @Update
    suspend fun updateResume(resume: Resume)

    @Query("DELETE FROM resumes WHERE id = :id")
    suspend fun deleteResume(id: String)
}
