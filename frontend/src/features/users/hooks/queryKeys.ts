import type { DeckQueryRequest } from '@/features/decks';
import type { SavedDeckQueryRequest } from '@/features/saved-decks';

/**
 * Factory for React Query keys related to users.
 */
export const userKeys = {
  all: ['users'] as const,
  me: () => [...userKeys.all, 'me'] as const,
  myDecks: (params?: DeckQueryRequest) => [...userKeys.me(), 'decks', params] as const,
  mySavedDecks: (params?: SavedDeckQueryRequest) =>
    [...userKeys.me(), 'saved-decks', params] as const,
  profile: (username: string) => [...userKeys.all, username] as const,
  userDecks: (username: string, params?: DeckQueryRequest) =>
    [...userKeys.profile(username), 'decks', params] as const,
};
