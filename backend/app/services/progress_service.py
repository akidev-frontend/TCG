from app.schemas.progress_schema import SetProgress
from app.database import SessionLocal
from app.models.set_model import Set
from app.models.card_model import Card
from app.models.collection_model import Collection
from sqlalchemy import func


class ProgressService:
    @staticmethod
    def get_set_progress(set_id: int) -> SetProgress | None:
        db = SessionLocal()
        try:
            set_ = db.query(Set).filter(Set.id == set_id).first()
            if not set_:
                return None

            total_cards = set_.total_cards

            owned_count = (
                db.query(func.count(func.distinct(Collection.card_id)))
                .join(Card, Collection.card_id == Card.id)
                .filter(Card.set_id == set_id)
                .scalar()
            )

            owned_count = owned_count or 0
            missing_count = total_cards - owned_count
            progress = round((owned_count / total_cards) * 100, 1) if total_cards > 0 else 0.0

            return SetProgress(
                set_id=set_.id,
                set_name=set_.name,
                set_code=set_.code,
                total_cards=total_cards,
                owned_cards=owned_count,
                missing_cards=missing_count,
                progress_percentage=progress,
            )
        finally:
            db.close()