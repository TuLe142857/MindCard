import React from 'react';
import { Layers, Calendar, Play, RefreshCw } from 'lucide-react';
import type { SavedDeckSummary } from '../types';
import { Button } from '@/shared/components/ui/Button';

interface SavedDeckCardProps {
  deck: SavedDeckSummary;
  onClickDetails: () => void;
  onStudyNew: () => void;
  onStudyReview: () => void;
  onSyncUpdate: () => void;
}

export const SavedDeckCard: React.FC<SavedDeckCardProps> = ({ 
  deck, 
  onClickDetails, 
  onStudyNew, 
  onStudyReview,
  onSyncUpdate
}) => {
  const totalDue = deck.dueCards || (deck.newCards + deck.learningCards + deck.reviewCards);
  const totalReviewAndLearning = deck.learningCards + deck.reviewCards;
  
  return (
    <div className="group flex flex-col bg-slate-900 border border-slate-800 rounded-2xl overflow-hidden hover:border-blue-500/50 hover:shadow-[0_0_20px_rgba(37,99,235,0.15)] transition-all relative">
      {/* Due Badge - Only show if there are cards to study */}
      {totalDue > 0 && (
        <div className="absolute top-3 right-3 z-10 flex items-center justify-center min-w-6 h-6 px-1.5 bg-red-500 text-white text-xs font-bold rounded-full shadow-lg" title={`${totalDue} cards due`}>
          {totalDue > 99 ? '99+' : totalDue}
        </div>
      )}

      {/* Main Content (Clickable to details) */}
      <div 
        className="p-5 flex flex-col flex-1 relative z-0 cursor-pointer"
        onClick={onClickDetails}
      >
        <div className="flex items-center justify-between mb-3 pr-8">
          <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-500/10 text-blue-400 border border-blue-500/20">
            {deck.topic}
          </span>
          
          {deck.hasUpdate && (
            <span className="inline-flex items-center gap-1 px-2 py-0.5 rounded text-[10px] font-bold bg-yellow-500/20 text-yellow-500 border border-yellow-500/30 uppercase tracking-wider animate-pulse">
              <RefreshCw size={10} />
              Update
            </span>
          )}
        </div>
        
        <h3 className="text-lg font-bold text-slate-100 mb-1 group-hover:text-blue-400 transition-colors line-clamp-1">
          {deck.name}
        </h3>
        
        <div className="flex items-center gap-2 text-xs text-slate-500 mt-auto pt-4 border-t border-slate-800/50">
          <Layers size={14} />
          <span>{deck.totalCards} cards</span>
          <span className="mx-1">•</span>
          <Calendar size={14} />
          <span>By {deck.creator}</span>
        </div>
      </div>

      {/* Progress Footer */}
      <div className="grid grid-cols-3 divide-x divide-slate-800 border-t border-slate-800 bg-slate-950/50 text-center">
        <div className="flex flex-col py-2 px-1">
          <span className="text-lg font-bold text-blue-400 leading-none mb-1">{deck.newCards}</span>
          <span className="text-[10px] text-slate-500 uppercase font-semibold">New</span>
        </div>
        <div className="flex flex-col py-2 px-1">
          <span className="text-lg font-bold text-red-400 leading-none mb-1">{deck.learningCards}</span>
          <span className="text-[10px] text-slate-500 uppercase font-semibold">Learn</span>
        </div>
        <div className="flex flex-col py-2 px-1">
          <span className="text-lg font-bold text-green-400 leading-none mb-1">{deck.reviewCards}</span>
          <span className="text-[10px] text-slate-500 uppercase font-semibold">Review</span>
        </div>
      </div>

      {/* Action Buttons */}
      <div className="grid grid-cols-2 gap-2 p-3 bg-slate-900 border-t border-slate-800">
        <Button 
          size="sm" 
          variant="outline" 
          className="w-full gap-1.5 border-slate-700 hover:bg-slate-800 text-blue-400"
          disabled={deck.newCards === 0}
          onClick={(e) => { e.stopPropagation(); onStudyNew(); }}
        >
          <Play size={14} className="fill-current" />
          Study New
        </Button>
        <Button 
          size="sm" 
          variant="outline" 
          className="w-full gap-1.5 border-slate-700 hover:bg-slate-800 text-green-400"
          disabled={totalReviewAndLearning === 0}
          onClick={(e) => { e.stopPropagation(); onStudyReview(); }}
        >
          <Play size={14} className="fill-current" />
          Review Due
        </Button>
        
        {deck.hasUpdate && (
          <Button 
            size="sm" 
            className="col-span-2 w-full gap-1.5 bg-yellow-500/10 text-yellow-500 border border-yellow-500/50 hover:bg-yellow-500/20"
            onClick={(e) => { e.stopPropagation(); onSyncUpdate(); }}
          >
            <RefreshCw size={14} />
            Review Updates
          </Button>
        )}
      </div>
    </div>
  );
};
