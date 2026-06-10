import { useQuery } from '@tanstack/react-query';
import { getTopics } from '../api/topicsApi';

/**
 * Factory for React Query keys related to topics.
 */
export const topicKeys = {
  all: ['topics'] as const,
};

/**
 * Hook to fetch all topics supported by the system.
 * Data is considered mostly static, so staleTime is set to 10 minutes.
 *
 * @returns React Query object containing the list of topics.
 */
export const useTopics = () => {
  return useQuery({
    queryKey: topicKeys.all,
    queryFn: getTopics,
    staleTime: 10 * 60 * 1000,
  });
};
