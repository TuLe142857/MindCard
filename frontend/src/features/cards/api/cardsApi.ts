import { apiClient } from '@/shared/api/apiClient';
import type { CardUpdateRequest, CardReviewRequest, CardMediaSlot } from '../types';

/**
 * Updates the text content and/or type of a card.
 *
 * @param cardId - The ID of the card to update.
 * @param data - The partial card data to update (type, frontText, backText).
 * @returns A promise that resolves when the card is updated.
 */
export const updateCard = async (cardId: number, data: CardUpdateRequest): Promise<void> => {
  await apiClient.patch(`/cards/${cardId}`, data);
};

/**
 * Soft deletes a card from its deck.
 * Only the deck owner can perform this action.
 *
 * @param cardId - The ID of the card to delete.
 * @returns A promise that resolves when the card is deleted.
 */
export const deleteCard = async (cardId: number): Promise<void> => {
  await apiClient.delete(`/cards/${cardId}`);
};

/**
 * Submits a study review score (0-5) for a card.
 * The backend uses the SuperMemo-2 algorithm to calculate the next review date.
 *
 * @param cardId - The ID of the card being reviewed.
 * @param data - The review payload containing the quality score.
 * @returns A promise that resolves when the review is recorded.
 */
export const reviewCard = async (cardId: number, data: CardReviewRequest): Promise<void> => {
  await apiClient.post(`/cards/${cardId}/review`, data);
};

/**
 * Updates a specific media file (image or audio) on a card.
 * Pass an empty file to delete the current media.
 *
 * @param cardId - The ID of the card.
 * @param slot - The media slot to update ('front-image', 'front-audio', 'back-image', 'back-audio').
 * @param file - The media file to upload.
 * @returns A promise that resolves when the media is updated.
 */
export const updateCardMedia = async (
  cardId: number,
  slot: CardMediaSlot,
  file: File
): Promise<void> => {
  const formData = new FormData();
  formData.append('file', file);

  await apiClient.post(`/cards/${cardId}/${slot}`, formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
};
