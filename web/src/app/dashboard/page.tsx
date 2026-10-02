"use client";

import { useEffect, useState } from "react";
import { SetProgress, SetInfo } from "../types";
import Link from "next/link";

export default function DashboardPage() {
  const [progress, setProgress] = useState<SetProgress[]>([]);
  const [sets, setsData] = useState<SetInfo[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    Promise.all([
      fetch("/api/sets").then((r) => r.json()),
      fetch("/api/sets").then((r) => r.json()),
    ]).then(([setsData, progressData]) => {
      setsData(setsData);
      setProgress(progressData);
      setLoading(false);
    });
  }, []);

  if (loading) return <p>Cargando...</p>;

  return (
    <div>
      <h1>Dashboard</h1>
      <h2>Progreso de Sets</h2>
      <div style={{ display: "grid", gap: "1rem" }}>
        {progress.map((p) => (
          <div
            key={p.set_id}
            style={{
              padding: "1rem",
              border: "1px solid #e5e5e5",
              borderRadius: "8px",
            }}
          >
            <Link href={`/set-progress/${p.set_id}`}>
              <a style={{ fontWeight: "bold" }}>{p.setName}</a>
            </Link>
            <p>
              {p.ownedCards}/{p.totalCards} cartas ({p.progressPercentage}%)
            </p>
            <div
              style={{
                height: "8px",
                background: "#e5e5e5",
                borderRadius: "4px",
                overflow: "hidden",
              }}
            >
              <div
                style={{
                  height: "100%",
                  width: `${p.progressPercentage}%`,
                  background: "#03DAC6",
                }}
              />
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}