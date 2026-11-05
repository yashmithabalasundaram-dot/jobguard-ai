"""
JobGuard AI - Authentication & Job Seeker Profile Module
"""

from typing import List, Optional, Dict

class JobSeeker:
    def __init__(
        self,
        user_id: str,
        full_name: str,
        email: str,
        phone: str = "",
        headline: str = "",
        bio: str = "",
        location: str = "",
        skills: Optional[List[str]] = None,
        preferred_roles: Optional[List[str]] = None
    ):
        self.user_id = user_id
        self.full_name = full_name
        self.email = email
        self.phone = phone
        self.headline = headline
        self.bio = bio
        self.location = location
        self.skills = skills or []
        self.preferred_roles = preferred_roles or []

    def to_dict(self) -> Dict:
        return {
            "user_id": self.user_id,
            "full_name": self.full_name,
            "email": self.email,
            "phone": self.phone,
            "headline": self.headline,
            "bio": self.bio,
            "location": self.location,
            "skills": self.skills,
            "preferred_roles": self.preferred_roles
        }

class AuthModule:
    def __init__(self):
        self.users: Dict[str, JobSeeker] = {
            "user_1": JobSeeker(
                user_id="user_1",
                full_name="Alex Morgan",
                email="alex.morgan@example.com",
                phone="+1 (555) 019-2834",
                headline="Android Developer | Kotlin & Compose",
                bio="Passionate mobile engineer building native AI-powered Android applications.",
                location="San Francisco, CA",
                skills=["Kotlin", "Jetpack Compose", "MVVM", "Room", "Coroutines"],
                preferred_roles=["Android Developer", "Mobile Engineer"]
            )
        }
        self.active_user_id: Optional[str] = "user_1"

    def register(self, full_name: str, email: str, phone: str, password: str) -> JobSeeker:
        if not full_name or not email or not password:
            raise ValueError("All required fields (full_name, email, password) must be provided.")

        user_id = f"user_{len(self.users) + 1}"
        new_user = JobSeeker(
            user_id=user_id,
            full_name=full_name,
            email=email,
            phone=phone
        )
        self.users[user_id] = new_user
        self.active_user_id = user_id
        return new_user

    def login(self, email: str, password: str) -> JobSeeker:
        for user in self.users.values():
            if user.email.lower() == email.lower():
                self.active_user_id = user.user_id
                return user
        raise ValueError("Invalid email or password.")

    def update_profile(self, user_id: str, **kwargs) -> JobSeeker:
        user = self.users.get(user_id)
        if not user:
            raise KeyError("User not found.")

        for key, value in kwargs.items():
            if hasattr(user, key) and value is not None:
                setattr(user, key, value)
        return user

    def get_current_user(self) -> Optional[JobSeeker]:
        if self.active_user_id:
            return self.users.get(self.active_user_id)
        return None
