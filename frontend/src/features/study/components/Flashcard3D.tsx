import React from 'react';
import { Volume2 } from 'lucide-react';
import { cn } from '@/shared/utils/cn';
import type { Card } from '@/features/decks/types';

interface Flashcard3DProps {
  card: Card;
  isFlipped: boolean;
  onFlip: () => void;
  typedAnswer?: string;
}

export const Flashcard3D: React.FC<Flashcard3DProps> = ({
  card,
  isFlipped,
  onFlip,
  typedAnswer,
}) => {
  const playAudio = (e: React.MouseEvent, url?: string) => {
    e.stopPropagation();
    if (url) {
      const audio = new Audio(url);
      audio.play();
    }
  };

  return (
    <div
      className="relative w-full max-w-2xl aspect-[4/3] sm:aspect-[16/9] mx-auto cursor-pointer perspective-1000"
      onClick={onFlip}
    >
      <div
        className={cn(
          'w-full h-full relative transition-transform duration-700 transform-style-3d shadow-2xl rounded-3xl',
          isFlipped ? 'rotate-y-180' : ''
        )}
      >
        {/* FRONT SIDE */}
        <div className="absolute inset-0 w-full h-full backface-hidden bg-slate-900 border border-slate-700 rounded-3xl p-8 flex flex-col items-center justify-center">
          {card.front.imageUrl && (
            <div className="mb-6 w-full max-h-[50%] flex justify-center">
              <img
                src={card.front.imageUrl}
                alt="Front"
                className="max-h-full object-contain rounded-xl"
              />
            </div>
          )}

          <h2 className="text-2xl md:text-3xl lg:text-4xl font-bold text-center text-slate-100 mb-6 leading-relaxed">
            {card.front.text}
          </h2>

          {card.front.audioUrl && (
            <button
              onClick={(e) => playAudio(e, card.front.audioUrl)}
              className="mt-auto flex items-center justify-center w-12 h-12 rounded-full bg-blue-500/20 text-blue-400 hover:bg-blue-500/30 transition-colors"
            >
              <Volume2 size={24} />
            </button>
          )}

          {!isFlipped && (
            <div className="absolute bottom-6 text-sm text-slate-500 animate-pulse">
              Tap to show answer
            </div>
          )}
        </div>

        {/* BACK SIDE */}
        <div className="absolute inset-0 w-full h-full backface-hidden rotate-y-180 bg-slate-800 border border-slate-600 rounded-3xl p-8 flex flex-col items-center justify-center">
          {/* Small Front reference at the top */}
          <div className="absolute top-6 left-0 right-0 px-8 text-center border-b border-slate-700 pb-4">
            <p className="text-sm text-slate-400 truncate">{card.front.text}</p>
          </div>

          <div className="flex-1 flex flex-col items-center justify-center w-full mt-12">
            {card.back.imageUrl && (
              <div className="mb-6 w-full max-h-[50%] flex justify-center">
                <img
                  src={card.back.imageUrl}
                  alt="Back"
                  className="max-h-full object-contain rounded-xl"
                />
              </div>
            )}

            <h2 className="text-2xl md:text-3xl lg:text-4xl font-bold text-center text-green-400 mb-6 leading-relaxed">
              {card.back.text}
            </h2>

            {card.type === 'TYPE' && typedAnswer !== undefined && (
              <div className="mt-2 mb-6 p-4 rounded-xl bg-slate-900/80 border border-slate-700 w-full max-w-md text-center">
                <span className="text-xs text-slate-400 uppercase tracking-wider block mb-2">
                  You typed
                </span>
                <span
                  className={cn(
                    'font-mono text-lg font-medium',
                    typedAnswer.trim().toLowerCase() === card.back.text?.trim().toLowerCase()
                      ? 'text-green-400'
                      : 'text-red-400 line-through decoration-red-500/50'
                  )}
                >
                  {typedAnswer || '(empty)'}
                </span>
              </div>
            )}

            {card.back.audioUrl && (
              <button
                onClick={(e) => playAudio(e, card.back.audioUrl)}
                className="mt-auto flex items-center justify-center w-12 h-12 rounded-full bg-green-500/20 text-green-400 hover:bg-green-500/30 transition-colors"
              >
                <Volume2 size={24} />
              </button>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};
