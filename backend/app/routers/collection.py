from typing import List, Optional
from fastapi import APIRouter, Depends, HTTPException, Query
from sqlalchemy.orm import Session
from sqlalchemy import func
from app.database import get_db
from app.models.collection_model import Collection
from app.models.card_model import Card
from app.models.set_model import Set
from app.schemas.collection_schema import (
    CollectionSchema,
    CollectionCreate,
    CollectionUpdate,
)

router = APIRouter()


@router.get("/collection", response_model=List[CollectionSchema])
def get_collection(
    card_id: Optional[int] = Query(None),
    db: Session = Depends(get_db),
):
    query = db.query(Collection)
    if card_id is not None:
        query = query.filter(Collection.card_id == card_id)
    entries = query.all()
    return entries


@router.post("/collection", response_model=CollectionSchema, status_code=201)
def add_to_collection(entry: CollectionCreate, db: Session = Depends(get_db)):
    db_entry = Collection(**entry.model_dump())
    db.add(db_entry)
    db.commit()
    db.refresh(db_entry)
    return db_entry


@router.put("/collection/{entry_id}", response_model=CollectionSchema)
def update_collection(entry_id: int, entry: CollectionUpdate, db: Session = Depends(get_db)):
    db_entry = db.query(Collection).filter(Collection.id == entry_id).first()
    if not db_entry:
        raise HTTPException(status_code=404, detail="Collection entry not found")
    update_data = entry.model_dump(exclude_unset=True)
    for key, value in update_data.items():
        setattr(db_entry, key, value)
    db.commit()
    db.refresh(db_entry)
    return db_entry


@router.delete("/collection/{entry_id}", status_code=204)
def delete_from_collection(entry_id: int, db: Session = Depends(get_db)):
    db_entry = db.query(Collection).filter(Collection.id == entry_id).first()
    if not db_entry:
        raise HTTPException(status_code=404, detail="Collection entry not found")
    db.delete(db_entry)
    db.commit()