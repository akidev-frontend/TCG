#!/usr/bin/env python3
"""
Descarga el catálogo completo de cartas Pokémon desde la API pokemontcg.io
y lo guarda como catalogo.json en la carpeta del backend.

Requiere: Python 3 con urllib (sin dependencias externas)
Uso: python download_catalog.py
"""

import json
import urllib.request
import time
import sys
import os

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


def main():
    print("Descargando catálogo de cartas Pokémon...")

    print("  Descargando sets...")
    sets_data = download_json(f"{BASE_URL}/sets?pageSize=250")
    if not sets_data:
        print("  Error descargando sets. Abortando.")
        sys.exit(1)
    sets = sets_data.get("data", [])
    print(f"  {len(sets)} sets descargados")

    print("  Descargando cartas...")
    all_cards = []
    page = 1
    while True:
        data = download_json(f"{BASE_URL}/cards?pageSize=250&page={page}")
        if not data:
            print(f"  Error en página {page}. Deteniendo.")
            break
        items = data.get("data", [])
        if not items:
            break
        all_cards.extend(items)
        print(f"    Página {page}: {len(items)} cartas (total: {len(all_cards)})")
        if len(items) < 250:
            break
        page += 1
        time.sleep(1)

    catalog = {"sets": sets, "cards": all_cards}

    with open(OUTPUT_FILE, "w", encoding="utf-8") as f:
        json.dump(catalog, f, ensure_ascii=False)

    print(f"\nGuardado en: {OUTPUT_FILE}")
    print(f"  Sets: {len(sets)}")
    print(f"  Cartas: {len(all_cards)}")


if __name__ == "__main__":
    main()