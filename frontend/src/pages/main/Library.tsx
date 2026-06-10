import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Search, Flame } from 'lucide-react';
import { SavedDeckCard } from '@/features/saved-decks/components/SavedDeckCard';
import type { SavedDeckSummary } from '@/features/saved-decks/types';

const MOCK_SAVED_DECKS: SavedDeckSummary[] = [
  {
    id: 1,
    originalDeckId: 10,
    originalDeckName: 'JLPT N3 Vocabulary',
    name: 'JLPT N3 Vocabulary',
    creator: 'Sensei',
    topic: 'Languages',
    description: 'Essential N3 vocabulary.',
    totalCards: 1200,
    newCards: 20,
    learningCards: 5,
    reviewCards: 12,
    dueCards: 37,
    hasUpdate: false,
    isOriginalDeckActive: true,
  },
  {
    id: 2,
    originalDeckId: 42,
    originalDeckName: 'AWS Solutions Architect',
    name: 'AWS Solutions Architect',
    creator: 'CloudGuru',
    topic: 'Technology',
    description: 'Exam prep questions.',
    totalCards: 300,
    newCards: 0,
    learningCards: 0,
    reviewCards: 45,
    dueCards: 45,
    hasUpdate: true,
    isOriginalDeckActive: true,
  },
  {
    id: 3,
    originalDeckId: 55,
    originalDeckName: 'European Capitals',
    name: 'European Capitals',
    creator: 'GeoMaster',
    topic: 'Geography',
    description: 'Capitals of Europe.',
    totalCards: 44,
    newCards: 0,
    learningCards: 0,
    reviewCards: 0,
    dueCards: 0,
    hasUpdate: false,
    isOriginalDeckActive: true,
  }
];

export const Library: React.FC = () => {
  const [searchTerm, setSearchTerm] = useState('');
  const navigate = useNavigate();

  // Tính toán tổng số thẻ cần học hôm nay
  const totalCardsToStudyToday = MOCK_SAVED_DECKS.reduce(
    (total, deck) => total + deck.newCards + deck.learningCards + deck.reviewCards,
    0
  );

  return (
    <div className="flex flex-col h-full gap-6">
      {/* Header & Stats Banner */}
      <div className="flex flex-col md:flex-row justify-between items-start md:items-stretch gap-4">
        <div className="flex-1 bg-slate-900 p-6 rounded-2xl border border-slate-800 flex flex-col justify-center">
          <h1 className="text-2xl font-bold text-slate-100 mb-1">My Library</h1>
          <p className="text-sm text-slate-400">Your collection of saved decks ready to study.</p>
        </div>

        {/* Daily Goal Banner */}
        <div className="bg-gradient-to-br from-blue-900/50 to-slate-900 p-6 rounded-2xl border border-blue-800/30 flex items-center gap-5 md:min-w-[300px]">
          <div className="w-14 h-14 rounded-full bg-blue-500/20 flex items-center justify-center border border-blue-500/30 shrink-0 shadow-[0_0_15px_rgba(37,99,235,0.2)]">
            <Flame className="text-blue-400" size={28} />
          </div>
          <div>
            <h3 className="text-sm font-medium text-blue-200 mb-1">Daily Review Goal</h3>
            <div className="flex items-baseline gap-2">
              <span className="text-3xl font-bold text-white">{totalCardsToStudyToday}</span>
              <span className="text-sm text-blue-300">cards due</span>
            </div>
          </div>
        </div>
      </div>

      {/* Toolbar */}
      <div className="flex items-center gap-3">
        <div className="relative flex-1 max-w-sm">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-500" size={18} />
          <input 
            type="text"
            placeholder="Search in library..."
            className="w-full bg-slate-950 border border-slate-800 rounded-lg pl-10 pr-4 py-2.5 text-sm text-slate-200 placeholder:text-slate-500 focus:outline-none focus:ring-2 focus:ring-blue-500/50 focus:border-blue-500 transition-all"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
        </div>
      </div>

      {/* Grid of Saved Decks */}
      {MOCK_SAVED_DECKS.length > 0 ? (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
          {MOCK_SAVED_DECKS.map((deck) => (
            <SavedDeckCard 
              key={deck.id} 
              deck={deck} 
              onClickDetails={() => navigate(`/deck/${deck.originalDeckId}`)}
              onStudyNew={() => navigate(`/study/${deck.id}?type=new`)}
              onStudyReview={() => navigate(`/study/${deck.id}?type=review`)}
              onSyncUpdate={() => navigate(`/sync/${deck.id}`)}
            />
          ))}
        </div>
      ) : (
        <div className="flex flex-col items-center justify-center p-12 bg-slate-900 border border-slate-800 border-dashed rounded-2xl flex-1">
          <div className="w-16 h-16 bg-slate-800 rounded-full flex items-center justify-center mb-4">
            <Search size={24} className="text-slate-400" />
          </div>
          <h3 className="text-lg font-medium text-slate-200 mb-2">Your library is empty</h3>
          <p className="text-sm text-slate-500 mb-6 text-center max-w-sm">
            Go to the Explore page to find and save decks created by the community.
          </p>
          <button 
            onClick={() => navigate('/explore')}
            className="px-6 py-2.5 bg-blue-600 hover:bg-blue-700 text-white font-medium rounded-lg transition-colors shadow-[0_0_15px_rgba(37,99,235,0.3)]"
          >
            Explore Decks
          </button>
        </div>
      )}
    </div>
  );
};
