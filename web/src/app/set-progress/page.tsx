"use client";

import { useEffect, useState } from "react";
import { SetProgress } from "../types";
import Link from "next/link";

export default function SetProgressPage({
  params,
}: {
  params: { id: string };
}) {
  const [progress, setProgress] = useState<SetProgress | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetch(`/api/sets/${params.id}/progress`)
      .then((r) => r.json())
      .then((data) => {
        setProgress(data);
        setLoading(false);
      });
  }, [params.id]);

  if (loading) return <p>Cargando...</p>;
  if (!progress) return <p>Set no encontrado</p>;

  return (
    <div>
      <h1>{progress.setName}</h1>
      <p>
        {progress.ownedCards}/{progress.totalCards} cartas ({progress.progressPercentage}%)
      </p>
      <div
        style={{
          height: "12px",
          background: "#e5e5e5",
          borderRadius: "6px",
          overflow: "hidden",
        }}
      >
        <div
          style={{
            height: "100%",
            width: `${progress.progressPercentage}%`,
            background: "#03DAC6",
          }}
        />
      </div>
      <p>
        {progress.missingCards} cartas faltantes
      </p>
      <Link href="/catalog">
        <a>Volver al catálogo</a>
      </Link>
    </div>
  );
}