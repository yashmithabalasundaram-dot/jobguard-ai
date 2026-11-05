"""
JobGuard AI - Python Modules Demonstration Runner
"""

from auth_module import AuthModule
from resume_analyzer_module import ResumeAnalyzerModule
from job_recommendation_module import JobRecommendationModule
from scam_detection_module import ScamDetectionEngineModule
from skill_gap_analyzer_module import SkillGapAnalyzerModule
from company_module import CompanyModule
from application_placement_module import ApplicationPlacementModule

def main():
    print("==================================================")
    print("      JOBGUARD AI - PYTHON BACKEND SERVICES       ")
    print("==================================================")

    # 1. Auth Module
    auth = AuthModule()
    user = auth.get_current_user()
    print(f"\n[1. AUTH MODULE] Active User: {user.full_name} ({user.email})")

    # 2. Resume Analyzer Module
    raw_resume_text = "Alex Morgan - Senior Android Engineer proficient in Kotlin, Jetpack Compose, MVVM, Room, and Coroutines."
    resume = ResumeAnalyzerModule.analyze_resume_text(raw_resume_text, "Alex_Resume_2025.pdf", "/docs/resume_1.pdf")
    print(f"[2. RESUME ANALYZER] Analyzed Resume Score: {resume.overallScore}/100")
    print(f"     Extracted Skills: {', '.join(resume.extracted_skills)}")

    # 3. Job Recommendation Module
    job_engine = JobRecommendationModule()
    jobs = job_engine.search_jobs(query="Android")
    print(f"\n[3. JOB RECOMMENDATION] Found {len(jobs)} jobs for query 'Android':")
    for j in jobs:
        print(f"     - {j.title} at {j.company} (Match Score: {j.match_score}%)")

    # 4. Scam Detection Module
    scam_job = job_engine.jobs_db[-1] # Urgent Data Entry
    report = ScamDetectionEngineModule.inspect_job_posting(scam_job)
    print(f"\n[4. SCAM DETECTION] Inspecting Job: '{scam_job.title}'")
    print(f"     Is Suspicious: {report.is_suspicious}")
    print(f"     Risk Score: {report.risk_score_percent}%")
    print(f"     Warning Flags: {report.warning_flags}")

    # 5. Skill Gap Analyzer Module
    user_skills = user.skills
    required = ["Kotlin", "Jetpack Compose", "Coroutines", "Hilt DI", "Kotlin Flow"]
    gap_result = SkillGapAnalyzerModule.analyze_gaps(user_skills, required)
    print(f"\n[5. SKILL GAP ANALYZER] Job Readiness: {gap_result.readiness_percentage}%")
    print(f"     Missing Skills: {', '.join(gap_result.missing_skills)}")
    print(f"     Recommended Courses: {len(gap_result.recommended_courses)}")

    # 6. Company Module
    company_mod = CompanyModule()
    comp = company_mod.get_company_by_id("comp_1")
    print(f"\n[6. COMPANY MODULE] {comp.name} - Rating: {comp.rating} ({comp.open_positions_count} open jobs)")

    # 7. Application & Placement Module
    app_mod = ApplicationPlacementModule()
    new_app = app_mod.submit_application(
        job_id="job_1",
        job_title="Senior Android Developer",
        company_name="TechCorp Solutions",
        resume_id=resume.resume_id,
        cover_note="Excited to apply!"
    )
    print(f"\n[7. APPLICATION MODULE] Submitted App ID: {new_app.application_id} Status: {new_app.status}")
    print(f"     Total Active Applications: {len(app_mod.get_applications())}")
    print(f"     Campus Placements Drive: {len(app_mod.get_placements())} Available")

    print("\n==================================================")
    print("   ALL PYTHON MODULES EXECUTED SUCCESSFULLY!      ")
    print("==================================================")

if __name__ == "__main__":
    main()
