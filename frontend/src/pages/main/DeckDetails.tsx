import React from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Layers, Star, Download, Globe, Lock, ArrowLeft, Clock, User } from 'lucide-react';
import { Button } from '@/shared/components/ui/Button';
import type { DeckSummary } from '@/features/decks/types';
import { useAppSelector } from '@/store/hooks';

// Dummy data to simulate fetching a deck by ID
const MOCK_DECK: DeckSummary = {
  id: 1,
  name: 'Advanced TypeScript Patterns',
  owner: 'JohnDoe',
  topic: 'Programming',
  description: 'Learn advanced type manipulation, generics, and utility types in TS. This deck covers everything from mapped types to conditional types, helping you write more robust and type-safe code in your React and Node applications.',
  visibility: 'PUBLIC',
  totalCard: 42,
  savedCount: 128,
  ratingCount: 15,
  avgRating: 4.8,
  createdAt: new Date().toISOString(),
  isSaved: false,
};

export const DeckDetails: React.FC = () => {
  const { deckId } = useParams<{ deckId: string }>();
  const navigate = useNavigate();
  const { user } = useAppSelector((state) => state.auth);
  
  // In a real app, use react-query to fetch deck details based on deckId
  const deck = MOCK_DECK;
  const isOwner = user?.username === deck.owner;

  return (
    <div className="flex flex-col gap-6 max-w-4xl mx-auto h-full">
      {/* Back Button */}
      <div>
        <button 
          onClick={() => navigate(-1)}
          className="flex items-center gap-2 text-slate-400 hover:text-slate-200 transition-colors text-sm font-medium"
        >
          <ArrowLeft size={16} />
          Back
        </button>
      </div>

      {/* Main Deck Info */}
      <div className="bg-slate-900 border border-slate-800 rounded-2xl p-6 md:p-8 relative overflow-hidden">
        {/* Background Decoration */}
        <div className="absolute top-0 right-0 w-64 h-64 bg-blue-500/5 rounded-full blur-3xl -translate-y-1/2 translate-x-1/2 pointer-events-none" />

        <div className="flex flex-col md:flex-row md:items-start justify-between gap-6 relative z-10">
          <div className="flex-1">
            <div className="flex items-center gap-3 mb-4">
              <span className="inline-flex items-center px-3 py-1 rounded-full text-xs font-semibold bg-blue-500/10 text-blue-400 border border-blue-500/20">
                {deck.topic}
              </span>
              <div className="flex items-center gap-1.5 text-slate-500 text-sm">
                {deck.visibility === 'PUBLIC' ? (
                  <><Globe size={16} /> Public</>
                ) : (
                  <><Lock size={16} /> Private</>
                )}
              </div>
            </div>

            <h1 className="text-3xl md:text-4xl font-bold text-slate-100 mb-4 leading-tight">
              {deck.name}
            </h1>

            <p className="text-slate-400 leading-relaxed mb-6 max-w-2xl">
              {deck.description || 'No description provided.'}
            </p>

            <div className="flex flex-wrap items-center gap-6 text-sm text-slate-500">
              <div className="flex items-center gap-2">
                <div className="w-8 h-8 rounded-full bg-slate-800 flex items-center justify-center text-slate-300 font-medium">
                  {deck.owner.charAt(0).toUpperCase()}
                </div>
                <div className="flex flex-col">
                  <span className="text-xs text-slate-600">Created by</span>
                  <span className="font-medium text-slate-300">{deck.owner}</span>
                </div>
              </div>
              <div className="flex items-center gap-2">
                <Clock size={16} />
                <span>{new Date(deck.createdAt).toLocaleDateString()}</span>
              </div>
            </div>
          </div>

          {/* Stats & Actions */}
          <div className="flex flex-col gap-4 min-w-[200px]">
            <div className="grid grid-cols-2 gap-3 p-4 bg-slate-950 rounded-xl border border-slate-800/50">
              <div className="flex flex-col items-center justify-center p-2">
                <Layers size={20} className="text-blue-400 mb-1" />
                <span className="text-xl font-bold text-slate-200">{deck.totalCard}</span>
                <span className="text-xs text-slate-500">Cards</span>
              </div>
              <div className="flex flex-col items-center justify-center p-2 border-l border-slate-800/50">
                <Download size={20} className="text-green-400 mb-1" />
                <span className="text-xl font-bold text-slate-200">{deck.savedCount}</span>
                <span className="text-xs text-slate-500">Saves</span>
              </div>
              <div className="col-span-2 flex flex-col items-center justify-center p-3 mt-1 bg-slate-900 rounded-lg">
                <div className="flex items-center gap-1.5 mb-1">
                  <Star size={18} className="fill-yellow-500 text-yellow-500" />
                  <span className="text-xl font-bold text-slate-200">{deck.avgRating > 0 ? deck.avgRating.toFixed(1) : 'New'}</span>
                </div>
                <span className="text-xs text-slate-500">{deck.ratingCount} Ratings</span>
              </div>
            </div>

            {/* Action Buttons based on context */}
            {isOwner ? (
              <Button className="w-full">
                Edit Deck
              </Button>
            ) : deck.isSaved ? (
              <Button className="w-full">
                Study Now
              </Button>
            ) : (
              <Button className="w-full bg-blue-600 hover:bg-blue-700 text-white shadow-[0_0_15px_rgba(37,99,235,0.3)]">
                Save to Library
              </Button>
            )}
          </div>
        </div>
      </div>

      {/* Additional Info / Restrictions */}
      {!isOwner && (
        <div className="bg-slate-900/50 border border-slate-800/50 rounded-xl p-6 text-center">
          <Layers size={32} className="mx-auto text-slate-600 mb-3" />
          <h3 className="text-lg font-medium text-slate-300 mb-2">Card Previews are hidden</h3>
          <p className="text-sm text-slate-500 max-w-md mx-auto">
            You must save this deck to your library before you can view and study its cards. Save the deck to get started!
          </p>
        </div>
      )}
      
      {isOwner && (
        <div className="bg-slate-900 border border-slate-800 rounded-xl p-6">
           <div className="flex items-center justify-between mb-6">
             <h3 className="text-lg font-bold text-slate-200">Cards Preview</h3>
             <Button variant="outline" size="sm">Manage Cards</Button>
           </div>
           <div className="text-center py-8 text-slate-500 text-sm border-2 border-dashed border-slate-800 rounded-lg">
             Card list will be displayed here for the owner.
           </div>
        </div>
      )}
    </div>
  );
};
