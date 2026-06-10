import React from 'react';
import { Layers, Star, Download, Globe, Lock } from 'lucide-react';
import { cn } from '@/shared/utils/cn';
import type { DeckSummary } from '../types';

interface DeckCardProps {
  deck: DeckSummary;
  className?: string;
  onClick?: () => void;
}

export const DeckCard: React.FC<DeckCardProps> = ({ deck, className, onClick }) => {
  return (
    <div 
      className={cn(
        "group flex flex-col bg-slate-900 border border-slate-800 rounded-xl overflow-hidden hover:border-blue-500/50 transition-all duration-300 hover:shadow-[0_0_20px_rgba(59,130,246,0.1)] cursor-pointer",
        className
      )}
      onClick={onClick}
    >
      <div className="p-5 flex-1 flex flex-col">
        <div className="flex justify-between items-start mb-3">
          <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-500/10 text-blue-400">
            {deck.topic}
          </span>
          {deck.visibility === 'PUBLIC' ? (
            <Globe size={16} className="text-slate-500" title="Public" />
          ) : (
            <Lock size={16} className="text-slate-500" title="Private" />
          )}
        </div>
        
        <h3 className="text-lg font-bold text-slate-100 mb-2 line-clamp-1 group-hover:text-blue-400 transition-colors">
          {deck.name}
        </h3>
        
        <p className="text-sm text-slate-400 line-clamp-2 flex-1 mb-4">
          {deck.description || 'No description provided.'}
        </p>

        <div className="flex items-center gap-2 mb-4 mt-auto">
          <div className="w-6 h-6 rounded-full bg-slate-800 flex items-center justify-center text-xs font-medium text-slate-300">
            {deck.owner.charAt(0).toUpperCase()}
          </div>
          <span className="text-xs text-slate-400 truncate">by <span className="text-slate-300">{deck.owner}</span></span>
        </div>
      </div>
      
      <div className="bg-slate-950/50 px-5 py-3 border-t border-slate-800/50 flex items-center justify-between mt-auto">
        <div className="flex items-center gap-4">
          <div className="flex items-center gap-1.5 text-slate-400 text-xs" title="Total Cards">
            <Layers size={14} />
            <span>{deck.totalCard}</span>
          </div>
          <div className="flex items-center gap-1.5 text-slate-400 text-xs" title="Saves">
            <Download size={14} />
            <span>{deck.savedCount}</span>
          </div>
        </div>
        
        <div className="flex items-center gap-1">
          <Star size={14} className={cn("fill-current", deck.avgRating > 0 ? "text-yellow-500" : "text-slate-600")} />
          <span className="text-xs font-medium text-slate-300">
            {deck.avgRating > 0 ? deck.avgRating.toFixed(1) : 'New'}
          </span>
          {deck.ratingCount > 0 && (
            <span className="text-xs text-slate-500 ml-0.5">({deck.ratingCount})</span>
          )}
        </div>
      </div>
    </div>
  );
};
