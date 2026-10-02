from typing import List, Optional
from fastapi import APIRouter, Depends, HTTPException, Query
from sqlalchemy.orm import Session
from app.database import get_db
from app.models.set_model import Set
from app.schemas.set_schema import SetSchema, SetCreate, SetUpdate

router = APIRouter()


@router.get("/sets", response_model=List[SetSchema])
def get_sets(
    series: Optional[str] = Query(None),
    language: Optional[str] = Query(None),
    db: Session = Depends(get_db),
):
    query = db.query(Set)
    if series:
        query = query.filter(Set.series == series)
    if language:
        query = query.filter(Set.language == language)
    sets = query.all()
    return sets


@router.get("/sets/{set_id}", response_model=SetSchema)
def get_set(set_id: int, db: Session = Depends(get_db)):
    set_ = db.query(Set).filter(Set.id == set_id).first()
    if not set_:
        raise HTTPException(status_code=404, detail="Set not found")
    return set_