from sqlalchemy import Column, Integer, String, ForeignKey
from sqlalchemy.orm import relationship
from app.database import Base


class Card(Base):
    __tablename__ = "cards"

    id = Column(Integer, primary_key=True, index=True)
    set_id = Column(Integer, ForeignKey("sets.id"), nullable=False)
    name = Column(String, nullable=False)
    number = Column(String, nullable=False)
    language = Column(String, nullable=False)
    rarity = Column(String)
    variant = Column(String)

    set_ = relationship("Set", backref="cards")