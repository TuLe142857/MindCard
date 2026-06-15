import React, { useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
  Layers,
  Star,
  Download,
  Globe,
  Lock,
  ArrowLeft,
  Clock,
  Plus,
  Music,
  Loader2,
} from 'lucide-react';
import { Button } from '@/shared/components/ui/Button';
import type { Card } from '@/features/decks/types';
import { useAppSelector } from '@/store/hooks';
import { CardFormModal } from '@/features/decks/components/CardFormModal';
import { DeckFormModal } from '@/features/decks/components/DeckFormModal';
import { useTopics } from '@/features/topics/hooks/useTopics';
import {
  useDeckDetails,
  useDeckCards,
  useSaveDeck,
  useBatchAddCards,
  useUpdateDeck,
} from '@/features/decks/hooks/useDecks';
import { useUpdateCard, useUpdateCardMedia } from '@/features/cards/hooks/useCards';
import { toast } from 'react-toastify';

export const DeckDetails: React.FC = () => {
  const { deckId } = useParams<{ deckId: string }>();
  const parsedDeckId = Number(deckId);
  const navigate = useNavigate();
  const { user } = useAppSelector((state) => state.auth);

  const [isCardModalOpen, setIsCardModalOpen] = useState(false);
  const [isDeckModalOpen, setIsDeckModalOpen] = useState(false);
  const [editingCard, setEditingCard] = useState<Card | null>(null);

  const { data: deck, isLoading: isLoadingDeck, error: deckError } = useDeckDetails(parsedDeckId);
  const { data: cardsData, isLoading: isLoadingCards } = useDeckCards(parsedDeckId, {
    page: 1,
    limit: 100,
  });
  const { data: topicsData } = useTopics();

  const { mutate: saveDeck, isPending: isSaving } = useSaveDeck();
  const { mutate: batchAddCards, isPending: isAddingCard } = useBatchAddCards();
  const { mutate: updateDeck, isPending: isUpdatingDeck } = useUpdateDeck();
  const { mutateAsync: updateCardText } = useUpdateCard(parsedDeckId);
  const { mutateAsync: updateCardMedia } = useUpdateCardMedia(parsedDeckId);

  const cards = cardsData?.data || [];
  const isOwner = user?.username === deck?.owner;

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  const handleAddCard = async (data: any) => {
    if (editingCard) {
      const toastId = toast.loading('Updating card...');
      try {
        await updateCardText({
          cardId: editingCard.id,
          data: { type: data.type, frontText: data.frontText, backText: data.backText },
        });

        if (data.frontImage)
          await updateCardMedia({
            cardId: editingCard.id,
            slot: 'front-image',
            file: data.frontImage,
          });
        if (data.backImage)
          await updateCardMedia({
            cardId: editingCard.id,
            slot: 'back-image',
            file: data.backImage,
          });
        if (data.frontAudio)
          await updateCardMedia({
            cardId: editingCard.id,
            slot: 'front-audio',
            file: data.frontAudio,
          });
        if (data.backAudio)
          await updateCardMedia({
            cardId: editingCard.id,
            slot: 'back-audio',
            file: data.backAudio,
          });

        toast.update(toastId, {
          type: 'success',
          render: 'Card updated successfully',
          isLoading: false,
          autoClose: 3000,
        });
        setIsCardModalOpen(false);
        setEditingCard(null);
      } catch {
        toast.update(toastId, {
          type: 'error',
          render: 'Failed to update card',
          isLoading: false,
          autoClose: 3000,
        });
      }
    } else {
      const toastId = toast.loading('Adding card...');
      batchAddCards(
        { deckId: parsedDeckId, cards: [data] },
        {
          onSuccess: () => {
            toast.update(toastId, {
              type: 'success',
              render: 'Card added successfully',
              isLoading: false,
              autoClose: 3000,
            });
            setIsCardModalOpen(false);
            setEditingCard(null);
          },
          onError: () => {
            toast.update(toastId, {
              type: 'error',
              render: 'Failed to add card',
              isLoading: false,
              autoClose: 3000,
            });
          },
        }
      );
    }
  };

  const openEditCard = (card: Card) => {
    setEditingCard(card);
    setIsCardModalOpen(true);
  };

  const openAddCard = () => {
    setEditingCard(null);
    setIsCardModalOpen(true);
  };

  const handleSaveDeck = () => {
    const toastId = toast.loading('Saving deck to your library...');
    saveDeck(parsedDeckId, {
      onSuccess: () => {
        toast.update(toastId, {
          type: 'success',
          render: 'Deck saved successfully',
          isLoading: false,
          autoClose: 3000,
        });
      },
      onError: () => {
        toast.update(toastId, {
          type: 'error',
          render: 'Failed to save deck',
          isLoading: false,
          autoClose: 3000,
        });
      },
    });
  };

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  const handleUpdateDeck = (data: any) => {
    const toastId = toast.loading('Updating deck...');
    updateDeck(
      { deckId: parsedDeckId, data },
      {
        onSuccess: () => {
          toast.update(toastId, {
            type: 'success',
            render: 'Deck updated successfully',
            isLoading: false,
            autoClose: 3000,
          });
          setIsDeckModalOpen(false);
        },
        onError: () => {
          toast.update(toastId, {
            type: 'error',
            render: 'Deck update failed',
            isLoading: false,
            autoClose: 3000,
          });
        },
      }
    );
  };

  if (isLoadingDeck) {
    return (
      <div className="flex items-center justify-center h-full min-h-100">
        <Loader2 className="animate-spin text-blue-500" size={40} />
      </div>
    );
  }

  if (deckError || !deck) {
    return (
      <div className="flex flex-col items-center justify-center h-full min-h-100 text-slate-400 gap-4">
        <p className="text-xl text-slate-300">Deck not found or failed to load.</p>
        <Button variant="outline" onClick={() => navigate(-1)}>
          Go Back
        </Button>
      </div>
    );
  }

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
                  <>
                    <Globe size={16} /> Public
                  </>
                ) : (
                  <>
                    <Lock size={16} /> Private
                  </>
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
          <div className="flex flex-col gap-4 min-w-50">
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
                  <span className="text-xl font-bold text-slate-200">
                    {deck.avgRating > 0 ? deck.avgRating.toFixed(1) : 'New'}
                  </span>
                </div>
                <span className="text-xs text-slate-500">{deck.ratingCount} Ratings</span>
              </div>
            </div>

            {/* Action Buttons based on context */}
            {deck.isSaved ? (
              <Button
                className="w-full cursor-default bg-slate-800/80 text-green-400 hover:bg-slate-800/80 border border-green-500/20"
                onClick={(e) => e.preventDefault()}
              >
                Saved
              </Button>
            ) : (
              <Button
                className="w-full bg-blue-600 hover:bg-blue-700 text-white shadow-[0_0_15px_rgba(37,99,235,0.3)]"
                onClick={handleSaveDeck}
                disabled={isSaving}
              >
                {isSaving ? <Loader2 className="animate-spin w-4 h-4 mr-2 inline" /> : null}
                Save to Library
              </Button>
            )}

            {isOwner && (
              <Button variant="outline" className="w-full" onClick={() => setIsDeckModalOpen(true)}>
                Edit Deck Settings
              </Button>
            )}
          </div>
        </div>
      </div>

      {/* Additional Info / Restrictions */}
      {!isOwner && !deck.isSaved && (
        <div className="bg-slate-900/50 border border-slate-800/50 rounded-xl p-6 text-center">
          <Layers size={32} className="mx-auto text-slate-600 mb-3" />
          <h3 className="text-lg font-medium text-slate-300 mb-2">Card Previews are hidden</h3>
          <p className="text-sm text-slate-500 max-w-md mx-auto">
            You must save this deck to your library before you can view and study its cards. Save
            the deck to get started!
          </p>
        </div>
      )}

      {(isOwner || deck.isSaved) && (
        <div className="flex flex-col gap-4">
          <div className="flex items-center justify-between">
            <h3 className="text-xl font-bold text-slate-200">
              {isOwner ? 'Manage Cards' : 'Preview Cards'}
            </h3>
            {isOwner && (
              <Button size="sm" onClick={openAddCard} className="gap-2" disabled={isAddingCard}>
                {isAddingCard ? (
                  <Loader2 className="animate-spin w-4 h-4 mr-1 inline" />
                ) : (
                  <Plus size={16} />
                )}
                Add Card
              </Button>
            )}
          </div>

          {/* Cards List */}
          <div className="flex flex-col gap-3">
            {isLoadingCards ? (
              <div className="flex justify-center py-8">
                <Loader2 className="animate-spin text-slate-500" size={24} />
              </div>
            ) : cards.length === 0 ? (
              <div className="text-center py-8 text-slate-500 border border-slate-800 border-dashed rounded-xl">
                No cards in this deck yet.
              </div>
            ) : (
              cards.map((card, index) => (
                <div
                  key={card.id}
                  className={`flex flex-col md:flex-row bg-slate-900 border border-slate-800 rounded-xl overflow-hidden transition-colors ${isOwner ? 'hover:border-blue-500/30 cursor-pointer' : ''}`}
                  onClick={() => isOwner && openEditCard(card)}
                >
                  <div className="flex flex-col items-center justify-center bg-slate-950 p-4 md:w-24 border-b md:border-b-0 md:border-r border-slate-800 gap-2">
                    <span className="text-slate-500 font-medium text-lg">#{index + 1}</span>
                    <span className="text-[10px] font-bold px-2 py-0.5 rounded-md bg-slate-800/80 text-slate-400 border border-slate-700 tracking-wider">
                      {card.type === 'TYPE' ? 'TYPING' : 'BASIC'}
                    </span>
                  </div>

                  <div className="flex-1 grid grid-cols-1 md:grid-cols-2 divide-y md:divide-y-0 md:divide-x divide-slate-800">
                    {/* Front */}
                    <div className="p-4 flex flex-col gap-2">
                      <span className="text-xs font-medium text-blue-400 uppercase tracking-wider">
                        Front
                      </span>
                      {card.front.imageUrl && (
                        <div className="h-20 w-full rounded bg-slate-950 flex items-center justify-center overflow-hidden mb-2">
                          <img
                            src={card.front.imageUrl}
                            alt="Front"
                            className="h-full object-contain"
                          />
                        </div>
                      )}
                      <p className="text-sm text-slate-300 line-clamp-3">{card.front.text}</p>
                      {card.front.audioUrl && <Music size={14} className="text-slate-500 mt-2" />}
                    </div>

                    {/* Back */}
                    <div className="p-4 flex flex-col gap-2">
                      <span className="text-xs font-medium text-green-400 uppercase tracking-wider">
                        Back
                      </span>
                      {card.back.imageUrl && (
                        <div className="h-20 w-full rounded bg-slate-950 flex items-center justify-center overflow-hidden mb-2">
                          <img
                            src={card.back.imageUrl}
                            alt="Back"
                            className="h-full object-contain"
                          />
                        </div>
                      )}
                      <p className="text-sm text-slate-300 line-clamp-3">{card.back.text}</p>
                      {card.back.audioUrl && <Music size={14} className="text-slate-500 mt-2" />}
                    </div>
                  </div>
                </div>
              ))
            )}
          </div>
        </div>
      )}

      {/* Card Form Modal */}
      <CardFormModal
        isOpen={isCardModalOpen}
        onClose={() => setIsCardModalOpen(false)}
        initialData={editingCard}
        onSubmit={handleAddCard}
      />

      {/* Deck Form Modal */}
      {deck && (
        <DeckFormModal
          isOpen={isDeckModalOpen}
          onClose={() => setIsDeckModalOpen(false)}
          initialData={{
            name: deck.name,
            topicId: topicsData?.find((t) => t.name === deck.topic)?.id || 0,
            description: deck.description || '',
            visibility: deck.visibility,
          }}
          onSubmit={handleUpdateDeck}
          isLoading={isUpdatingDeck}
        />
      )}
    </div>
  );
};
