import { useState, useCallback } from 'react';

export interface UsePaginationOptions {
  initialPage?: number;
  initialLimit?: number;
}

/**
 * A shared hook to manage pagination state (page, limit) and provide
 * ready-to-use queryParams for API requests, along with handler
 * functions compatible with the `<Pagination />` component.
 */
export const usePagination = (options: UsePaginationOptions = {}) => {
  const { initialPage = 1, initialLimit = 10 } = options;

  const [page, setPage] = useState(initialPage);
  const [limit, setLimit] = useState(initialLimit);

  const nextPage = useCallback(() => setPage((prev) => prev + 1), []);
  const previousPage = useCallback(() => setPage((prev) => Math.max(prev - 1, 1)), []);

  const resetPagination = useCallback(() => {
    setPage(initialPage);
    setLimit(initialLimit);
  }, [initialPage, initialLimit]);

  return {
    page,
    limit,
    setPage,
    setLimit,
    nextPage,
    previousPage,
    resetPagination,
  };
};
