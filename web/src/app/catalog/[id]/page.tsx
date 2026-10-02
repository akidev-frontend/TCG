"use client";

import { useEffect, useState } from "react";
import { CardInfo } from "../types";
import Link from "next/link";

export default function CardDetailPage({
  params,
}: {
  params: { id: string };
}) {
  const [card, setCard] = useState<CardInfo | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetch(`/api/cards?id=${params.id}`)
      .then((r) => r.json())
      .then(setCard)
      .finally(() => setLoading(false));
  }, [params.id]);

  if (loading) return <p>Cargando...</p>;
  if (!card) return <p>Carta no encontrada</p>;

  return (
    <div>
      <h1>{card.name}</h1>
      <p>Número: #{card.number}</p>
      <p>Set ID: {card.set_id}</p>
      <p>Lenguaje: {card.language}</p>
      <p>Rareza: {card.rarity || "N/A"}</p>
      {card.variant && <p>Variante: {card.variant}</p>}
      <Link href="/collection">
        <a>
          <button>Añadir a colección</button>
        </a>
      </Link>
      <Link href="/catalog">
        <a>Volver al catálogo</a>
      </Link>
    </div>
  );
}