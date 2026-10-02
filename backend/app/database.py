from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker, Session, declarative_base
from app.config import DATABASE_URL

Base = declarative_base()
engine = create_engine(DATABASE_URL, pool_pre_ping=True)
SessionLocal = sessionmaker(bind=engine)


def get_db() -> Session:
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()