import Layout from "../components/Layout";
import Link from "next/link";

export default function HomePage() {
  return (
    <Layout>
      <h1>TCG Portfolio</h1>
      <p>Gestiona tu colección de cartas Pokémon.</p>
      <div style={{ display: "grid", gap: "1rem", marginTop: "2rem" }}>
        <Link href="/dashboard">
          <a style={{ padding: "1rem", border: "1px solid #ccc", borderRadius: "8px" }}>
            📊 Dashboard
          </a>
        </Link>
        <Link href="/collection">
          <a style={{ padding: "1rem", border: "1px solid #ccc", borderRadius: "8px" }}>
            📦 Mi Colección
          </a>
        </Link>
        <Link href="/catalog">
          <a style={{ padding: "1rem", border: "1px solid #ccc", borderRadius: "8px" }}>
            🃏 Catálogo
          </a>
        </Link>
        <Link href="/scan">
          <a style={{ padding: "1rem", border: "1px solid #ccc", borderRadius: "8px" }}>
            📸 Escanear Carta
          </a>
        </Link>
      </div>
    </Layout>
  );
}