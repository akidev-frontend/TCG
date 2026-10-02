import axios from "axios";

const API_BASE = process.env.NEXT_PUBLIC_API_URL || "https://tcg.akidojo.dev";

const api = axios.create({
  baseURL: API_BASE,
});

export async function getHealth() {
  const { data } = await api.get("/health");
  return data;
}

export async function getSets() {
  const { data } = await api.get("/sets");
  return data;
}

export async function getSet(id: number) {
  const { data } = await api.get(`/sets/${id}`);
  return data;
}

export async function getSetProgress(id: number) {
  const { data } = await api.get(`/sets/${id}/progress`);
  return data;
}

export async function getCards(params?: { set_id?: number; name?: string }) {
  const { data } = await api.get("/cards", { params });
  return data;
}

export async function getCard(id: number) {
  const { data } = await api.get(`/cards/${id}`);
  return data;
}

export async function getCollection() {
  const { data } = await api.get("/collection");
  return data;
}

export async function addToCollection(data: {
  card_id: number;
  quantity?: number;
  condition?: string;
  purchase_price?: number;
  purchase_date?: string;
  notes?: string;
}) {
  const { data: result } = await api.post("/collection", data);
  return result;
}

export async function updateCollection(
  id: number,
  data: Partial<{
    quantity: number;
    condition: string;
    purchase_price: number;
    purchase_date: string;
    notes: string;
  }>
) {
  const { data: result } = await api.put(`/collection/${id}`, data);
  return result;
}

export async function deleteFromCollection(id: number) {
  await api.delete(`/collection/${id}`);
}

export async function scanCard(imageFile: File) {
  const formData = new FormData();
  formData.append("image", imageFile);
  const { data } = await api.post("/scan", formData, {
    headers: { "Content-Type": "multipart/form-data" },
  });
  return data;
}