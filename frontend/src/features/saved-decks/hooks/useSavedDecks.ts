import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import {
  getSavedDeckSummary,
  updateSavedDeck,
  getSyncSummary,
  getSyncDetails,
  syncAllCards,
  syncPartialCards,
  getStudyQueue,
} from '../api/savedDecksApi';
import type {
  UpdateSavedDeckRequest,
  SyncCardsRequest,
  StudyQueueParams,
} from '../types';

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

/**
 * Hook to fetch the summary and study progress of a saved deck.
 *
 * @param savedDeckId - The ID of the saved deck.
 * @returns React Query object containing the saved deck detail.
 */
export const useSavedDeckSummary = (savedDeckId: number) => {
  return useQuery({
    queryKey: savedDeckKeys.detail(savedDeckId),
    queryFn: () => getSavedDeckSummary(savedDeckId),
    enabled: !!savedDeckId,
  });
};

/**
 * Hook to update the custom name and/or description of a saved deck.
 * Invalidates the saved deck detail cache upon success.
 *
 * @returns Mutation object for updating a saved deck.
 */
export const useUpdateSavedDeck = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({
      savedDeckId,
      data,
    }: {
      savedDeckId: number;
      data: UpdateSavedDeckRequest;
    }) => updateSavedDeck(savedDeckId, data),
    onSuccess: (_, { savedDeckId }) => {
      queryClient.invalidateQueries({ queryKey: savedDeckKeys.detail(savedDeckId) });
    },
  });
};

/**
 * Hook to check the synchronization status of a saved deck.
 * Returns counts of new, updated, and deleted cards.
 *
 * @param savedDeckId - The ID of the saved deck.
 * @returns React Query object containing the sync summary.
 */
export const useSyncSummary = (savedDeckId: number) => {
  return useQuery({
    queryKey: savedDeckKeys.syncSummary(savedDeckId),
    queryFn: () => getSyncSummary(savedDeckId),
    enabled: !!savedDeckId,
  });
};

/**
 * Hook to fetch a paginated list of card diffs that are out of sync.
 *
 * @param savedDeckId - The ID of the saved deck.
 * @param params - Pagination parameters (page, limit).
 * @returns React Query object containing the paginated card diffs.
 */
export const useSyncDetails = (
  savedDeckId: number,
  params?: { page?: number; limit?: number }
) => {
  return useQuery({
    queryKey: savedDeckKeys.syncDetails(savedDeckId, params),
    queryFn: () => getSyncDetails(savedDeckId, params),
    enabled: !!savedDeckId,
  });
};

/**
 * Hook to synchronize all out-of-sync cards in a saved deck.
 * Invalidates the sync summary, sync details, detail, and study queue caches upon success.
 *
 * @returns Mutation object for full sync.
 */
export const useSyncAllCards = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: syncAllCards,
    onSuccess: (_, savedDeckId) => {
      queryClient.invalidateQueries({ queryKey: savedDeckKeys.detail(savedDeckId) });
      queryClient.invalidateQueries({ queryKey: savedDeckKeys.syncSummary(savedDeckId) });
      queryClient.invalidateQueries({ queryKey: savedDeckKeys.syncDetails(savedDeckId) });
      queryClient.invalidateQueries({ queryKey: savedDeckKeys.studyQueue(savedDeckId) });
    },
  });
};

/**
 * Hook to synchronize a specific list of cards in a saved deck.
 * Invalidates the sync summary, sync details, detail, and study queue caches upon success.
 *
 * @returns Mutation object for partial sync.
 */
export const useSyncPartialCards = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({
      savedDeckId,
      data,
    }: {
      savedDeckId: number;
      data: SyncCardsRequest;
    }) => syncPartialCards(savedDeckId, data),
    onSuccess: (_, { savedDeckId }) => {
      queryClient.invalidateQueries({ queryKey: savedDeckKeys.detail(savedDeckId) });
      queryClient.invalidateQueries({ queryKey: savedDeckKeys.syncSummary(savedDeckId) });
      queryClient.invalidateQueries({ queryKey: savedDeckKeys.syncDetails(savedDeckId) });
      queryClient.invalidateQueries({ queryKey: savedDeckKeys.studyQueue(savedDeckId) });
    },
  });
};

/**
 * Hook to fetch a batch of cards for study or review.
 *
 * @param savedDeckId - The ID of the saved deck.
 * @param params - Query parameters (limit, type: 'new' | 'review').
 * @returns React Query object containing the study queue cards.
 */
export const useStudyQueue = (savedDeckId: number, params?: StudyQueueParams) => {
  return useQuery({
    queryKey: savedDeckKeys.studyQueue(savedDeckId, params),
    queryFn: () => getStudyQueue(savedDeckId, params),
    enabled: !!savedDeckId,
  });
};
