import type { SavedDeckSummary } from '@/features/users/types';

/**
 * Re-export SavedDeckSummary from users feature.
 * This type is used both in user profile pages and saved deck detail pages.
 */
export type { SavedDeckSummary };

/**
 * Detailed response of a Saved Deck (same structure as SavedDeckSummary).
 */
export type SavedDeckDetail = SavedDeckSummary;

/**
 * Request payload for updating a saved deck's custom name and description.
 */
export interface UpdateSavedDeckRequest {
  /** New name for the saved deck (1-255 characters) */
  name?: string;
  /** New description for the saved deck */
  description?: string;
}

/**
 * Synchronization summary showing counts of out-of-sync cards.
 */
export interface DeckSyncSummary {
  /** ID of the original deck */
  deckId: number;
  /** Number of new cards added by creator that are not yet in user's progress */
  totalNewCards: number;
  /** Number of cards updated by creator with newer versions */
  totalUpdatedCards: number;
  /** Number of cards deleted by creator but still in user's progress */
  totalDeletedCards: number;
}

/**
 * Represents a before/after diff for a single field.
 */
export interface FieldDiff {
  /** Current value on user's side, null if empty */
  current: string | null;
  /** Upcoming value from creator, null if empty */
  upcoming: string | null;
}

/**
 * Type of change detected for a card during sync.
 * - `NEW`: Card was added by creator and doesn't exist in user's progress
 * - `DELETED`: Card was deleted by creator but still exists in user's progress
 * - `UPDATED`: Card content was modified by creator
 */
export type CardChangeType = 'NEW' | 'DELETED' | 'UPDATED';

/**
 * Detailed diff for a single card between the user's pinned version
 * and the creator's latest version.
 */
export interface CardDiff {
  /** ID of the card */
  cardId: number;
  /** Type of change detected */
  changeType: CardChangeType;
  /** Current version number (null if change type is NEW) */
  currentVersion: number;
  /** Upcoming version number (null if change type is DELETED) */
  upcomingVersion: number;
  /** Diff for card type */
  type: FieldDiff | null;
  /** Diff for front side text */
  frontText: FieldDiff | null;
  /** Diff for front side image URL */
  frontImage: FieldDiff | null;
  /** Diff for front side audio URL */
  frontAudio: FieldDiff | null;
  /** Diff for back side text */
  backText: FieldDiff | null;
  /** Diff for back side image URL */
  backImage: FieldDiff | null;
  /** Diff for back side audio URL */
  backAudio: FieldDiff | null;
}

/**
 * Request payload for syncing specific cards in a saved deck.
 */
export interface SyncCardsRequest {
  /** List of card IDs to sync */
  cardIds: number[];
}

/**
 * Type of cards to fetch for the study queue.
 * - `new`: Fetch new cards that haven't been studied yet
 * - `review`: Fetch due cards that need to be reviewed
 */
export type StudyQueueType = 'new' | 'review';

/**
 * Query parameters for fetching the study queue.
 */
export interface StudyQueueParams {
  /** Maximum number of cards to fetch */
  limit?: number;
  /** Type of cards to fetch */
  type?: StudyQueueType;
}
