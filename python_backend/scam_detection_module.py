"""
JobGuard AI - Scam & Risk Detection Engine Module
"""

from typing import List, Dict
from job_recommendation_module import Job

class SafetyReport:
    def __init__(
        self,
        is_suspicious: bool,
        risk_score_percent: int,
        warning_flags: List[str],
        recommendation: str
    ):
        self.is_suspicious = is_suspicious
        self.risk_score_percent = risk_score_percent
        self.warning_flags = warning_flags
        self.recommendation = recommendation

    def to_dict(self) -> Dict:
        return {
            "is_suspicious": self.is_suspicious,
            "risk_score_percent": self.risk_score_percent,
            "warning_flags": self.warning_flags,
            "recommendation": self.recommendation
        }

class ScamDetectionEngineModule:
    @classmethod
    def inspect_job_posting(cls, job: Job) -> SafetyReport:
        text = f"{job.title} {job.description} {job.salary}".lower()
        flags = []

        if any(keyword in text for keyword in ["upfront", "registration fee", "pay $", "starter fee"]):
            flags.append("Requires upfront money or registration fee from applicant.")

        if any(keyword in text for keyword in ["5,000", "10,000", "unlimited income", "earn big"]):
            flags.append("Unrealistically high salary for minimal entry-level requirements.")

        if any(keyword in text for keyword in ["no interview", "instant hiring", "no experience required"]):
            flags.append("Guaranteed instant hiring with zero technical screening process.")

        if any(keyword in text for keyword in ["telegram", "whatsapp only", "gmail.com"]):
            flags.append("Requests off-platform communication via unverified personal channels.")

        is_suspicious = job.is_scam_flagged or len(flags) > 0
        risk_score = min(max(len(flags) * 30 + 35, 60), 95) if is_suspicious else 10

        if is_suspicious:
            recommendation = (
                "CAUTION: This listing shows multiple scam warning indicators. "
                "Do NOT pay any upfront fees or share sensitive banking details."
            )
        else:
            recommendation = (
                "VERIFIED: This job listing matches standard employer verification standards. "
                "Always verify recruiters via official company channels."
            )

        if not flags and is_suspicious:
            flags = [job.scam_reason or "Flagged by community safety reports."]

        return SafetyReport(
            is_suspicious=is_suspicious,
            risk_score_percent=risk_score,
            warning_flags=flags,
            recommendation=recommendation
        )
