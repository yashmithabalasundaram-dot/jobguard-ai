"""
JobGuard AI - Company & Employer Profile Module
"""

from typing import List, Dict, Optional

class Company:
    def __init__(
        self,
        company_id: str,
        name: str,
        location: str,
        industry: str,
        description: str,
        website: str,
        rating: float = 4.5,
        employee_count: str = "100-500",
        open_positions_count: int = 12,
        is_verified: bool = True
    ):
        self.company_id = company_id
        self.name = name
        self.location = location
        self.industry = industry
        self.description = description
        self.website = website
        self.rating = rating
        self.employee_count = employee_count
        self.open_positions_count = open_positions_count
        self.is_verified = is_verified

    def to_dict(self) -> Dict:
        return {
            "company_id": self.company_id,
            "name": self.name,
            "location": self.location,
            "industry": self.industry,
            "description": self.description,
            "website": self.website,
            "rating": self.rating,
            "employee_count": self.employee_count,
            "open_positions_count": self.open_positions_count,
            "is_verified": self.is_verified
        }

class CompanyModule:
    def __init__(self):
        self.companies: Dict[str, Company] = {
            "comp_1": Company(
                company_id="comp_1",
                name="TechCorp Solutions",
                location="San Francisco, CA",
                industry="Software & AI Solutions",
                description="Global leader in mobile and cloud artificial intelligence products.",
                website="https://techcorp-solutions.example.com",
                rating=4.8,
                employee_count="1,000-5,000",
                open_positions_count=14
            ),
            "comp_2": Company(
                company_id="comp_2",
                name="InnovateX Labs",
                location="Remote / Palo Alto, CA",
                industry="NextGen Mobile Technology",
                description="Creating intuitive mobile tools and design frameworks.",
                website="https://innovatex.example.com",
                rating=4.6,
                employee_count="250-500",
                open_positions_count=8
            )
        }

    def get_company_by_id(self, company_id: str) -> Optional[Company]:
        return self.companies.get(company_id) or self.companies.get("comp_1")
