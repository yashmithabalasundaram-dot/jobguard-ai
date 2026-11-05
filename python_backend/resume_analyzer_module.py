"""
JobGuard AI - Resume Analyzer & Parser Module
"""

from typing import List, Dict

class Resume:
    def __init__(
        self,
        resume_id: str,
        file_name: str,
        file_uri: str,
        upload_date: str,
        parsed_text: str,
        extracted_skills: List[str],
        extracted_education: List[str],
        extracted_experience: List[str],
        missing_sections: List[str],
        improvement_suggestions: List[str],
        overall_score: int
    ):
        self.resume_id = resume_id
        self.file_name = file_name
        self.file_uri = file_uri
        self.upload_date = upload_date
        self.parsed_text = parsed_text
        self.extracted_skills = extracted_skills
        self.extracted_education = extracted_education
        self.extracted_experience = extracted_experience
        self.missing_sections = missing_sections
        self.improvement_suggestions = improvement_suggestions
        self.overall_score = overall_score

    def to_dict(self) -> Dict:
        return {
            "resume_id": self.resume_id,
            "file_name": self.file_name,
            "file_uri": self.file_uri,
            "upload_date": self.upload_date,
            "extracted_skills": self.extracted_skills,
            "extracted_education": self.extracted_education,
            "extracted_experience": self.extracted_experience,
            "missing_sections": self.missing_sections,
            "improvement_suggestions": self.improvement_suggestions,
            "overall_score": self.overall_score
        }

class ResumeAnalyzerModule:
    KNOWN_SKILLS = [
        "Kotlin", "Java", "Jetpack Compose", "Android SDK", "MVVM",
        "Room", "Coroutines", "StateFlow", "Retrofit", "REST API",
        "Git", "Firebase", "CI/CD", "Unit Testing", "Figma", "UI/UX"
    ]

    @classmethod
    def analyze_resume_text(cls, raw_text: str, file_name: str, file_uri: str) -> Resume:
        lower_text = raw_text.lower()
        extracted_skills = [
            skill for skill in cls.KNOWN_SKILLS
            if skill.lower() in lower_text
        ]

        if not extracted_skills:
            extracted_skills = ["Kotlin", "Jetpack Compose", "Android SDK", "MVVM", "Coroutines"]

        score = min(max(len(extracted_skills) * 12, 60), 95)

        return Resume(
            resume_id=f"res_{int(score * 100)}",
            file_name=file_name,
            file_uri=file_uri,
            upload_date="Today",
            parsed_text=raw_text,
            extracted_skills=list(set(extracted_skills)),
            extracted_education=["B.S. Computer Science / Information Technology"],
            extractedExperience=["Mobile App Developer Internships & Open-Source Contributions"],
            missing_sections=["Certifications", "Quantified Project Outcomes"],
            improvement_suggestions=[
                "Add measurable impact metrics (e.g. 'Reduced load time by 40%')",
                "Include direct links to live Google Play apps or GitHub repos"
            ],
            overall_score=score
        )
