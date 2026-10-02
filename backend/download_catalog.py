#!/usr/bin/env python3
"""
Descarga el catálogo completo de cartas Pokémon en varios idiomas
desde la API pokemontcg.io y lo guarda como catalogo.json.

Idiomas: español (es), japonés (ja), coreano (ko), inglés (en).

Uso: python download_catalog.py
"""

import json
import urllib.request
import time
import os

LANGUAGES = ["es", "ja", "ko", "en"]
BASE_URL = "https://api.pokemontcg.io/v2"
OUTPUT_FILE = os.path.join(os.path.dirname(os.path.abspath(__file__)), "catalogo.json")


def download_json(url, timeout=20):
    for attempt in range(3):
        try:
            req = urllib.request.Request(url)
            with urllib.request.urlopen(req, timeout=timeout) as resp:
                return json.loads(resp.read().decode())
        except Exception as e:
            print(f"  Intento {attempt + 1} falló: {e}")
            if attempt < 2:
                time.sleep(3)
    return None


def download_all_cards(language):
    all_cards = []
    page = 1
    while True:
        data = download_json(f"{BASE_URL}/cards?pageSize=250&page={page}&language={language}")
        if not data:
            print(f"  Error descargando cartas en {language}. Deteniendo.")
            break
        items = data.get("data", [])
        if not items:
            break
        all_cards.extend(items)
        print(f"    {language}: página {page}, {len(items)} cartas (total: {len(all_cards)})")
        if len(items) < 250:
            break
        page += 1
        time.sleep(1)
    return all_cards


def main():
    all_sets = {}
    all_cards = []

    for lang in LANGUAGES:
        print(f"\nDescargando en {lang}...")

        sets_data = download_json(f"{BASE_URL}/sets?pageSize=250")
        if sets_data:
            for s in sets_data.get("data", []):
                code = s.get("id") or s.get("code")
                if code and code not in all_sets:
                    all_sets[code] = {
                        "name": s.get("name", ""),
                        "code": code,
                        "language": lang,
                        "series": s.get("series"),
                        "release_date": s.get("releaseDate"),
                        "total_cards": s.get("printedTotal") or s.get("total") or 0,
                    }

        cards = download_all_cards(lang)
        for card in cards:
            set_info = card.get("set")
            set_code = None
            if isinstance(set_info, dict):
                set_code = set_info.get("id") or set_info.get("code")
            all_cards.append({
                "name": card.get("name", ""),
                "number": card.get("number", ""),
                "set_code": set_code,
                "language": lang,
                "rarity": card.get("rarity"),
                "variant": None,
            })

    catalog = {"sets": list(all_sets.values()), "cards": all_cards}

    with open(OUTPUT_FILE, "w", encoding="utf-8") as f:
        json.dump(catalog, f, ensure_ascii=False)

    print(f"\nGuardado en: {OUTPUT_FILE}")
    print(f"  Sets: {len(catalog['sets'])}")
    print(f"  Cartas: {len(catalog['cards'])}")


if __name__ == "__main__":
    main()