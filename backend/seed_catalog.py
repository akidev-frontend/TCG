#!/usr/bin/env python3
"""
Script de seed para importar el catálogo de cartas Pokémon a PostgreSQL.

Uso:
    python seed_catalog.py < archivo_catalogo.json
    python seed_catalog.py sets.json cards.json

El archivo de entrada debe contener los datos en formato JSON compatible
con las tablas sets y cards.
"""

import json
import sys
from sqlalchemy.orm import Session
from app.database import engine, SessionLocal
from app.models import Set, Card


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
            print(f"Set no encontrado para carta {card_data.get('name')}: {set_code}")
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
    if len(sys.argv) < 2:
        print("Uso: python seed_catalog.py <archivo_json>")
        sys.exit(1)

    input_file = sys.argv[1]

    with open(input_file, "r", encoding="utf-8") as f:
        data = json.load(f)

    sets_data = data.get("sets", data if isinstance(data, list) else [])
    cards_data = data.get("cards", [])

    db: Session = SessionLocal()
    try:
        sets_count = seed_sets(db, sets_data)
        cards_count = seed_cards(db, cards_data)
        print(f"Importado: {sets_count} sets, {cards_count} cartas")
    finally:
        db.close()


if __name__ == "__main__":
    main()