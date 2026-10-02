"use client";

import { useState } from "react";
import { scanCard } from "../lib/api";
import { ScanResponse } from "../types";

export default function ScanPage() {
  const [scanning, setScanning] = useState(false);
  const [result, setResult] = useState<ScanResponse | null>(null);
  const [preview, setPreview] = useState<string | null>(null);

  const handleFileChange = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;

    setPreview(URL.createObjectURL(file));
    setScanning(true);
    setResult(null);

    try {
      const data = await scanCard(file);
      setResult(data);
    } catch (err) {
      setResult({
        success: false,
        name: "",
        set_name: "",
        set_code: "",
        card_number: "",
        rarity: null,
        variant: null,
        message: "Error al escanear",
      });
    } finally {
      setScanning(false);
    }
  };

  return (
    <div>
      <h1>Escanear Carta</h1>
      <input type="file" accept="image/*" capture="environment" onChange={handleFileChange} />

      {preview && (
        <img src={preview} alt="Vista previa" style={{ maxWidth: "300px", marginTop: "1rem" }} />
      )}

      {scanning && <p>Escaneando...</p>}

      {result && (
        <div style={{ marginTop: "1rem", padding: "1rem", border: "1px solid #e5e5e5", borderRadius: "8px" }}>
          {result.success ? (
            <>
              <h2>{result.name}</h2>
              <p>Set: {result.setName}</p>
              <p>Código: {result.setCode}</p>
              <p>Número: {result.cardNumber}</p>
              <p>Rareza: {result.rarity || "N/A"}</p>
              {result.variant && <p>Variante: {result.variant}</p>}
              <button onClick={() => window.location.reload()}>Escanear otra</button>
            </>
          ) : (
            <>
              <p>No se pudo identificar la carta.</p>
              <p>{result.message}</p>
              <button onClick={() => window.location.reload()}>Reintentar</button>
            </>
          )}
        </div>
      )}
    </div>
  );
}