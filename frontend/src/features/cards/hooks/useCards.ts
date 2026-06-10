import { useMutation, useQueryClient } from '@tanstack/react-query';
import { updateCard, deleteCard, reviewCard, updateCardMedia } from '../api/cardsApi';
import { deckKeys } from '@/features/decks/hooks/useDecks';
import type { CardUpdateRequest, CardReviewRequest, CardMediaSlot } from '../types';

/**
 * Factory for React Query keys related to cards.
 * Centralizes key generation for consistent caching and invalidation.
 */
export const cardKeys = {
  all: ['cards'] as const,
  detail: (id: number) => [...cardKeys.all, id] as const,
};

/**
 * Hook to update a card's text content and/or type.
 * Invalidates the parent deck's cards list upon success.
 *
 * @param deckId - The ID of the deck containing the card (used for cache invalidation).
 * @returns Mutation object for updating a card.
 */
export const useUpdateCard = (deckId: number) => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ cardId, data }: { cardId: number; data: CardUpdateRequest }) =>
      updateCard(cardId, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: deckKeys.cards(deckId) });
    },
  });
};

/**
 * Hook to delete a card from a deck.
 * Invalidates the parent deck's cards list and detail upon success.
 *
 * @param deckId - The ID of the deck containing the card (used for cache invalidation).
 * @returns Mutation object for deleting a card.
 */
export const useDeleteCard = (deckId: number) => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: deleteCard,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: deckKeys.cards(deckId) });
      queryClient.invalidateQueries({ queryKey: deckKeys.detail(deckId) });
    },
  });
};

/**
 * Hook to submit a study review score for a card.
 * Uses the SuperMemo-2 algorithm on the backend to schedule next review.
 *
 * @returns Mutation object for reviewing a card.
 */
export const useReviewCard = () => {
  return useMutation({
    mutationFn: ({ cardId, data }: { cardId: number; data: CardReviewRequest }) =>
      reviewCard(cardId, data),
  });
};

/**
 * Hook to update a specific media file (image or audio) on a card.
 * Invalidates the parent deck's cards list upon success.
 *
 * @param deckId - The ID of the deck containing the card (used for cache invalidation).
 * @returns Mutation object for uploading card media.
 */
export const useUpdateCardMedia = (deckId: number) => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ cardId, slot, file }: { cardId: number; slot: CardMediaSlot; file: File }) =>
      updateCardMedia(cardId, slot, file),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: deckKeys.cards(deckId) });
    },
  });
};
