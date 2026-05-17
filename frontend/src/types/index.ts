// src/types/index.ts

export interface User {
  id: number;
  username: string;
  email: string;
  hashed_password: string;
  avatar?: string;
}

export interface Topic {
  id: number;
  name: string;
  icon: string;
  color: string; // tailwind gradient class
}

export type DeckVisibility = 'PRIVATE' | 'PUBLIC';

export interface Deck {
  id: number;
  owner_id: number;
  topic_id: number;
  name: string;
  visibility: DeckVisibility;
  is_deleted: boolean;
  description?: string;
  card_count: number;
  created_at: string;
}

export interface SavedDeck {
  user_id: number;
  deck_id: number;
  saved_at: string;
}

export interface DeckRating {
  deck_id: number;
  user_id: number;
  stars: number; // 1–5
}

export type CardType = 'BASIC' | 'TYPE';

export interface Card {
  id: number;
  deck_id: number;
  card_type: CardType;
  front_text?: string;
  front_image_uri?: string;
  front_audio_uri?: string;
  back_text?: string;
  back_image_uri?: string;
  back_audio_uri?: string;
}

export type CardStatus = 'NEW' | 'LEARNING' | 'REVIEW';

export interface UserCardProgress {
  user_id: number;
  card_id: number;
  next_due: string;
  interval: number;
  ease_factor: number;
  status: CardStatus;
}

export interface UserDeckSettings {
  user_id: number;
  deck_id: number;
  daily_review_cards: number;
}

// Computed / enriched types for UI
export interface DeckWithMeta extends Deck {
  topic: Topic;
  owner: User;
  avg_rating: number;
  rating_count: number;
  is_saved: boolean;
}
