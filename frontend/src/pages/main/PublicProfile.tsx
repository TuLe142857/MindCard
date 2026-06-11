import React from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { User as UserIcon, Calendar, BookOpen } from 'lucide-react';
import { DeckCard } from '@/features/decks/components/DeckCard';
import type { DeckSummary } from '@/features/decks/types';

const MOCK_PUBLIC_DECKS: DeckSummary[] = [
  {
    id: 1,
    name: 'Advanced TypeScript Patterns',
    owner: 'JohnDoe', // Will be matched with the URL username
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
    id: 3,
    name: 'React Query Fundamentals',
    owner: 'JohnDoe',
    topic: 'Programming',
    description: 'Master server state management in React applications.',
    visibility: 'PUBLIC',
    totalCard: 25,
    savedCount: 56,
    ratingCount: 8,
    avgRating: 4.5,
    createdAt: new Date().toISOString(),
  }
];

export const PublicProfile: React.FC = () => {
  const { username } = useParams<{ username: string }>();
  const navigate = useNavigate();

  // In a real app, we would fetch user details and their public decks by username.
  const displayUsername = username || 'Unknown User';
  const userDecks = MOCK_PUBLIC_DECKS.map(deck => ({ ...deck, owner: displayUsername }));

  return (
    <div className="flex flex-col gap-8 max-w-6xl mx-auto h-full pb-12">
      {/* Profile Header */}
      <div className="bg-slate-900 border border-slate-800 rounded-3xl p-8 flex flex-col md:flex-row items-center md:items-start gap-6 relative overflow-hidden">
        {/* Background decorative element */}
        <div className="absolute top-0 right-0 w-64 h-64 bg-blue-500/5 rounded-full blur-3xl -translate-y-1/2 translate-x-1/2" />
        
        <div className="w-28 h-28 rounded-full bg-slate-950 border-4 border-slate-800 flex items-center justify-center shrink-0 z-10 shadow-xl">
          <UserIcon size={40} className="text-slate-600" />
        </div>
        
        <div className="flex flex-col items-center md:items-start text-center md:text-left z-10 flex-1">
          <h1 className="text-3xl font-bold text-slate-100">{displayUsername}</h1>
          <div className="flex flex-wrap items-center justify-center md:justify-start gap-4 mt-4 text-sm text-slate-400">
            <div className="flex items-center gap-1.5 bg-slate-950/50 px-3 py-1.5 rounded-full border border-slate-800/50">
              <BookOpen size={14} className="text-blue-400" />
              <span className="font-medium text-slate-300">{userDecks.length}</span> Public Decks
            </div>
            <div className="flex items-center gap-1.5 bg-slate-950/50 px-3 py-1.5 rounded-full border border-slate-800/50">
              <Calendar size={14} className="text-purple-400" />
              <span>Joined recently</span>
            </div>
          </div>
        </div>
      </div>

      {/* Public Decks List */}
      <div className="flex flex-col gap-6">
        <div className="flex items-center justify-between border-b border-slate-800 pb-2">
          <h2 className="text-xl font-bold text-slate-200">Public Decks</h2>
        </div>
        
        {userDecks.length > 0 ? (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
            {userDecks.map((deck) => (
              <DeckCard 
                key={deck.id} 
                deck={deck} 
                onClick={() => navigate(`/deck/${deck.id}`)}
              />
            ))}
          </div>
        ) : (
          <div className="flex flex-col items-center justify-center py-20 px-4 border border-dashed border-slate-800 rounded-2xl bg-slate-900/50 text-center">
            <div className="w-16 h-16 rounded-full bg-slate-800 flex items-center justify-center mb-4">
              <BookOpen size={24} className="text-slate-500" />
            </div>
            <h3 className="text-lg font-medium text-slate-300 mb-1">No public decks</h3>
            <p className="text-slate-500 max-w-sm">
              {displayUsername} hasn't published any decks yet.
            </p>
          </div>
        )}
      </div>
    </div>
  );
};
