"""
JobGuard AI - Skill Gap & Personalized Learning Roadmap Module
"""

from typing import List, Dict

class Course:
    def __init__(
        self,
        course_id: str,
        title: str,
        provider: str,
        duration: str,
        level: str,
        skill_category: str,
        description: str
    ):
        self.course_id = course_id
        self.title = title
        self.provider = provider
        self.duration = duration
        self.level = level
        self.skill_category = skill_category
        self.description = description

    def to_dict(self) -> Dict:
        return {
            "course_id": self.course_id,
            "title": self.title,
            "provider": self.provider,
            "duration": self.duration,
            "level": self.level,
            "skill_category": self.skill_category,
            "description": self.description
        }

class SkillGapResult:
    def __init__(
        self,
        matched_skills: List[str],
        missing_skills: List[str],
        readiness_percentage: int,
        recommended_courses: List[Course]
    ):
        self.matched_skills = matched_skills
        self.missing_skills = missing_skills
        self.readiness_percentage = readiness_percentage
        self.recommended_courses = recommended_courses

    def to_dict(self) -> Dict:
        return {
            "matched_skills": self.matched_skills,
            "missing_skills": self.missing_skills,
            "readiness_percentage": self.readiness_percentage,
            "recommended_courses": [c.to_dict() for c in self.recommended_courses]
        }

class SkillGapAnalyzerModule:
    @classmethod
    def analyze_gaps(cls, user_skills: List[str], required_skills: List[str]) -> SkillGapResult:
        user_skills_lower = [s.lower() for s in user_skills]
        matched = []
        missing = []

        for skill in required_skills:
            if skill.lower() in user_skills_lower:
                matched.append(skill)
            else:
                missing.append(skill)

        total = max(len(required_skills), 1)
        readiness = min(max(int((len(matched) / total) * 100), 20), 100)

        courses = []
        for idx, skill in enumerate(missing):
            courses.append(
                Course(
                    course_id=f"course_{idx + 1}",
                    title=f"Mastering {skill} for Modern Android",
                    provider="JobGuard AI Academy",
                    duration="4 Hours",
                    level="Intermediate",
                    skill_category=skill,
                    description=f"Comprehensive practical course covering {skill} with hands-on Kotlin projects."
                )
            )

        return SkillGapResult(
            matched_skills=matched,
            missing_skills=missing,
            readiness_percentage=readiness,
            recommended_courses=courses
        )
