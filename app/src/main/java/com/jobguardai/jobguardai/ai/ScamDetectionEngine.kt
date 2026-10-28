package com.jobguardai.jobguardai.ai

import com.jobguardai.jobguardai.data.model.Job

data class SafetyReport(
    val isSuspicious: Boolean,
    val riskScorePercent: Int,
    val warningFlags: List<String>,
    val recommendation: String
)

object ScamDetectionEngine {
    fun inspectJobPosting(job: Job): SafetyReport {
        val text = "${job.title} ${job.description} ${job.salary}".lowercase()
        val flags = mutableListOf<String>()

        if (text.contains("upfront") || text.contains("registration fee") || text.contains("pay $") || text.contains("fee")) {
            flags.add("Requires upfront money/fee from applicant")
        }
        if (text.contains("5,000") || text.contains("10,000") || text.contains("unlimited income") || text.contains("earn big")) {
            flags.add("Unrealistically high salary for minimal requirements")
        }
        if (text.contains("no interview") || text.contains("instant hiring") || text.contains("no experience required")) {
            flags.add("Guaranteed instant hiring with zero screening process")
        }
        if (text.contains("telegram") || text.contains("whatsapp only") || text.contains("gmail.com")) {
            flags.add("Requests off-platform communication via unverified channels")
        }

        val isSuspicious = job.isScamFlagged || flags.isNotEmpty()
        val riskScore = if (isSuspicious) (flags.size * 30 + 35).coerceIn(60, 95) else 10

        val recommendation = if (isSuspicious) {
            "CAUTION: This listing shows multiple scam warning indicators. Do NOT pay any upfront fees or share financial credentials."
        } else {
            "VERIFIED: This job listing matches standard employer verification standards. Always verify recruiters via official company channels."
        }

        return SafetyReport(
            isSuspicious = isSuspicious,
            riskScorePercent = riskScore,
            warningFlags = if (flags.isEmpty() && isSuspicious) listOf(job.scamReason ?: "Flagged by community report") else flags,
            recommendation = recommendation
        )
    }
}
