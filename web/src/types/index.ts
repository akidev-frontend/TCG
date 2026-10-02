export interface SetInfo {
  id: number;
  name: string;
  code: string;
  language: string;
  series: string | null;
  total_cards: number;
}

export interface CardInfo {
  id: number;
  set_id: number;
  name: string;
  number: string;
  language: string;
  rarity: string | null;
  variant: string | null;
}

export interface CollectionEntry {
  id: number;
  card_id: number;
  quantity: number;
  condition: string | null;
  purchase_price: number | null;
  purchase_date: string | null;
  notes: string | null;
  created_at: string;
  updated_at: string;
}

export interface SetProgress {
  set_id: number;
  set_name: string;
  set_code: string;
  total_cards: number;
  owned_cards: number;
  missing_cards: number;
  progress_percentage: number;
}

export interface ScanResponse {
  success: boolean;
  card_id: number | null;
  name: string;
  set_name: string;
  set_code: string;
  card_number: string;
  rarity: string | null;
  variant: string | null;
  message: string | null;
}