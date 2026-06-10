import { apiClient } from '@/shared/api/apiClient';
import type { ApiSuccessResponse } from '@/shared/types/api';
import type { Topic } from '../types';

/**
 * Fetches all topics supported by the system.
 *
 * @returns A promise resolving to the list of all topics.
 */
export const getTopics = async (): Promise<Topic[]> => {
  const response = await apiClient.get<ApiSuccessResponse<Topic[]>>('/topics');
  return response.data.data;
};
