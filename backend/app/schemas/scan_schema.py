from pydantic import BaseModel
from typing import Optional


class ScanRequest(BaseModel):
    game: str
    name: str
    language: str
    set: str
    set_code: str
    card_number: str
    rarity: Optional[str] = None
    variant: Optional[str] = None


class ScanResponse(BaseModel):
    success: bool
    card_id: Optional[int] = None
    name: str
    set_name: str
    set_code: str
    card_number: str
    rarity: Optional[str] = None
    variant: Optional[str] = None
    message: Optional[str] = None