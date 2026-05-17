// src/components/ui/DeckCard.tsx
import { useState } from 'react';
import type { DeckWithMeta } from '../../types';
import StarRating from './StarRating';

interface DeckCardProps {
  deck: DeckWithMeta;
  onSaveToggle?: (deckId: number, saved: boolean) => void;
}

export default function DeckCard({ deck, onSaveToggle }: DeckCardProps) {
  const [saved, setSaved] = useState(deck.is_saved);
  const [saveAnim, setSaveAnim] = useState(false);

  const handleSave = (e: React.MouseEvent) => {
    e.stopPropagation();
    const next = !saved;
    setSaved(next);
    setSaveAnim(true);
    setTimeout(() => setSaveAnim(false), 300);
    onSaveToggle?.(deck.id, next);
  };

  return (
    <div
      className="
        group relative flex flex-col
        w-56 min-w-[14rem] shrink-0
        rounded-2xl bg-slate-800/60 border border-slate-700/50
        backdrop-blur-sm shadow-lg
        transition-all duration-300
        hover:-translate-y-1.5 hover:shadow-indigo-500/20 hover:shadow-2xl
        hover:border-indigo-500/40
        cursor-pointer overflow-hidden
      "
    >
      {/* Top gradient band */}
      <div className={`h-1.5 w-full bg-gradient-to-r ${deck.topic.color}`} />

      <div className="flex flex-col gap-3 p-4 flex-1">
        {/* Topic badge */}
        <div className="flex items-center justify-between">
          <span className={`
            inline-flex items-center gap-1.5 rounded-full px-2.5 py-0.5
            text-xs font-medium bg-gradient-to-r ${deck.topic.color}
            text-white/90
          `}>
            <span>{deck.topic.icon}</span>
            {deck.topic.name}
          </span>

          {/* Save button */}
          <button
            id={`save-deck-${deck.id}`}
            onClick={handleSave}
            title={saved ? 'Bỏ lưu' : 'Lưu deck'}
            className={`
              p-1.5 rounded-lg transition-all duration-200
              ${saved
                ? 'text-indigo-400 bg-indigo-500/20 hover:bg-indigo-500/30'
                : 'text-slate-500 hover:text-slate-300 hover:bg-slate-700'
              }
              ${saveAnim ? 'scale-125' : 'scale-100'}
            `}
          >
            <svg
              viewBox="0 0 24 24"
              fill={saved ? 'currentColor' : 'none'}
              stroke="currentColor"
              strokeWidth={2}
              className="w-4 h-4"
            >
              <path d="M19 21l-7-5-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2z" />
            </svg>
          </button>
        </div>

        {/* Deck name */}
        <h3 className="
          text-sm font-semibold text-slate-100 leading-snug
          line-clamp-2 group-hover:text-white
          transition-colors
        ">
          {deck.name}
        </h3>

        {/* Description */}
        {deck.description && (
          <p className="text-xs text-slate-400 line-clamp-2 leading-relaxed">
            {deck.description}
          </p>
        )}

        {/* Meta */}
        <div className="flex items-center gap-3 text-xs text-slate-400 mt-auto">
          <span className="flex items-center gap-1">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth={2} className="w-3.5 h-3.5">
              <rect x="2" y="3" width="20" height="14" rx="2" />
              <line x1="8" y1="21" x2="16" y2="21" />
              <line x1="12" y1="17" x2="12" y2="21" />
            </svg>
            {deck.card_count} thẻ
          </span>
          <span className="text-slate-600">•</span>
          <span className="flex items-center gap-1">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth={2} className="w-3.5 h-3.5">
              <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" />
              <circle cx="12" cy="7" r="4" />
            </svg>
            {deck.owner.username.split(' ').slice(-1)[0]}
          </span>
        </div>

        <StarRating rating={deck.avg_rating} count={deck.rating_count} />
      </div>

      {/* Start button */}
      <div className="px-4 pb-4">
        <button
          id={`start-deck-${deck.id}`}
          className={`
            w-full py-2 rounded-xl text-sm font-semibold
            bg-gradient-to-r ${deck.topic.color}
            text-white shadow-md
            transition-all duration-200
            hover:opacity-90 hover:shadow-lg active:scale-95
          `}
        >
          Bắt đầu học
        </button>
      </div>

      {/* Hover glow */}
      <div className="
        absolute inset-0 rounded-2xl opacity-0
        group-hover:opacity-100 transition-opacity duration-300
        pointer-events-none
        ring-1 ring-inset ring-indigo-400/20
      " />
    </div>
  );
}
