import Navbar from "./Navbar";

export default function Layout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <>
      <Navbar />
      <main style={{ padding: "2rem", maxWidth: "1200px", margin: "0 auto" }}>
        {children}
      </main>
    </>
  );
}