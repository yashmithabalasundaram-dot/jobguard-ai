"""
JobGuard AI - Job Recommendation & Search Module
"""

from typing import List, Dict, Optional

class Job:
    def __init__(
        self,
        job_id: str,
        title: str,
        company: str,
        location: str,
        type_: str,
        salary: str,
        experience: str,
        description: str,
        requirements: List[str],
        skills: List[str],
        is_saved: bool = False,
        is_scam_flagged: bool = False,
        scam_reason: Optional[str] = None,
        match_score: int = 85
    ):
        self.job_id = job_id
        self.title = title
        self.company = company
        self.location = location
        self.type_ = type_
        self.salary = salary
        self.experience = experience
        self.description = description
        self.requirements = requirements
        self.skills = skills
        self.is_saved = is_saved
        self.is_scam_flagged = is_scam_flagged
        self.scam_reason = scam_reason
        self.match_score = match_score

    def to_dict(self) -> Dict:
        return {
            "job_id": self.job_id,
            "title": self.title,
            "company": self.company,
            "location": self.location,
            "type": self.type_,
            "salary": self.salary,
            "experience": self.experience,
            "description": self.description,
            "skills": self.skills,
            "is_saved": self.is_saved,
            "is_scam_flagged": self.is_scam_flagged,
            "match_score": self.match_score
        }

class JobRecommendationModule:
    def __init__(self):
        self.jobs_db: List[Job] = [
            Job(
                job_id="job_1",
                title="Senior Android Developer",
                company="TechCorp Solutions",
                location="San Francisco, CA (Hybrid)",
                type_="Full-Time",
                salary="$130k - $160k",
                experience="3-5 yrs",
                description="Lead mobile development team using Jetpack Compose & Coroutines.",
                requirements=["Kotlin", "Jetpack Compose", "MVVM Architecture"],
                skills=["Kotlin", "Jetpack Compose", "Coroutines", "MVVM", "Room", "REST API"],
                match_score=94
            ),
            Job(
                job_id="job_2",
                title="Mobile UI/UX Engineer",
                company="InnovateX Labs",
                location="Remote",
                type_="Remote",
                salary="$110k - $140k",
                experience="2-4 yrs",
                description="Design interactive mobile interfaces with Material 3.",
                requirements=["Compose UI", "Material 3", "StateFlow"],
                skills=["Jetpack Compose", "Figma", "Kotlin", "Material 3"],
                is_saved=True,
                match_score=88
            ),
            Job(
                job_id="job_4",
                title="Urgent Data Entry Specialist (High Salary!)",
                company="Global Wealth Systems LLC",
                location="Work from Home",
                type_="Part-Time",
                salary="$5,000 / week",
                experience="No Experience Required",
                description="Earn big money from home! Pay $50 starter fee upfront.",
                requirements=["Typing", "Upfront fee required"],
                skills=["Data Entry", "Typing"],
                is_scam_flagged=True,
                scam_reason="Requires upfront fee ($50) for no experience.",
                match_score=20
            )
        ]

    def search_jobs(self, query: str = "", role_filter: str = "All") -> List[Job]:
        query_lower = query.lower().strip()
        filtered = []

        for job in self.jobs_db:
            matches_query = (
                not query_lower or
                query_lower in job.title.lower() or
                query_lower in job.company.lower() or
                query_lower in job.location.lower() or
                any(query_lower in s.lower() for s in job.skills)
            )
            matches_role = (role_filter == "All" or job.type_.lower() == role_filter.lower())

            if matches_query and matches_role:
                filtered.append(job)

        return filtered

    def get_saved_jobs(self) -> List[Job]:
        return [job for job in self.jobs_db if job.is_saved]

    def toggle_save_job(self, job_id: str) -> Optional[Job]:
        for job in self.jobs_db:
            if job.job_id == job_id:
                job.is_saved = not job.is_saved
                return job
        return None

    def calculate_match_score(self, user_skills: List[str], job: Job) -> int:
        if not user_skills or not job.skills:
            return job.match_score

        user_skills_lower = [s.lower() for s in user_skills]
        matched = sum(1 for skill in job.skills if skill.lower() in user_skills_lower)
        score = int((matched / len(job.skills)) * 100)
        return min(max(score, 40), 98)
