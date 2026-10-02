from pydantic import BaseModel


class SetProgress(BaseModel):
    set_id: int
    set_name: str
    set_code: str
    total_cards: int
    owned_cards: int
    missing_cards: int
    progress_percentage: float