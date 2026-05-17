// src/components/ui/TopicRow.tsx
import { useRef, useState, useEffect } from 'react';
import type { DeckWithMeta, Topic } from '../../types';
import DeckCard from './DeckCard';

interface TopicRowProps {
  topic: Topic;
  decks: DeckWithMeta[];
  onSaveToggle?: (deckId: number, saved: boolean) => void;
}

export default function TopicRow({ topic, decks, onSaveToggle }: TopicRowProps) {
  const scrollRef = useRef<HTMLDivElement>(null);
  const [canScrollLeft,  setCanScrollLeft]  = useState(false);
  const [canScrollRight, setCanScrollRight] = useState(true);

  const checkScroll = () => {
    const el = scrollRef.current;
    if (!el) return;
    setCanScrollLeft(el.scrollLeft > 8);
    setCanScrollRight(el.scrollLeft + el.clientWidth < el.scrollWidth - 8);
  };

  useEffect(() => {
    const el = scrollRef.current;
    if (!el) return;
    checkScroll();
    el.addEventListener('scroll', checkScroll, { passive: true });
    const ro = new ResizeObserver(checkScroll);
    ro.observe(el);
    return () => { el.removeEventListener('scroll', checkScroll); ro.disconnect(); };
  }, []);

  const scroll = (dir: 'left' | 'right') => {
    const el = scrollRef.current;
    if (!el) return;
    const amount = 240 * 2; // ~2 cards
    el.scrollBy({ left: dir === 'left' ? -amount : amount, behavior: 'smooth' });
  };

  return (
    <section className="space-y-3">
      {/* Topic header */}
      <div className="flex items-center gap-3 px-1">
        <div className={`
          flex items-center justify-center
          w-9 h-9 rounded-xl text-lg
          bg-gradient-to-br ${topic.color} shadow-md
        `}>
          {topic.icon}
        </div>
        <div>
          <h2 className="text-base font-bold text-slate-100">{topic.name}</h2>
          <p className="text-xs text-slate-400">{decks.length} bộ thẻ</p>
        </div>

        {/* Scroll controls – right side */}
        <div className="ml-auto flex gap-2">
          <ArrowButton
            dir="left"
            disabled={!canScrollLeft}
            onClick={() => scroll('left')}
          />
          <ArrowButton
            dir="right"
            disabled={!canScrollRight}
            onClick={() => scroll('right')}
          />
        </div>
      </div>

      {/* Horizontal scroll container */}
      <div
        ref={scrollRef}
        className="
          flex gap-4 overflow-x-auto pb-3
          scroll-smooth snap-x snap-mandatory
          [scrollbar-width:none] [&::-webkit-scrollbar]:hidden
        "
      >
        {decks.map(deck => (
          <div key={deck.id} className="snap-start">
            <DeckCard deck={deck} onSaveToggle={onSaveToggle} />
          </div>
        ))}
      </div>

      {/* Divider */}
      <div className="border-b border-slate-700/40 mt-2" />
    </section>
  );
}

// ─── Arrow Button ─────────────────────────────────────────────────────────────
function ArrowButton({
  dir,
  disabled,
  onClick,
}: {
  dir: 'left' | 'right';
  disabled: boolean;
  onClick: () => void;
}) {
  return (
    <button
      onClick={onClick}
      disabled={disabled}
      className={`
        flex items-center justify-center w-8 h-8 rounded-full
        border transition-all duration-200
        ${disabled
          ? 'border-slate-700 text-slate-600 cursor-not-allowed opacity-40'
          : 'border-slate-600 text-slate-300 hover:border-indigo-400 hover:text-indigo-300 hover:bg-indigo-500/10 active:scale-90'
        }
      `}
    >
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth={2.5} className="w-4 h-4">
        {dir === 'left'
          ? <path d="M15 18l-6-6 6-6" />
          : <path d="M9 18l6-6-6-6" />
        }
      </svg>
    </button>
  );
}
