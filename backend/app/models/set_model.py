from sqlalchemy import Column, Integer, String, Date
from app.database import Base


class Set(Base):
    __tablename__ = "sets"

    id = Column(Integer, primary_key=True, index=True)
    name = Column(String, nullable=False)
    code = Column(String, unique=True, nullable=False)
    language = Column(String, nullable=False)
    series = Column(String)
    release_date = Column(Date)
    total_cards = Column(Integer, nullable=False)