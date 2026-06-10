import type { StudyQueueParams } from '../types';

/**
 * Factory for React Query keys related to saved decks.
 * Centralizes key generation for consistent caching and invalidation.
 */
export const savedDeckKeys = {
  all: ['saved-decks'] as const,
  detail: (id: number) => [...savedDeckKeys.all, id] as const,
  syncSummary: (id: number) => [...savedDeckKeys.detail(id), 'sync-summary'] as const,
  syncDetails: (id: number, params?: { page?: number; limit?: number }) =>
    [...savedDeckKeys.detail(id), 'sync-details', params] as const,
  studyQueue: (id: number, params?: StudyQueueParams) =>
    [...savedDeckKeys.detail(id), 'study-queue', params] as const,
};
