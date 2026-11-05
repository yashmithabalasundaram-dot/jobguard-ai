"""
JobGuard AI - Applications & Campus Placement Drive Module
"""

from typing import List, Dict, Optional

class JobApplication:
    def __init__(
        self,
        application_id: str,
        job_id: str,
        job_title: str,
        company_name: str,
        applied_date: str,
        status: str,
        resume_id: str,
        cover_note: str = ""
    ):
        self.application_id = application_id
        self.job_id = job_id
        self.job_title = job_title
        self.company_name = company_name
        self.applied_date = applied_date
        self.status = status
        self.resume_id = resume_id
        self.cover_note = cover_note

    def to_dict(self) -> Dict:
        return {
            "application_id": self.application_id,
            "job_id": self.job_id,
            "job_title": self.job_title,
            "company_name": self.company_name,
            "applied_date": self.applied_date,
            "status": self.status,
            "resume_id": self.resume_id,
            "cover_note": self.cover_note
        }

class Placement:
    def __init__(
        self,
        placement_id: str,
        company_name: str,
        drive_title: str,
        role: str,
        package_amount: str,
        eligibility_criteria: str,
        drive_date: str,
        location: str,
        status: str,
        description: str
    ):
        self.placement_id = placement_id
        self.company_name = company_name
        self.drive_title = drive_title
        self.role = role
        self.package_amount = package_amount
        self.eligibility_criteria = eligibility_criteria
        self.drive_date = drive_date
        self.location = location
        self.status = status
        self.description = description

    def to_dict(self) -> Dict:
        return {
            "placement_id": self.placement_id,
            "company_name": self.company_name,
            "drive_title": self.drive_title,
            "role": self.role,
            "package_amount": self.package_amount,
            "eligibility_criteria": self.eligibility_criteria,
            "drive_date": self.drive_date,
            "location": self.location,
            "status": self.status,
            "description": self.description
        }

class ApplicationPlacementModule:
    def __init__(self):
        self.applications: List[JobApplication] = [
            JobApplication(
                application_id="app_101",
                job_id="job_1",
                job_title="Senior Android Developer",
                company_name="TechCorp Solutions",
                applied_date="Today",
                status="Under Review",
                resume_id="res_1",
                cover_note="Excited to contribute Kotlin & Jetpack Compose expertise."
            )
        ]
        self.placements: List[Placement] = [
            Placement(
                placement_id="place_1",
                company_name="Microsoft Placement Drive 2025",
                drive_title="Campus Drive - Software Engineer",
                role="Software Development Engineer (SDE-1)",
                package_amount="$120,000 / annum",
                eligibility_criteria="CGPA 7.5+ in B.Tech CS/IT",
                drive_date="15 Nov 2025",
                location="On-Campus & Virtual",
                status="Ongoing",
                description="Microsoft Campus Recruitment Drive for final year software engineering students."
            ),
            Placement(
                placement_id="place_2",
                company_name="Google University Recruitment",
                drive_title="Associate Android Developer Drive",
                role="Associate Mobile Software Engineer",
                package_amount="$135,000 / annum",
                eligibility_criteria="B.Tech CS graduates",
                drive_date="22 Nov 2025",
                location="Virtual Drive",
                status="Upcoming",
                description="Google is hiring upcoming graduates for Android ecosystem teams."
            )
        ]

    def submit_application(self, job_id: str, job_title: str, company_name: str, resume_id: str, cover_note: str) -> JobApplication:
        app_id = f"app_{len(self.applications) + 101}"
        new_app = JobApplication(
            application_id=app_id,
            job_id=job_id,
            job_title=job_title,
            company_name=company_name,
            applied_date="Today",
            status="Submitted",
            resume_id=resume_id,
            cover_note=cover_note
        )
        self.applications.append(new_app)
        return new_app

    def get_applications(self) -> List[JobApplication]:
        return self.applications

    def get_placements(self) -> List[Placement]:
        return self.placements
