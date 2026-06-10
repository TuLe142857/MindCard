import { apiClient } from '@/shared/api/apiClient';
import type { ApiSuccessResponse, ApiPaginatedResponse } from '@/shared/types/api';
import type { User } from '../types';
import type { UserPublicProfile } from '../types';
import type { DeckSummary, DeckQueryRequest } from '@/features/decks';
import type { SavedDeckSummary, SavedDeckQueryRequest } from '@/features/saved-decks';

/**
 * Fetches the private profile of the currently authenticated user.
 *
 * @returns A promise resolving to the User object (includes email).
 */
export const getSelfProfile = async (): Promise<User> => {
  const response = await apiClient.get<ApiSuccessResponse<User>>('/users/me');
  return response.data.data;
};

/**
 * Updates the user's avatar.
 *
 * @param file - The image file to upload as the new avatar.
 * @returns A promise resolving to the new avatar URL string.
 */
export const updateAvatar = async (file: File): Promise<string> => {
  const formData = new FormData();
  formData.append('file', file);

  const response = await apiClient.patch<ApiSuccessResponse<string>>('/users/me/avatar', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
  return response.data.data;
};

/**
 * Fetches the list of decks created by the current user.
 *
 * @param params - Pagination and filtering parameters.
 * @returns A promise resolving to a paginated response of DeckSummary.
 */
export const getSelfDecks = async (
  params?: DeckQueryRequest
): Promise<ApiPaginatedResponse<DeckSummary>> => {
  const response = await apiClient.get<ApiPaginatedResponse<DeckSummary>>('/users/me/decks', {
    params,
  });
  return response.data;
};

/**
 * Fetches the list of decks saved by the current user for studying.
 *
 * @param params - Pagination and filtering parameters.
 * @returns A promise resolving to a paginated response of SavedDeckSummary.
 */
export const getSavedDecks = async (
  params?: SavedDeckQueryRequest
): Promise<ApiPaginatedResponse<SavedDeckSummary>> => {
  const response = await apiClient.get<ApiPaginatedResponse<SavedDeckSummary>>(
    '/users/me/saved-decks',
    { params }
  );
  return response.data;
};

/**
 * Fetches the public profile of a user by their username.
 *
 * @param username - The username of the target user.
 * @returns A promise resolving to the UserPublicProfile object.
 */
export const getUserProfile = async (username: string): Promise<UserPublicProfile> => {
  const response = await apiClient.get<ApiSuccessResponse<UserPublicProfile>>(`/users/${username}`);
  return response.data.data;
};

/**
 * Fetches the list of public decks created by a specific user.
 *
 * @param username - The username of the target user.
 * @param params - Pagination and filtering parameters.
 * @returns A promise resolving to a paginated response of DeckSummary.
 */
export const getUserDecks = async (
  username: string,
  params?: DeckQueryRequest
): Promise<ApiPaginatedResponse<DeckSummary>> => {
  const response = await apiClient.get<ApiPaginatedResponse<DeckSummary>>(
    `/users/${username}/decks`,
    { params }
  );
  return response.data;
};
