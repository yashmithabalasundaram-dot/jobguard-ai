package com.jobguardai.jobguardai.domain.usecase

import com.jobguardai.jobguardai.ai.ResumeAnalyzer
import com.jobguardai.jobguardai.data.model.Resume

class AnalyzeResumeUseCase {
    operator fun invoke(rawText: String, fileName: String, fileUri: String): Resume {
        return ResumeAnalyzer.analyzeResumeText(rawText, fileName, fileUri)
    }
}
