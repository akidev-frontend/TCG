#!/usr/bin/env python3
"""
Descarga el catálogo completo de cartas Pokémon desde GitHub
y lo guarda como catalogo.json en la carpeta del backend.

Uso: python download_catalog.py
"""

import json
import urllib.request
import os

ALL_CARDS_URL = "https://raw.githubusercontent.com/bascn/pokemon-tcg-data/main/all-cards.json"
OUTPUT_FILE = os.path.join(os.path.dirname(os.path.abspath(__file__)), "catalogo.json")


def main():
    print("Descargando catálogo de cartas Pokémon desde GitHub...")

    try:
        with urllib.request.urlopen(ALL_CARDS_URL, timeout=30) as resp:
            all_cards = json.loads(resp.read().decode())
    except Exception as e:
        print(f"  Error descargando: {e}")
        return

    if not isinstance(all_cards, list):
        print("  Formato inesperado")
        return

    print(f"  {len(all_cards)} cartas descargadas")

    # Build sets from cards
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

    catalog = {
        "sets": list(sets_data.values()),
        "cards": [
            {
                "name": card.get("name", ""),
                "number": card.get("number", ""),
                "set_code": (card.get("set").get("id") or card.get("set").get("code"))
                if isinstance(card.get("set"), dict)
                else None,
                "language": "English",
                "rarity": card.get("rarity"),
                "variant": None,
            }
            for card in all_cards
        ],
    }

    with open(OUTPUT_FILE, "w", encoding="utf-8") as f:
        json.dump(catalog, f, ensure_ascii=False)

    print(f"\nGuardado en: {OUTPUT_FILE}")
    print(f"  Sets: {len(catalog['sets'])}")
    print(f"  Cartas: {len(catalog['cards'])}")


if __name__ == "__main__":
    main()