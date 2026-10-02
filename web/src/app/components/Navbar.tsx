import Link from "next/link";

export default function Navbar() {
  return (
    <nav style={{ padding: "1rem 2rem", borderBottom: "1px solid #e5e5e5" }}>
      <div style={{ display: "flex", gap: "2rem", alignItems: "center" }}>
        <Link href="/" style={{ fontWeight: "bold", fontSize: "1.2rem" }}>
          TCG Portfolio
        </Link>
        <Link href="/dashboard">Dashboard</Link>
        <Link href="/collection">Colección</Link>
        <Link href="/catalog">Catálogo</Link>
        <Link href="/scan">Escanear</Link>
      </div>
    </nav>
  );
}