import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import {
  getSelfProfile,
  updateAvatar,
  getSelfDecks,
  getSavedDecks,
  getUserProfile,
  getUserDecks,
} from '../api/usersApi';
import type { DeckQueryRequest } from '@/features/decks';
import type { SavedDeckQueryRequest } from '@/features/saved-decks';
import { userKeys } from './queryKeys';

/**
 * Hook to fetch the current user's private profile.
 *
 * @returns React Query object containing the user profile data.
 */
export const useGetSelfProfile = () => {
  return useQuery({
    queryKey: userKeys.me(),
    queryFn: getSelfProfile,
  });
};

/**
 * Hook to update the current user's avatar.
 *
 * @returns React Query mutation object for updating the avatar.
 */
export const useUpdateAvatar = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (file: File) => updateAvatar(file),
    onSuccess: () => {
      // Invalidate both 'me' profile and the globally cached user data if needed
      queryClient.invalidateQueries({ queryKey: userKeys.me() });
    },
  });
};

/**
 * Hook to fetch the decks created by the current user.
 *
 * @param params - Optional query parameters for pagination and filtering.
 * @returns React Query object containing paginated deck summaries.
 */
export const useGetSelfDecks = (params?: DeckQueryRequest) => {
  return useQuery({
    queryKey: userKeys.myDecks(params),
    queryFn: () => getSelfDecks(params),
  });
};

/**
 * Hook to fetch the decks saved by the current user.
 *
 * @param params - Optional query parameters for pagination and filtering.
 * @returns React Query object containing paginated saved deck summaries.
 */
export const useGetSavedDecks = (params?: SavedDeckQueryRequest) => {
  return useQuery({
    queryKey: userKeys.mySavedDecks(params),
    queryFn: () => getSavedDecks(params),
  });
};

/**
 * Hook to fetch the public profile of a user by username.
 *
 * @param username - The username to lookup.
 * @returns React Query object containing the public profile data.
 */
export const useGetUserProfile = (username: string) => {
  return useQuery({
    queryKey: userKeys.profile(username),
    queryFn: () => getUserProfile(username),
    enabled: !!username,
  });
};

/**
 * Hook to fetch the public decks of a user by username.
 *
 * @param username - The username whose decks to fetch.
 * @param params - Optional query parameters for pagination and filtering.
 * @returns React Query object containing paginated deck summaries.
 */
export const useGetUserDecks = (username: string, params?: DeckQueryRequest) => {
  return useQuery({
    queryKey: userKeys.userDecks(username, params),
    queryFn: () => getUserDecks(username, params),
    enabled: !!username,
  });
};
