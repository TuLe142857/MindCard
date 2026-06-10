import type { ApiPaginatedQuery } from '@/shared/types/api';
import type { User } from '@/features/auth/types';

/**
 * Public profile of a user (does not contain sensitive info like email).
 */
export type UserPublicProfile = Omit<User, 'email'>;

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
}

/**
 * Summary information of a Saved Deck.
 */
export interface SavedDeckSummary {
  /** Unique ID of the saved deck record */
  id: number;
  /** ID of the original public deck */
  originalDeckId: number;
  /** Original name of the deck */
  originalDeckName: string;
  /** Custom name set by the learner, or original name if not set */
  name: string;
  /** Username of the deck creator */
  creator: string;
  /** Topic name of the deck */
  topic: string;
  /** Custom description set by the learner, or original if not set */
  description: string;
  /** Total number of flashcards in the deck */
  totalCards: number;
  /** Number of new cards to learn */
  newCards: number;
  /** Number of cards currently in learning phase */
  learningCards: number;
  /** Number of cards that need to be reviewed */
  reviewCards: number;
  /** Total number of due cards (new + learning + review) */
  dueCards: number;
  /** Whether the original deck has been updated since last sync */
  hasUpdate: boolean;
  /** Whether the original deck is still active/public */
  isOriginalDeckActive: boolean;
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
 * Query parameters for fetching saved decks.
 */
export interface SavedDeckQueryRequest extends ApiPaginatedQuery {
  /** Filter by custom name */
  keyword?: string;
}
