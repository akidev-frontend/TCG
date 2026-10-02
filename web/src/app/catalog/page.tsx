"use client";

import { useEffect, useState } from "react";
import { CardInfo, SetInfo } from "../types";
import Link from "next/link";

export default function CatalogPage() {
  const [cards, setCards] = useState<CardInfo[]>([]);
  const [sets, setSets] = useState<SetInfo[]>([]);
  const [search, setSearch] = useState("");
  const [setFilter, setSetFilter] = useState<number | "">("");
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    Promise.all([
      fetch("/api/cards").then((r) => r.json()),
      fetch("/api/sets").then((r) => r.json()),
    ]).then(([cardsData, setsData]) => {
      setCards(cardsData);
      setSets(setsData);
      setLoading(false);
    });
  }, []);

  const filtered = cards.filter((card) => {
    const matchesSearch =
      !search || card.name.toLowerCase().includes(search.toLowerCase());
    const matchesSet =
      setFilter === "" || card.set_id === setFilter;
    return matchesSearch && matchesSet;
  });

  if (loading) return <p>Cargando...</p>;

  return (
    <div>
      <h1>Catálogo de Cartas</h1>
      <div style={{ display: "flex", gap: "1rem", marginBottom: "1rem" }}>
        <input
          type="text"
          placeholder="Buscar carta..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          style={{ padding: "0.5rem", flex: 1 }}
        />
        <select
          value={setFilter}
          onChange={(e) =>
            setSetFilter(e.target.value ? Number(e.target.value) : "")
          }
          style={{ padding: "0.5rem" }}
        >
          <option value="">Todos los sets</option>
          {sets.map((s) => (
            <option key={s.id} value={s.id}>
              {s.name}
            </option>
          ))}
        </select>
      </div>
      <div style={{ display: "grid", gap: "0.5rem" }}>
        {filtered.map((card) => (
          <Link key={card.id} href={`/catalog/${card.id}`}>
            <a
              style={{
                padding: "0.5rem",
                border: "1px solid #e5e5e5",
                borderRadius: "4px",
                display: "flex",
                justifyContent: "space-between",
              }}
            >
              <span>{card.name}</span>
              <span>#{card.number}</span>
            </a>
          </Link>
        ))}
      </div>
      <p>{filtered.length} de {cards.length} cartas</p>
    </div>
  );
}