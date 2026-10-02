from pydantic import BaseModel
from typing import Optional


class CardBase(BaseModel):
    set_id: int
    name: str
    number: str
    language: str
    rarity: Optional[str] = None
    variant: Optional[str] = None


class CardCreate(CardBase):
    pass


class CardUpdate(BaseModel):
    set_id: Optional[int] = None
    name: Optional[str] = None
    number: Optional[str] = None
    language: Optional[str] = None
    rarity: Optional[str] = None
    variant: Optional[str] = None


class CardSchema(CardBase):
    id: int

    class Config:
        from_attributes = True