package com.jobguardai.jobguardai.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.jobguardai.jobguardai.data.model.Job
import com.jobguardai.jobguardai.data.model.JobApplication
import com.jobguardai.jobguardai.data.model.Resume
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Job::class, Resume::class, JobApplication::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun jobDao(): JobDao
    abstract fun resumeDao(): ResumeDao
    abstract fun jobApplicationDao(): JobApplicationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "jobguard_database"
                )
                .addCallback(DatabaseCallback(context))
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val context: Context
        ) : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateInitialData(database)
                    }
                }
            }
        }

        private suspend fun populateInitialData(database: AppDatabase) {
            val jobDao = database.jobDao()
            val resumeDao = database.resumeDao()
            val appDao = database.jobApplicationDao()

            val initialJobs = listOf(
                Job(
                    id = "job_1",
                    title = "Senior Android Developer",
                    company = "TechCorp Solutions",
                    location = "San Francisco, CA (Hybrid)",
                    type = "Full-Time",
                    salary = "$130k - $160k",
                    experience = "3-5 yrs",
                    description = "We are seeking a talented Senior Android Developer to lead our mobile team in building next-gen AI driven mobile applications using Jetpack Compose, Kotlin Coroutines, and Clean MVVM Architecture.",
                    requirements = listOf(
                        "3+ years of native Android development using Kotlin",
                        "Strong experience with Jetpack Compose & Material 3",
                        "Proficiency in MVVM, Dependency Injection, Room & Coroutines"
                    ),
                    skills = listOf("Kotlin", "Jetpack Compose", "Coroutines", "MVVM", "Room", "REST API"),
                    isSaved = false,
                    matchScore = 94,
                    deadline = "15 Nov 2025"
                ),
                Job(
                    id = "job_2",
                    title = "Mobile UI/UX Engineer",
                    company = "InnovateX Labs",
                    location = "Remote",
                    type = "Remote",
                    salary = "$110k - $140k",
                    experience = "2-4 yrs",
                    description = "InnovateX is looking for a creative UI/UX focused Android Engineer to design and implement slick user interface components with Compose and smooth animation pipelines.",
                    requirements = listOf(
                        "Proficiency in Jetpack Compose UI & Animation APIs",
                        "Eye for pixel-perfect implementation of Figma designs",
                        "Experience with StateFlow, ViewModel, and Navigation Compose"
                    ),
                    skills = listOf("Jetpack Compose", "Figma", "Kotlin", "Material 3", "UI Animations"),
                    isSaved = true,
                    matchScore = 88,
                    deadline = "20 Nov 2025"
                ),
                Job(
                    id = "job_3",
                    title = "AI Mobile Integration Specialist",
                    company = "CyberGenesis Inc.",
                    location = "New York, NY",
                    type = "Contract",
                    salary = "$75 - $90 / hr",
                    experience = "4+ yrs",
                    description = "Join CyberGenesis to embed lightweight ML models and On-Device Generative AI features into native Android clients.",
                    requirements = listOf(
                        "Experience integrating TensorFlow Lite or On-Device AI models in Android",
                        "Deep understanding of async programming with Kotlin Coroutines & Flow"
                    ),
                    skills = listOf("Kotlin", "On-Device AI", "Coroutines", "TensorFlow Lite", "Retrofit"),
                    isSaved = false,
                    matchScore = 82,
                    deadline = "28 Nov 2025"
                ),
                Job(
                    id = "job_4",
                    title = "Urgent Data Entry Specialist (High Salary!)",
                    company = "Global Wealth Systems LLC",
                    location = "Work from Home",
                    type = "Part-Time",
                    salary = "$5,000 / week",
                    experience = "No Experience Required",
                    description = "Earn big money working just 2 hours a day from home! Pay registration fee of $50 upfront to get started immediately. No interview required.",
                    requirements = listOf(
                        "Basic typing skills",
                        "Upfront payment required for starter kit"
                    ),
                    skills = listOf("Data Entry", "Typing"),
                    isSaved = false,
                    isScamFlagged = true,
                    scamReason = "SUSPICIOUS: Requires upfront payment ($50), unrealistically high pay ($5,000/wk) for entry level, and no interview required.",
                    matchScore = 20,
                    deadline = "Immediate"
                )
            )
            jobDao.insertJobs(initialJobs)

            val initialResume = Resume(
                id = "res_1",
                fileName = "Alex_Morgan_Resume_2025.pdf",
                fileUri = "content://com.android.providers.downloads.documents/document/resume_1.pdf",
                uploadDate = "12 Oct 2025",
                fileSize = "1.4 MB",
                parsedText = "Alex Morgan - Senior Android Engineer. Proficient in Kotlin, Jetpack Compose, MVVM, Room, Coroutines, Git, CI/CD, and REST APIs.",
                extractedSkills = listOf("Kotlin", "Jetpack Compose", "Coroutines", "MVVM Architecture", "Room DB", "REST APIs", "Git"),
                extractedEducation = listOf("B.S. Computer Science - Stanford University (2020-2024)"),
                extractedExperience = listOf("Android Developer Intern - TechCorp (2023-2024)", "Mobile Project Lead - Campus App (2022-2023)"),
                missingSections = listOf("Certifications", "Quantified Achievements in Past Work"),
                improvementSuggestions = listOf(
                    "Add measurable metrics to project outcomes (e.g., 'Improved app speed by 35%')",
                    "Include links to live GitHub repositories or Google Play apps",
                    "Highlight experience with modern Kotlin Flow and dependency injection (Hilt)"
                ),
                overallScore = 88,
                isDefault = true
            )
            resumeDao.insertResume(initialResume)

            val initialApp = JobApplication(
                id = "app_101",
                jobId = "job_1",
                jobTitle = "Senior Android Developer",
                companyName = "TechCorp Solutions",
                appliedDate = "Today",
                status = "Under Review",
                resumeId = "res_1",
                coverNote = "I am excited to contribute my expertise in Jetpack Compose and Kotlin Coroutines to TechCorp."
            )
            appDao.insertApplication(initialApp)
        }
    }
}
