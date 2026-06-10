/**
 * Factory for React Query keys related to cards.
 * Centralizes key generation for consistent caching and invalidation.
 */
export const cardKeys = {
  all: ['cards'] as const,
  detail: (id: number) => [...cardKeys.all, id] as const,
};
