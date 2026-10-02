from pydantic import BaseModel
from datetime import date
from typing import Optional


class SetBase(BaseModel):
    name: str
    code: str
    language: str
    series: Optional[str] = None
    release_date: Optional[date] = None
    total_cards: int


class SetCreate(SetBase):
    pass


class SetUpdate(BaseModel):
    name: Optional[str] = None
    code: Optional[str] = None
    language: Optional[str] = None
    series: Optional[str] = None
    release_date: Optional[date] = None
    total_cards: Optional[int] = None


class SetSchema(SetBase):
    id: int

    class Config:
        from_attributes = True