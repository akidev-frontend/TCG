"use client";

import { useEffect, useState } from "react";
import { CollectionEntry, CardInfo } from "../types";
import { deleteFromCollection, updateCollection } from "../lib/api";

export default function CollectionPage() {
  const [entries, setEntries] = useState<CollectionEntry[]>([]);
  const [cards, setCards] = useState<CardInfo[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetch("/api/collection")
      .then((r) => r.json())
      .then((data) => {
        setEntries(data);
        setLoading(false);
      });
    fetch("/api/cards").then((r) => r.json()).then(setCards);
  }, []);

  const handleDelete = (id: number) => {
    deleteFromCollection(id).then(() => {
      setEntries(entries.filter((e) => e.id !== id));
    });
  };

  if (loading) return <p>Cargando...</p>;

  return (
    <div>
      <h1>Mi Colección</h1>
      {entries.length === 0 ? (
        <p>No hay cartas en tu colección.</p>
      ) : (
        <div style={{ display: "grid", gap: "1rem" }}>
          {entries.map((entry) => (
            <div
              key={entry.id}
              style={{
                padding: "1rem",
                border: "1px solid #e5e5e5",
                borderRadius: "8px",
                display: "flex",
                justifyContent: "space-between",
                alignItems: "center",
              }}
            >
              <div>
                <strong>Carta ID: {entry.card_id}</strong>
                <p>Cantidad: {entry.quantity}</p>
                <p>Condición: {entry.condition || "N/A"}</p>
                {entry.purchase_price && (
                  <p>Precio: ${entry.purchase_price.toFixed(2)}</p>
                )}
                {entry.notes && <p>Notas: {entry.notes}</p>}
              </div>
              <div style={{ display: "flex", gap: "0.5rem" }}>
                <button
                  onClick={() =>
                    updateCollection(entry.id, { quantity: entry.quantity + 1 })
                  }
                >
                  +1
                </button>
                <button
                  onClick={() => handleDelete(entry.id)}
                  style={{ color: "red" }}
                >
                  Eliminar
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}