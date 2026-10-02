#!/usr/bin/env python3
"""
Importa all-cards.json a PostgreSQL.
Crea tablas sets y cards, elimina datos previos.

Uso: python import_catalog.py
"""

import json
import sys
from sqlalchemy.orm import Session
from app.database import engine, SessionLocal, Base
from app.models import Set, Card

INPUT_FILE = "all-cards.json"


def main():
    input_file = sys.argv[1] if len(sys.argv) > 1 else INPUT_FILE

    print(f"Leyendo {input_file}...")
    with open(input_file, "r", encoding="utf-8") as f:
        all_cards = json.load(f)

    if not isinstance(all_cards, list):
        print("Error: se esperaba una lista de cartas")
        sys.exit(1)

    print(f"  {len(all_cards)} cartas encontradas")

    # Build sets
    sets_map = {}
    for card in all_cards:
        set_info = card.get("set")
        if not isinstance(set_info, dict):
            continue
        code = set_info.get("id") or set_info.get("code")
        if not code:
            continue
        if code not in sets_map:
            sets_map[code] = {
                "name": set_info.get("name", code),
                "code": code,
                "language": "English",
                "series": set_info.get("series"),
                "release_date": set_info.get("releaseDate"),
                "total_cards": set_info.get("printedTotal") or set_info.get("total") or 0,
            }

    sets_list = list(sets_map.values())
    print(f"  {len(sets_list)} sets únicos")

    db: Session = SessionLocal()
    try:
        # Clear existing data
        db.query(Card).delete()
        db.query(Set).delete()
        db.commit()
        print("  Datos previos eliminados")

        # Insert sets
        for set_data in sets_list:
            set_obj = Set(**set_data)
            db.add(set_obj)
        db.commit()
        print(f"  {len(sets_list)} sets importados")

        # Insert cards
        card_count = 0
        for card in all_cards:
            set_info = card.get("set")
            if not isinstance(set_info, dict):
                continue
            set_code = set_info.get("id") or set_info.get("code")
            set_obj = db.query(Set).filter(Set.code == set_code).first()
            if not set_obj:
                continue
            card_obj = Card(
                set_id=set_obj.id,
                name=card.get("name", ""),
                number=card.get("number", ""),
                language="English",
                rarity=card.get("rarity"),
                variant=None,
            )
            db.add(card_obj)
            card_count += 1
            if card_count % 5000 == 0:
                db.commit()
                print(f"  {card_count} cartas importadas...")

        db.commit()
        print(f"  {card_count} cartas importadas")
        print("\n¡Importación completada!")

    except Exception as e:
        db.rollback()
        print(f"Error: {e}")
        raise
    finally:
        db.close()


if __name__ == "__main__":
    main()