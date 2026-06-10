import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import {
  searchDecks,
  createDeck,
  getDeckDetails,
  updateDeck,
  deleteDeck,
  saveDeck,
  rateDeck,
  updateDeckVisibility,
  getDeckCards,
  batchAddCards,
} from '../api/decksApi';

import type {
  CardCreateRequest,
  DeckQueryRequest,
  DeckRatingRequest,
  DeckUpdateRequest,
  UpdateDeckVisibilityRequest,
} from '../types';
import type { ApiPaginatedQuery } from '@/shared/types/api.ts';

import { deckKeys } from './queryKeys';

/**
 * Hook to search and fetch a paginated list of public decks.
 *
 * @param params - Search and pagination filters.
 * @returns React Query object containing deck data and loading states.
 */
export const useSearchDecks = (params: DeckQueryRequest) => {
  return useQuery({
    queryKey: deckKeys.list(params),
    queryFn: () => searchDecks(params),
  });
};

/**
 * Hook to fetch detailed information for a specific deck.
 *
 * @param deckId - The ID of the deck to fetch.
 * @returns React Query object containing deck detail data.
 */
export const useDeckDetails = (deckId: number) => {
  return useQuery({
    queryKey: deckKeys.detail(deckId),
    queryFn: () => getDeckDetails(deckId),
    enabled: !!deckId,
  });
};

/**
 * Hook to fetch a paginated list of cards inside a specific deck.
 *
 * @param deckId - The ID of the deck.
 * @param params - Pagination parameters.
 * @returns React Query object containing the paginated cards.
 */
export const useDeckCards = (deckId: number, params?: Omit<ApiPaginatedQuery, 'sortBy'>) => {
  return useQuery({
    queryKey: deckKeys.cards(deckId, params),
    queryFn: () => getDeckCards(deckId, params),
    enabled: !!deckId,
  });
};

/**
 * Hook to create a new deck.
 * Invalidates the deck list cache upon success.
 *
 * @returns Mutation object for creating a deck.
 */
export const useCreateDeck = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: createDeck,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: deckKeys.lists() });
    },
  });
};

/**
 * Hook to update an existing deck.
 * Invalidates both the specific deck detail and deck lists upon success.
 *
 * @returns Mutation object for updating a deck.
 */
export const useUpdateDeck = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ deckId, data }: { deckId: number; data: DeckUpdateRequest }) =>
      updateDeck(deckId, data),
    onSuccess: (_, { deckId }) => {
      queryClient.invalidateQueries({ queryKey: deckKeys.detail(deckId) });
      queryClient.invalidateQueries({ queryKey: deckKeys.lists() });
    },
  });
};

/**
 * Hook to delete a deck.
 * Invalidates deck lists upon success.
 *
 * @returns Mutation object for deleting a deck.
 */
export const useDeleteDeck = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: deleteDeck,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: deckKeys.lists() });
    },
  });
};

/**
 * Hook to save a public deck to the user's library.
 * Invalidates the specific deck detail to reflect updated saved stats/status.
 *
 * @returns Mutation object for saving a deck.
 */
export const useSaveDeck = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: saveDeck,
    onSuccess: (_, deckId) => {
      queryClient.invalidateQueries({ queryKey: deckKeys.detail(deckId) });
    },
  });
};

/**
 * Hook to rate a public deck.
 * Invalidates the deck detail and lists to update rating averages.
 *
 * @returns Mutation object for rating a deck.
 */
export const useRateDeck = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ deckId, data }: { deckId: number; data: DeckRatingRequest }) =>
      rateDeck(deckId, data),
    onSuccess: (_, { deckId }) => {
      queryClient.invalidateQueries({ queryKey: deckKeys.detail(deckId) });
      queryClient.invalidateQueries({ queryKey: deckKeys.lists() });
    },
  });
};

/**
 * Hook to change the visibility of a deck.
 * Invalidates the specific deck detail and lists upon success.
 *
 * @returns Mutation object for updating deck visibility.
 */
export const useUpdateDeckVisibility = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ deckId, data }: { deckId: number; data: UpdateDeckVisibilityRequest }) =>
      updateDeckVisibility(deckId, data),
    onSuccess: (_, { deckId }) => {
      queryClient.invalidateQueries({ queryKey: deckKeys.detail(deckId) });
      queryClient.invalidateQueries({ queryKey: deckKeys.lists() });
    },
  });
};

/**
 * Hook to batch add multiple cards to a deck.
 * Invalidates the cards list and deck detail (which may contain total card count) upon success.
 *
 * @returns Mutation object for batch adding cards.
 */
export const useBatchAddCards = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ deckId, cards }: { deckId: number; cards: CardCreateRequest[] }) =>
      batchAddCards(deckId, cards),
    onSuccess: (_, { deckId }) => {
      queryClient.invalidateQueries({ queryKey: deckKeys.cards(deckId) });
      queryClient.invalidateQueries({ queryKey: deckKeys.detail(deckId) });
    },
  });
};
