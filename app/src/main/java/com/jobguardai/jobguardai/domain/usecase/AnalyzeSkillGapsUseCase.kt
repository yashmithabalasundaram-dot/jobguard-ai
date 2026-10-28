package com.jobguardai.jobguardai.domain.usecase

import com.jobguardai.jobguardai.ai.SkillGapAnalyzer
import com.jobguardai.jobguardai.ai.SkillGapResult

class AnalyzeSkillGapsUseCase {
    operator fun invoke(userSkills: List<String>, requiredSkills: List<String>): SkillGapResult {
        return SkillGapAnalyzer.analyze(userSkills, requiredSkills)
    }
}
