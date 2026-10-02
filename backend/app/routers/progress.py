from typing import List
from fastapi import APIRouter, Depends, HTTPException, Query
from sqlalchemy.orm import Session
from sqlalchemy import func
from app.database import get_db
from app.models.set_model import Set
from app.models.card_model import Card
from app.models.collection_model import Collection
from app.schemas.progress_schema import SetProgress

router = APIRouter()


@router.get("/sets/{set_id}/progress", response_model=SetProgress)
def get_set_progress(set_id: int, db: Session = Depends(get_db)):
    set_ = db.query(Set).filter(Set.id == set_id).first()
    if not set_:
        raise HTTPException(status_code=404, detail="Set not found")

    total_cards = set_.total_cards

    owned_count = (
        db.query(func.count(func.distinct(Collection.card_id)))
        .join(Card, Collection.card_id == Card.id)
        .filter(Card.set_id == set_id)
        .scalar()
    )

    missing_count = total_cards - (owned_count or 0)
    progress = round(((owned_count or 0) / total_cards) * 100, 1) if total_cards > 0 else 0.0

    return SetProgress(
        set_id=set_.id,
        set_name=set_.name,
        set_code=set_.code,
        total_cards=total_cards,
        owned_cards=owned_count or 0,
        missing_cards=missing_count,
        progress_percentage=progress,
    )