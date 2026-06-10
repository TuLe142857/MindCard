import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Search, Filter } from 'lucide-react';
import { DeckCard } from '@/features/decks/components/DeckCard';
import type { DeckSummary } from '@/features/decks/types';

const MOCK_DECKS: DeckSummary[] = [
  {
    id: 1,
    name: 'Advanced TypeScript Patterns',
    owner: 'JohnDoe',
    topic: 'Programming',
    description: 'Learn advanced type manipulation, generics, and utility types in TS.',
    visibility: 'PUBLIC',
    totalCard: 42,
    savedCount: 128,
    ratingCount: 15,
    avgRating: 4.8,
    createdAt: new Date().toISOString(),
  },
  {
    id: 2,
    name: 'JLPT N3 Vocabulary',
    owner: 'SakuraSan',
    topic: 'Languages',
    description: 'Essential vocabulary for passing the JLPT N3 exam.',
    visibility: 'PUBLIC',
    totalCard: 850,
    savedCount: 342,
    ratingCount: 89,
    avgRating: 4.9,
    createdAt: new Date().toISOString(),
  },
  {
    id: 3,
    name: 'React Query Fundamentals',
    owner: 'DevMaster',
    topic: 'Programming',
    description: 'Master server state management in React applications.',
    visibility: 'PUBLIC',
    totalCard: 25,
    savedCount: 56,
    ratingCount: 8,
    avgRating: 4.5,
    createdAt: new Date().toISOString(),
  },
  {
    id: 4,
    name: 'World History Trivia',
    owner: 'HistoryBuff',
    topic: 'History',
    description: 'Fun facts and important dates from world history.',
    visibility: 'PUBLIC',
    totalCard: 100,
    savedCount: 12,
    ratingCount: 2,
    avgRating: 5.0,
    createdAt: new Date().toISOString(),
  },
  {
    id: 5,
    name: 'Basic Anatomy',
    owner: 'MedStudent99',
    topic: 'Science',
    description: 'Learn the major bones and muscles of the human body.',
    visibility: 'PUBLIC',
    totalCard: 65,
    savedCount: 89,
    ratingCount: 12,
    avgRating: 4.2,
    createdAt: new Date().toISOString(),
  },
  {
    id: 6,
    name: 'AWS Solutions Architect',
    owner: 'CloudGuru',
    topic: 'Technology',
    description: 'Flashcards for the AWS SAA-C03 certification exam.',
    visibility: 'PUBLIC',
    totalCard: 300,
    savedCount: 500,
    ratingCount: 120,
    avgRating: 4.7,
    createdAt: new Date().toISOString(),
  }
];

export const Explore: React.FC = () => {
  const [searchTerm, setSearchTerm] = useState('');
  const navigate = useNavigate();
  
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

      {/* Grid of Decks */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
        {MOCK_DECKS.map((deck) => (
          <DeckCard 
            key={deck.id} 
            deck={deck} 
            onClick={() => navigate(`/deck/${deck.id}`)}
          />
        ))}
      </div>
    </div>
  );
};
