import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Plus, Search } from 'lucide-react';
import { Button } from '@/shared/components/ui/Button';
import { DeckCard } from '@/features/decks/components/DeckCard';
import { DeckFormModal } from '@/features/decks/components/DeckFormModal';
import type { DeckSummary } from '@/features/decks/types';

// Mock private decks
const MOCK_PRIVATE_DECKS: DeckSummary[] = [
  {
    id: 101,
    name: 'My Custom French Vocab',
    owner: 'currentUser',
    topic: 'Languages',
    description: 'Words I encountered while watching French movies.',
    visibility: 'PRIVATE',
    totalCard: 15,
    savedCount: 0,
    ratingCount: 0,
    avgRating: 0,
    createdAt: new Date().toISOString(),
  },
  {
    id: 102,
    name: 'System Design Interview Prep',
    owner: 'currentUser',
    topic: 'Technology',
    description: 'Key concepts for backend architecture.',
    visibility: 'PUBLIC',
    totalCard: 50,
    savedCount: 5,
    ratingCount: 1,
    avgRating: 5.0,
    createdAt: new Date().toISOString(),
  },
];

export const MyDecks: React.FC = () => {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [searchTerm, setSearchTerm] = useState('');
  const navigate = useNavigate();

  const handleCreateDeck = (data: any) => {
    console.log('Create deck data:', data);
    // TODO: Call API to create deck
    setIsModalOpen(false);
  };

  return (
    <div className="flex flex-col h-full gap-6">
      {/* Header & Actions */}
      <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4 bg-slate-900 p-6 rounded-2xl border border-slate-800">
        <div>
          <h1 className="text-2xl font-bold text-slate-100 mb-1">My Decks</h1>
          <p className="text-sm text-slate-400">Manage the decks you have created.</p>
        </div>

        <div className="flex items-center gap-3 w-full md:w-auto">
          <div className="relative flex-1 md:w-64">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-500" size={18} />
            <input
              type="text"
              placeholder="Search my decks..."
              className="w-full bg-slate-950 border border-slate-800 rounded-lg pl-10 pr-4 py-2.5 text-sm text-slate-200 placeholder:text-slate-500 focus:outline-none focus:ring-2 focus:ring-blue-500/50 focus:border-blue-500 transition-all"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
          </div>

          <Button className="flex-shrink-0" onClick={() => setIsModalOpen(true)}>
            <Plus size={18} className="mr-2" />
            <span className="hidden sm:inline">Create Deck</span>
            <span className="sm:hidden">Create</span>
          </Button>
        </div>
      </div>

      {/* Grid of Decks */}
      {MOCK_PRIVATE_DECKS.length > 0 ? (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
          {MOCK_PRIVATE_DECKS.map((deck) => (
            <DeckCard key={deck.id} deck={deck} onClick={() => navigate(`/deck/${deck.id}`)} />
          ))}
        </div>
      ) : (
        <div className="flex flex-col items-center justify-center p-12 bg-slate-900 border border-slate-800 border-dashed rounded-2xl flex-1">
          <div className="w-16 h-16 bg-slate-800 rounded-full flex items-center justify-center mb-4">
            <Plus size={24} className="text-slate-400" />
          </div>
          <h3 className="text-lg font-medium text-slate-200 mb-2">No decks yet</h3>
          <p className="text-sm text-slate-500 mb-6 text-center max-w-sm">
            You haven't created any decks yet. Create your first deck to start adding flashcards.
          </p>
          <Button onClick={() => setIsModalOpen(true)}>Create New Deck</Button>
        </div>
      )}

      {/* Create Deck Modal */}
      <DeckFormModal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        onSubmit={handleCreateDeck}
      />
    </div>
  );
};
