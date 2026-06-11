import React, { useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { X, Check, BrainCircuit } from 'lucide-react';
import { Button } from '@/shared/components/ui/Button';
import { Flashcard3D } from '@/features/study/components/Flashcard3D';
import type { Card } from '@/features/decks/types';

const MOCK_STUDY_CARDS: Card[] = [
  {
    id: 1,
    type: 'BASIC',
    front: { text: 'What is the Event Loop in JavaScript?' },
    back: {
      text: 'The Event Loop is a mechanism that handles asynchronous callbacks in Node.js and browsers, allowing non-blocking I/O operations.',
    },
  },
  {
    id: 5,
    type: 'BASIC',
    front: {
      text: 'Listen to the audio and identify the animal',
      audioUrl: 'https://actions.google.com/sounds/v1/animals/cat_meow_2.ogg',
    },
    back: {
      text: 'Cat',
      imageUrl: 'https://upload.wikimedia.org/wikipedia/commons/4/4d/Cat_November_2010-1a.jpg',
    },
  },
  {
    id: 4,
    type: 'TYPE',
    front: { text: 'How do you declare a constant variable in JavaScript? (Type exactly)' },
    back: { text: 'const' },
  },
  {
    id: 2,
    type: 'BASIC',
    front: { text: 'What is a Closure in JavaScript?' },
    back: {
      text: 'A closure is the combination of a function bundled together with references to its surrounding state (the lexical environment).',
    },
  },
  {
    id: 3,
    type: 'BASIC',
    front: {
      text: 'Identify this logo',
      imageUrl: 'https://upload.wikimedia.org/wikipedia/commons/a/a7/React-icon.svg',
    },
    back: {
      text: 'React',
      audioUrl: 'https://actions.google.com/sounds/v1/cartoon/clang_and_wobble.ogg',
    },
  },
];

export const StudySession: React.FC = () => {
  const { savedDeckId } = useParams<{ savedDeckId: string }>();
  const navigate = useNavigate();

  const [currentIndex, setCurrentIndex] = useState(0);
  const [isFlipped, setIsFlipped] = useState(false);
  const [sessionCompleted, setSessionCompleted] = useState(false);
  const [typedAnswer, setTypedAnswer] = useState('');

  const currentCard = MOCK_STUDY_CARDS[currentIndex];
  const progressPercent = (currentIndex / MOCK_STUDY_CARDS.length) * 100;

  const handleFlip = () => {
    if (!isFlipped) setIsFlipped(true);
  };

  const handleRate = (rating: number) => {
    console.log(`Card ${currentCard.id} rated: ${rating}`);

    // Move to next card
    if (currentIndex < MOCK_STUDY_CARDS.length - 1) {
      setIsFlipped(false);
      // Small timeout to allow flip animation back to front before changing text
      setTimeout(() => {
        setCurrentIndex((prev) => prev + 1);
        setTypedAnswer(''); // reset typed answer
      }, 300);
    } else {
      setSessionCompleted(true);
    }
  };

  if (sessionCompleted) {
    return (
      <div className="flex flex-col items-center justify-center h-[calc(100vh-100px)] animate-in zoom-in fade-in duration-500">
        <div className="w-24 h-24 rounded-full bg-green-500/20 flex items-center justify-center mb-6">
          <Check size={48} className="text-green-500" />
        </div>
        <h1 className="text-3xl font-bold text-slate-100 mb-2">Session Complete!</h1>
        <p className="text-slate-400 mb-8 max-w-md text-center">
          Great job! You have reviewed all due cards for this session. Keep up the good work.
        </p>
        <Button onClick={() => navigate('/library')} className="px-8 py-6 text-lg">
          Return to Library
        </Button>
      </div>
    );
  }

  return (
    <div className="flex flex-col h-[calc(100vh-120px)] md:h-[calc(100vh-80px)]">
      {/* Top Bar: Progress & Exit */}
      <div className="flex items-center justify-between mb-8">
        <div className="flex items-center gap-4 flex-1">
          <button
            onClick={() => navigate('/library')}
            className="w-10 h-10 rounded-full bg-slate-800 hover:bg-slate-700 flex items-center justify-center text-slate-400 hover:text-slate-200 transition-colors"
          >
            <X size={20} />
          </button>

          <div className="flex-1 max-w-xl">
            <div className="flex justify-between text-xs font-medium text-slate-400 mb-2">
              <span>
                Card {currentIndex + 1} of {MOCK_STUDY_CARDS.length}
              </span>
              <span>{Math.round(progressPercent)}%</span>
            </div>
            <div className="h-2 w-full bg-slate-800 rounded-full overflow-hidden">
              <div
                className="h-full bg-blue-500 transition-all duration-500 ease-out"
                style={{ width: `${progressPercent}%` }}
              />
            </div>
          </div>
        </div>
      </div>

      {/* Main Study Area */}
      <div className="flex-1 flex flex-col justify-center items-center pb-12">
        <Flashcard3D
          card={currentCard}
          isFlipped={isFlipped}
          onFlip={handleFlip}
          typedAnswer={currentCard.type === 'TYPE' ? typedAnswer : undefined}
        />
      </div>

      {/* Bottom Controls */}
      <div className="mt-auto pt-6 max-w-2xl w-full mx-auto">
        {!isFlipped ? (
          currentCard.type === 'TYPE' ? (
            <div className="flex flex-col gap-3">
              <input
                type="text"
                className="w-full bg-slate-900 border-2 border-slate-700 focus:border-blue-500 rounded-xl px-6 py-4 text-lg text-slate-100 outline-none transition-colors"
                placeholder="Type your answer here..."
                value={typedAnswer}
                onChange={(e) => setTypedAnswer(e.target.value)}
                onKeyDown={(e) => {
                  if (e.key === 'Enter') handleFlip();
                }}
                autoFocus
              />
              <Button
                className="w-full h-14 text-lg font-bold shadow-[0_0_20px_rgba(37,99,235,0.2)]"
                onClick={handleFlip}
              >
                Check Answer
              </Button>
            </div>
          ) : (
            <Button
              className="w-full h-16 text-lg font-bold shadow-[0_0_20px_rgba(37,99,235,0.2)]"
              onClick={handleFlip}
            >
              Show Answer
            </Button>
          )
        ) : (
          <div className="flex flex-col animate-in slide-in-from-bottom-4 fade-in duration-300">
            <h3 className="text-center text-sm text-slate-400 mb-4 flex items-center justify-center gap-2">
              <BrainCircuit size={16} /> How well did you remember this?
            </h3>

            {/* Rating Buttons: 0 to 5 */}
            <div className="grid grid-cols-3 md:grid-cols-6 gap-2">
              <button
                onClick={() => handleRate(0)}
                className="flex flex-col items-center justify-center p-3 rounded-xl bg-red-500/10 hover:bg-red-500/20 border border-red-500/30 text-red-400 transition-all"
              >
                <span className="text-xl font-bold mb-1">0</span>
                <span className="text-[10px] uppercase font-semibold">Again</span>
              </button>

              <button
                onClick={() => handleRate(1)}
                className="flex flex-col items-center justify-center p-3 rounded-xl bg-orange-500/10 hover:bg-orange-500/20 border border-orange-500/30 text-orange-400 transition-all"
              >
                <span className="text-xl font-bold mb-1">1</span>
                <span className="text-[10px] uppercase font-semibold">Hard</span>
              </button>

              <button
                onClick={() => handleRate(2)}
                className="flex flex-col items-center justify-center p-3 rounded-xl bg-yellow-500/10 hover:bg-yellow-500/20 border border-yellow-500/30 text-yellow-400 transition-all"
              >
                <span className="text-xl font-bold mb-1">2</span>
                <span className="text-[10px] uppercase font-semibold">Good</span>
              </button>

              <button
                onClick={() => handleRate(3)}
                className="flex flex-col items-center justify-center p-3 rounded-xl bg-emerald-500/10 hover:bg-emerald-500/20 border border-emerald-500/30 text-emerald-400 transition-all"
              >
                <span className="text-xl font-bold mb-1">3</span>
                <span className="text-[10px] uppercase font-semibold">Easy</span>
              </button>

              <button
                onClick={() => handleRate(4)}
                className="flex flex-col items-center justify-center p-3 rounded-xl bg-cyan-500/10 hover:bg-cyan-500/20 border border-cyan-500/30 text-cyan-400 transition-all"
              >
                <span className="text-xl font-bold mb-1">4</span>
                <span className="text-[10px] uppercase font-semibold">V. Easy</span>
              </button>

              <button
                onClick={() => handleRate(5)}
                className="flex flex-col items-center justify-center p-3 rounded-xl bg-indigo-500/10 hover:bg-indigo-500/20 border border-indigo-500/30 text-indigo-400 transition-all"
              >
                <span className="text-xl font-bold mb-1">5</span>
                <span className="text-[10px] uppercase font-semibold">Perfect</span>
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};
