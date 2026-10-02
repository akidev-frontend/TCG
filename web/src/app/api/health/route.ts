import { NextRequest, NextResponse } from "next/server";

const API_BASE = process.env.NEXT_PUBLIC_API_URL || "https://tcg.akidojo.dev";

export async function GET() {
  const res = await fetch(`${API_BASE}/health`);
  const data = await res.json();
  return NextResponse.json(data);
}