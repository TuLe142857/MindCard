import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Search, Filter, Loader2 } from 'lucide-react';
import { DeckCard } from '@/features/decks/components/DeckCard';
import { useSearchDecks } from '@/features/decks/hooks/useDecks';

import Pagination from '@/shared/components/ui/Pagination';
import { usePagination } from '@/shared/hooks/usePagination';

export const Explore: React.FC = () => {
  const [searchTerm, setSearchTerm] = useState('');
  const [debouncedTerm, setDebouncedTerm] = useState('');
  const navigate = useNavigate();

  const { page, limit, setPage, nextPage, previousPage } = usePagination({
    initialPage: 1,
    initialLimit: 5,
  });

  // Debounce search input
  useEffect(() => {
    const timer = setTimeout(() => setDebouncedTerm(searchTerm), 500);
    return () => clearTimeout(timer);
  }, [searchTerm]);

  // Reset page when search term changes
  useEffect(() => {
    setPage(1);
  }, [debouncedTerm, setPage]);

  // Fetch decks using React Query hook
  const { data, isLoading, error } = useSearchDecks({
    keyword: debouncedTerm || undefined,
    page,
    limit,
  });

  const decks = data?.data || [];
  const totalPages = data?.meta?.totalPages || 0;

  return (
    <div className="flex flex-col h-full gap-6">
      {/* Header & Filters */}
      <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4 bg-slate-900 p-6 rounded-2xl border border-slate-800">
        <div>
          <h1 className="text-2xl font-bold text-slate-100 mb-1">Explore Decks</h1>
          <p className="text-sm text-slate-400">Discover public decks created by the community.</p>
        </div>

        <div className="flex items-center gap-3 w-full md:w-auto">
          <div className="relative flex-1 md:w-64">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-500" size={18} />
            <input
              type="text"
              placeholder="Search decks..."
              className="w-full bg-slate-950 border border-slate-800 rounded-lg pl-10 pr-4 py-2.5 text-sm text-slate-200 placeholder:text-slate-500 focus:outline-none focus:ring-2 focus:ring-blue-500/50 focus:border-blue-500 transition-all"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
          </div>

          <button className="flex items-center gap-2 px-4 py-2.5 bg-slate-800 hover:bg-slate-700 text-slate-300 rounded-lg text-sm font-medium transition-colors border border-slate-700">
            <Filter size={16} />
            <span className="hidden sm:inline">Filters</span>
          </button>
        </div>
      </div>

      {/* Main Content Area */}
      {isLoading ? (
        <div className="flex items-center justify-center flex-1 min-h-[300px]">
          <Loader2 className="animate-spin text-blue-500" size={32} />
        </div>
      ) : error ? (
        <div className="flex flex-col items-center justify-center flex-1 min-h-[300px] text-slate-400">
          <p className="mb-2 text-red-400">Failed to load public decks.</p>
          <p className="text-sm">Please try again later.</p>
        </div>
      ) : decks.length === 0 ? (
        <div className="flex flex-col items-center justify-center flex-1 min-h-[300px] text-slate-400">
          <div className="w-16 h-16 mb-4 rounded-full bg-slate-800/50 flex items-center justify-center">
            <Search size={24} className="text-slate-500" />
          </div>
          <p className="font-medium text-slate-300">No decks found</p>
          <p className="text-sm mt-1">Try adjusting your search filters.</p>
        </div>
      ) : (
        <div className="flex flex-col gap-8">
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
            {decks.map((deck) => (
              <DeckCard key={deck.id} deck={deck} onClick={() => navigate(`/deck/${deck.id}`)} />
            ))}
          </div>

          <Pagination
            currentPage={page}
            totalPages={totalPages}
            onNextPage={nextPage}
            onPreviousPage={previousPage}
            onSetPage={setPage}
          />
        </div>
      )}
    </div>
  );
};
