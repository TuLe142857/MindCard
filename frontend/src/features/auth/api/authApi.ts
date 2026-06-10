import { apiClient } from '@/shared/api/apiClient';
import type { ApiSuccessResponse } from '@/shared/types/api';
import type { LoginRequest, User } from '../types';

/**
 * Authenticates the user and retrieves their profile.
 * Tokens are set automatically via http-only cookies by the server.
 *
 * @param credentials - The user's login credentials.
 * @returns A promise resolving to the authenticated User profile.
 */
export const login = async (credentials: LoginRequest): Promise<User> => {
  await apiClient.post('/auth/login', credentials);

  // Fetch the user profile explicitly after a successful login
  const meResponse = await apiClient.get<ApiSuccessResponse<User>>('/users/me');
  return meResponse.data.data;
};

/**
 * Logs out the current user and clears the server-side session.
 *
 * @returns A promise that resolves when logout is complete.
 */
export const logout = async (): Promise<void> => {
  await apiClient.post('/auth/logout');
};
