import type { ApiPaginatedQuery } from '@/shared/types/api';

/**
 * Request payload for creating a new Deck.
 */
export interface DeckCreateRequest {
  /** Name of the deck */
  name: string;
  /** Topic ID the deck belongs to */
  topicId: number;
  /** Optional description for the deck */
  description?: string;
  /** Visibility status of the deck */
  visibility: 'PUBLIC' | 'PRIVATE';
}

/**
 * Request payload for updating an existing Deck.
 */
export interface DeckUpdateRequest {
  /** Updated name of the deck */
  name?: string;
  /** Updated topic ID */
  topicId?: number;
  /** Updated description */
  description?: string;
}

/**
 * Request payload for updating the visibility of a Deck.
 */
export interface UpdateDeckVisibilityRequest {
  /** New visibility status */
  visibility: 'PUBLIC' | 'PRIVATE';
}

/**
 * Request payload for rating a Deck.
 */
export interface DeckRatingRequest {
  /** Rating value from 1 to 5 */
  rating: number;
}

/**
 * Request payload for creating a single Card.
 * Includes optional files for image and audio.
 */
export interface CardCreateRequest {
  /** Type of the card */
  type: 'BASIC' | 'TYPE';
  /** Text for the front of the card */
  frontText?: string;
  /** Text for the back of the card */
  backText?: string;
  /** Image file for the front of the card */
  frontImage?: File;
  /** Audio file for the front of the card */
  frontAudio?: File;
  /** Image file for the back of the card */
  backImage?: File;
  /** Audio file for the back of the card */
  backAudio?: File;
}

/**
 * Represents one side (front or back) of a Flashcard.
 */
export interface CardSide {
  /** Text content */
  text?: string;
  /** URL to the image */
  imageUrl?: string;
  /** URL to the audio file */
  audioUrl?: string;
}

/**
 * Represents a Flashcard in a Deck.
 */
export interface Card {
  /** Unique ID of the card */
  id: number;
  /** Type of the card (e.g., 'BASIC') */
  type: string;
  /** Content on the front of the card */
  front: CardSide;
  /** Content on the back of the card */
  back: CardSide;
}

/**
 * Summary information of a Deck.
 */
export interface DeckSummary {
  /** Unique ID of the deck */
  id: number;
  /** Name of the deck */
  name: string;
  /** owner's username*/
  owner: string;
  /** topic name */
  topic: string;
  /** Short description */
  description?: string | null;
  /** Visibility status */
  visibility: 'PUBLIC' | 'PRIVATE';
  /** Total number of flashcards */
  totalCard: number;
  savedCount: number;
  ratingCount: number;
  avgRating: number;
  /** Creation timestamp */
  createdAt: string;
  isSaved?: boolean;
  userRating?: number | null;
}

/**
 * Query parameters for fetching decks.
 */
export interface DeckQueryRequest extends ApiPaginatedQuery {
  /** Filter by keyword in title/description */
  keyword?: string;
  /** Filter by a specific topic */
  topicId?: number;
}

/**
 * Detailed information of a Deck.
 * Currently maps directly to DeckSummary.
 */
export type DeckDetail = DeckSummary;
