#!/usr/bin/env python3
"""
Importa el catálogo de cartas Pokémon a PostgreSQL.

Lee de all-cards.json (incluido en el repo) y inserta en sets y cards.

Uso: python seed_catalog.py
"""

import json
import sys
from sqlalchemy.orm import Session
from app.database import engine, SessionLocal
from app.models import Set, Card

INPUT_FILE = "all-cards.json"


def seed_sets(db: Session, sets_data: list[dict]) -> int:
    count = 0
    for set_data in sets_data:
        existing = db.query(Set).filter(Set.code == set_data["code"]).first()
        if existing:
            continue
        set_obj = Set(
            name=set_data["name"],
            code=set_data["code"],
            language=set_data.get("language", "English"),
            series=set_data.get("series"),
            release_date=set_data.get("release_date"),
            total_cards=set_data.get("total_cards", 0),
        )
        db.add(set_obj)
        count += 1
    db.commit()
    return count


def seed_cards(db: Session, cards_data: list[dict]) -> int:
    count = 0
    for card_data in cards_data:
        set_code = card_data.get("set_code")
        set_obj = db.query(Set).filter(Set.code == set_code).first()
        if not set_obj:
            continue
        existing = (
            db.query(Card)
            .filter(Card.set_id == set_obj.id, Card.number == card_data["number"])
            .first()
        )
        if existing:
            continue
        card_obj = Card(
            set_id=set_obj.id,
            name=card_data["name"],
            number=card_data["number"],
            language=card_data.get("language", "English"),
            rarity=card_data.get("rarity"),
            variant=card_data.get("variant"),
        )
        db.add(card_obj)
        count += 1
    db.commit()
    return count


def main():
    input_file = sys.argv[1] if len(sys.argv) > 1 else INPUT_FILE

    with open(input_file, "r", encoding="utf-8") as f:
        all_cards = json.load(f)

    if not isinstance(all_cards, list):
        print("Formato inesperado: se esperaba una lista de cartas")
        sys.exit(1)

    print(f"Procesando {len(all_cards)} cartas...")

    sets_data = {}
    for card in all_cards:
        set_info = card.get("set")
        if isinstance(set_info, dict):
            code = set_info.get("id") or set_info.get("code")
            name = set_info.get("name")
            if code and code not in sets_data:
                sets_data[code] = {
                    "name": name or code,
                    "code": code,
                    "language": "English",
                    "series": set_info.get("series"),
                    "release_date": set_info.get("releaseDate"),
                    "total_cards": set_info.get("printedTotal") or set_info.get("total") or 0,
                }

    cards_list = []
    for card in all_cards:
        set_info = card.get("set")
        set_code = None
        if isinstance(set_info, dict):
            set_code = set_info.get("id") or set_info.get("code")
        cards_list.append({
            "name": card.get("name", ""),
            "number": card.get("number", ""),
            "set_code": set_code,
            "language": "English",
            "rarity": card.get("rarity"),
            "variant": None,
        })

    db: Session = SessionLocal()
    try:
        sets_count = seed_sets(db, list(sets_data.values()))
        cards_count = seed_cards(db, cards_list)
        print(f"Importado: {sets_count} sets, {cards_count} cartas")
    finally:
        db.close()


if __name__ == "__main__":
    main()