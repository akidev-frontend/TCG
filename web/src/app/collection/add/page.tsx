"use client";

import { useState } from "react";
import { addToCollection } from "../../lib/api";
import Link from "next/link";

export default function AddCollectionEntryPage({
  params,
}: {
  params: { id: string };
}) {
  const [quantity, setQuantity] = useState("1");
  const [condition, setCondition] = useState("");
  const [purchasePrice, setPurchasePrice] = useState("");
  const [purchaseDate, setPurchaseDate] = useState("");
  const [notes, setNotes] = useState("");

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    await addToCollection({
      card_id: Number(params.id),
      quantity: Number(quantity),
      condition: condition || null,
      purchase_price: purchasePrice ? Number(purchasePrice) : null,
      purchase_date: purchaseDate || null,
      notes: notes || null,
    });
    window.location.href = "/collection";
  };

  return (
    <div>
      <h1>Añadir a colección</h1>
      <form onSubmit={handleSubmit}>
        <div style={{ marginBottom: "1rem" }}>
          <label>Cantidad</label>
          <input
            type="number"
            value={quantity}
            onChange={(e) => setQuantity(e.target.value)}
            min={1}
          />
        </div>
        <div style={{ marginBottom: "1rem" }}>
          <label>Condición</label>
          <input
            value={condition}
            onChange={(e) => setCondition(e.target.value)}
            placeholder="Near Mint, Lightly Played..."
          />
        </div>
        <div style={{ marginBottom: "1rem" }}>
          <label>Precio de compra</label>
          <input
            type="number"
            step="0.01"
            value={purchasePrice}
            onChange={(e) => setPurchasePrice(e.target.value)}
          />
        </div>
        <div style={{ marginBottom: "1rem" }}>
          <label>Fecha de compra</label>
          <input
            type="date"
            value={purchaseDate}
            onChange={(e) => setPurchaseDate(e.target.value)}
          />
        </div>
        <div style={{ marginBottom: "1rem" }}>
          <label>Notas</label>
          <textarea
            value={notes}
            onChange={(e) => setNotes(e.target.value)}
          />
        </div>
        <button type="submit">Guardar</button>
      </form>
      <Link href="/collection">
        <a>Volver a colección</a>
      </Link>
    </div>
  );
}