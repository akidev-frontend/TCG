import { NextRequest, NextResponse } from "next/server";

const API_BASE = process.env.NEXT_PUBLIC_API_URL || "https://tcg.akidojo.dev";

export async function POST(req: NextRequest) {
  const formData = await req.formData();
  const res = await fetch(`${API_BASE}/scan`, {
    method: "POST",
    body: formData,
  });
  const data = await res.json();
  return NextResponse.json(data, { status: res.status });
}