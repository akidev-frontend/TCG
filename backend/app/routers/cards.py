from typing import List, Optional
from fastapi import APIRouter, Depends, HTTPException, Query
from sqlalchemy.orm import Session
from app.database import get_db
from app.models.card_model import Card
from app.schemas.card_schema import CardSchema, CardCreate, CardUpdate

router = APIRouter()


@router.get("/cards", response_model=List[CardSchema])
def get_cards(
    set_id: Optional[int] = Query(None),
    name: Optional[str] = Query(None),
    language: Optional[str] = Query(None),
    db: Session = Depends(get_db),
):
    query = db.query(Card)
    if set_id is not None:
        query = query.filter(Card.set_id == set_id)
    if name:
        query = query.filter(Card.name.ilike(f"%{name}%"))
    if language:
        query = query.filter(Card.language == language)
    cards = query.all()
    return cards


@router.get("/cards/{card_id}", response_model=CardSchema)
def get_card(card_id: int, db: Session = Depends(get_db)):
    card = db.query(Card).filter(Card.id == card_id).first()
    if not card:
        raise HTTPException(status_code=404, detail="Card not found")
    return card