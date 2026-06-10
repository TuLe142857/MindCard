import { apiClient } from '@/shared/api/apiClient';
import type {
  ApiSuccessResponse,
  ApiPaginatedResponse,
  ApiPaginatedQuery,
} from '@/shared/types/api';
import type {
  DeckSummary,
  DeckDetail,
  DeckQueryRequest,
  DeckCreateRequest,
  DeckUpdateRequest,
  UpdateDeckVisibilityRequest,
  DeckRatingRequest,
  Card,
  CardCreateRequest,
} from '../types';

/**
 * Searches for public decks based on keywords and topic.
 *
 * @param params - Pagination and filtering parameters.
 * @returns A promise resolving to a paginated list of DeckSummary.
 */
export const searchDecks = async (
  params?: DeckQueryRequest
): Promise<ApiPaginatedResponse<DeckSummary>> => {
  const response = await apiClient.get<ApiPaginatedResponse<DeckSummary>>('/decks', { params });
  return response.data;
};

/**
 * Creates a new deck.
 *
 * @param data - The deck creation payload.
 * @returns A promise that resolves when the deck is created.
 */
export const createDeck = async (data: DeckCreateRequest): Promise<void> => {
  await apiClient.post('/decks', data);
};

/**
 * Fetches detailed information of a specific deck.
 *
 * @param deckId - The ID of the deck to fetch.
 * @returns A promise resolving to the DeckDetail.
 */
export const getDeckDetails = async (deckId: number): Promise<DeckDetail> => {
  const response = await apiClient.get<ApiSuccessResponse<DeckDetail>>(`/decks/${deckId}`);
  return response.data.data;
};

/**
 * Updates an existing deck's information.
 *
 * @param deckId - The ID of the deck to update.
 * @param data - The partial deck data to update.
 * @returns A promise that resolves when the deck is updated.
 */
export const updateDeck = async (deckId: number, data: DeckUpdateRequest): Promise<void> => {
  await apiClient.patch(`/decks/${deckId}`, data);
};

/**
 * Deletes a specific deck.
 *
 * @param deckId - The ID of the deck to delete.
 * @returns A promise that resolves when the deck is deleted.
 */
export const deleteDeck = async (deckId: number): Promise<void> => {
  await apiClient.delete(`/decks/${deckId}`);
};

/**
 * Saves a public deck to the user's library.
 *
 * @param deckId - The ID of the deck to save.
 * @returns A promise that resolves when the deck is saved.
 */
export const saveDeck = async (deckId: number): Promise<void> => {
  await apiClient.post(`/decks/${deckId}/save`);
};

/**
 * Rates a public deck.
 *
 * @param deckId - The ID of the deck to rate.
 * @param data - The rating payload (1-5 stars).
 * @returns A promise that resolves when the rating is submitted.
 */
export const rateDeck = async (deckId: number, data: DeckRatingRequest): Promise<void> => {
  await apiClient.post(`/decks/${deckId}/rating`, data);
};

/**
 * Updates the visibility status of a deck (Public/Private).
 *
 * @param deckId - The ID of the deck to update.
 * @param data - The visibility payload.
 * @returns A promise that resolves when visibility is updated.
 */
export const updateDeckVisibility = async (
  deckId: number,
  data: UpdateDeckVisibilityRequest
): Promise<void> => {
  await apiClient.patch(`/decks/${deckId}/visibility`, data);
};

/**
 * Fetches a paginated list of cards within a deck.
 *
 * @param deckId - The ID of the deck.
 * @param params - Pagination parameters.
 * @returns A promise resolving to a paginated list of Cards.
 */
export const getDeckCards = async (
  deckId: number,
  params?: Omit<ApiPaginatedQuery, 'sortBy'>
): Promise<ApiPaginatedResponse<Card>> => {
  const response = await apiClient.get<ApiPaginatedResponse<Card>>(`/decks/${deckId}/cards`, {
    params,
  });
  return response.data;
};

/**
 * Adds multiple cards to a deck simultaneously via multipart form-data.
 *
 * @param deckId - The ID of the deck to add cards to.
 * @param cards - Array of card creation requests containing text and media files.
 * @returns A promise that resolves when cards are successfully added.
 */
export const batchAddCards = async (deckId: number, cards: CardCreateRequest[]): Promise<void> => {
  const formData = new FormData();
  cards.forEach((card, index) => {
    formData.append(`cards[${index}].type`, card.type);
    if (card.frontText) formData.append(`cards[${index}].frontText`, card.frontText);
    if (card.backText) formData.append(`cards[${index}].backText`, card.backText);
    if (card.frontImage) formData.append(`cards[${index}].frontImage`, card.frontImage);
    if (card.frontAudio) formData.append(`cards[${index}].frontAudio`, card.frontAudio);
    if (card.backImage) formData.append(`cards[${index}].backImage`, card.backImage);
    if (card.backAudio) formData.append(`cards[${index}].backAudio`, card.backAudio);
  });

  await apiClient.post(`/decks/${deckId}/card/batch`, formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
};
