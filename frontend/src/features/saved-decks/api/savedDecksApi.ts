import { apiClient } from '@/shared/api/apiClient';
import type { ApiSuccessResponse, ApiPaginatedResponse } from '@/shared/types/api';
import type { Card } from '@/features/decks/types';
import type {
  SavedDeckDetail,
  UpdateSavedDeckRequest,
  DeckSyncSummary,
  CardDiff,
  SyncCardsRequest,
  StudyQueueParams,
} from '../types';

/**
 * Fetches the summary and study progress of a saved deck.
 *
 * @param savedDeckId - The ID of the saved deck.
 * @returns A promise resolving to the SavedDeckDetail.
 */
export const getSavedDeckSummary = async (savedDeckId: number): Promise<SavedDeckDetail> => {
  const response = await apiClient.get<ApiSuccessResponse<SavedDeckDetail>>(
    `/saved-decks/${savedDeckId}`
  );
  return response.data.data;
};

/**
 * Updates the custom name and/or description of a saved deck.
 *
 * @param savedDeckId - The ID of the saved deck.
 * @param data - The update payload.
 * @returns A promise resolving to the updated SavedDeckDetail.
 */
export const updateSavedDeck = async (
  savedDeckId: number,
  data: UpdateSavedDeckRequest
): Promise<SavedDeckDetail> => {
  const response = await apiClient.patch<ApiSuccessResponse<SavedDeckDetail>>(
    `/saved-decks/${savedDeckId}`,
    data
  );
  return response.data.data;
};

/**
 * Checks the synchronization status of a saved deck.
 * Returns counts of new, updated, and deleted cards compared to the original.
 *
 * @param savedDeckId - The ID of the saved deck.
 * @returns A promise resolving to the DeckSyncSummary.
 */
export const getSyncSummary = async (savedDeckId: number): Promise<DeckSyncSummary> => {
  const response = await apiClient.get<ApiSuccessResponse<DeckSyncSummary>>(
    `/saved-decks/${savedDeckId}/sync-summary`
  );
  return response.data.data;
};

/**
 * Fetches a paginated list of card diffs that are out of sync.
 *
 * @param savedDeckId - The ID of the saved deck.
 * @param params - Pagination parameters (page, limit).
 * @returns A promise resolving to a paginated list of CardDiff.
 */
export const getSyncDetails = async (
  savedDeckId: number,
  params?: { page?: number; limit?: number }
): Promise<ApiPaginatedResponse<CardDiff>> => {
  const response = await apiClient.get<ApiPaginatedResponse<CardDiff>>(
    `/saved-decks/${savedDeckId}/sync-details`,
    { params }
  );
  return response.data;
};

/**
 * Synchronizes all out-of-sync cards in the saved deck with the latest versions.
 *
 * @param savedDeckId - The ID of the saved deck.
 * @returns A promise that resolves when sync is complete.
 */
export const syncAllCards = async (savedDeckId: number): Promise<void> => {
  await apiClient.post(`/saved-decks/${savedDeckId}/sync`);
};

/**
 * Synchronizes a specific list of cards in the saved deck.
 *
 * @param savedDeckId - The ID of the saved deck.
 * @param data - The payload containing the list of card IDs to sync.
 * @returns A promise that resolves when sync is complete.
 */
export const syncPartialCards = async (
  savedDeckId: number,
  data: SyncCardsRequest
): Promise<void> => {
  await apiClient.post(`/saved-decks/${savedDeckId}/sync/partial`, data);
};

/**
 * Fetches a batch of cards for study or review from a saved deck.
 *
 * @param savedDeckId - The ID of the saved deck.
 * @param params - Query parameters (limit, type: 'new' | 'review').
 * @returns A promise resolving to a list of Cards in the study queue.
 */
export const getStudyQueue = async (
  savedDeckId: number,
  params?: StudyQueueParams
): Promise<Card[]> => {
  const response = await apiClient.get<ApiSuccessResponse<Card[]>>(
    `/saved-decks/${savedDeckId}/cards/batch`,
    { params }
  );
  return response.data.data;
};
