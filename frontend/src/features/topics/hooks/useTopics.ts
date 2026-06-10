import { useQuery } from '@tanstack/react-query';
import { getTopics } from '../api/topicsApi';

import { topicKeys } from './queryKeys';

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
