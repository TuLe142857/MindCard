import type { DeckQueryRequest } from '../types';
import type { ApiPaginatedQuery } from '@/shared/types/api';

/**
 * Factory for React Query keys related to decks.
 * Centralizes key generation for consistent caching and invalidation.
 */
export const deckKeys = {
  all: ['decks'] as const,
  lists: () => [...deckKeys.all, 'list'] as const,
  list: (filters: DeckQueryRequest) => [...deckKeys.lists(), filters] as const,
  details: () => [...deckKeys.all, 'detail'] as const,
  detail: (id: number) => [...deckKeys.details(), id] as const,
  cards: (id: number, filters?: ApiPaginatedQuery) =>
    [...deckKeys.detail(id), 'cards', filters] as const,
};
