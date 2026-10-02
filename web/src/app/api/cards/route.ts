import { NextRequest, NextResponse } from "next/server";

const API_BASE = process.env.NEXT_PUBLIC_API_URL || "https://tcg.akidojo.dev";

export async function GET(req: NextRequest) {
  const searchParams = req.nextUrl.searchParams;
  const setId = searchParams.get("set_id");
  const name = searchParams.get("name");

  const params = new URLSearchParams();
  if (setId) params.set("set_id", setId);
  if (name) params.set("name", name);

  const res = await fetch(`${API_BASE}/cards?${params.toString()}`);
  const data = await res.json();
  return NextResponse.json(data);
}