import { NextRequest, NextResponse } from "next/server";

const API_BASE = process.env.NEXT_PUBLIC_API_URL || "https://tcg.akidojo.dev";

export async function GET(
  req: NextRequest,
  { params }: { params: { id: string } }
) {
  const res = await fetch(`${API_BASE}/sets/${params.id}/progress`);
  const data = await res.json();
  return NextResponse.json(data);
}