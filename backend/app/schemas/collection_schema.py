from pydantic import BaseModel
from datetime import date
from typing import Optional


class CollectionBase(BaseModel):
    card_id: int
    quantity: int = 1
    condition: Optional[str] = None
    purchase_price: Optional[float] = None
    purchase_date: Optional[date] = None
    notes: Optional[str] = None


class CollectionCreate(CollectionBase):
    pass


class CollectionUpdate(BaseModel):
    card_id: Optional[int] = None
    quantity: Optional[int] = None
    condition: Optional[str] = None
    purchase_price: Optional[float] = None
    purchase_date: Optional[date] = None
    notes: Optional[str] = None


class CollectionSchema(CollectionBase):
    id: int
    created_at: date
    updated_at: date

    class Config:
        from_attributes = True