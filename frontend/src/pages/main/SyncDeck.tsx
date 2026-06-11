import React, { useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { ArrowLeft, RefreshCw, Plus, Minus, Edit3, CheckCircle2, AlertTriangle, Image as ImageIcon, Music } from 'lucide-react';
import { Button } from '@/shared/components/ui/Button';
import { cn } from '@/shared/utils/cn';
import type { DeckSyncSummary, CardDiff, FieldDiff } from '@/features/saved-decks/types';

// Dummy Data
const MOCK_SUMMARY: DeckSyncSummary = {
  deckId: 42,
  totalNewCards: 2,
  totalUpdatedCards: 2,
  totalDeletedCards: 1,
};

const MOCK_DIFFS: CardDiff[] = [
  {
    cardId: 101,
    changeType: 'NEW',
    currentVersion: 0,
    upcomingVersion: 1,
    type: { current: null, upcoming: 'BASIC' },
    frontText: { current: null, upcoming: 'What is the largest mammal on Earth?' },
    frontImage: { current: null, upcoming: 'https://upload.wikimedia.org/wikipedia/commons/1/1c/Blue_Whale_001_body_bw.jpg' },
    frontAudio: { current: null, upcoming: 'https://actions.google.com/sounds/v1/water/ocean_waves.ogg' },
    backText: { current: null, upcoming: 'Blue Whale' },
    backImage: null,
    backAudio: null,
  },
  {
    cardId: 55,
    changeType: 'UPDATED',
    currentVersion: 2,
    upcomingVersion: 3,
    type: null,
    frontText: { current: 'React logo', upcoming: 'Identify this framework logo' },
    frontImage: { current: 'https://upload.wikimedia.org/wikipedia/commons/a/a7/React-icon.svg', upcoming: 'https://upload.wikimedia.org/wikipedia/commons/a/a7/React-icon.svg' },
    frontAudio: null,
    backText: { current: 'ReactJS', upcoming: 'React' },
    backImage: { current: null, upcoming: 'https://upload.wikimedia.org/wikipedia/commons/thumb/a/a7/React-icon.svg/200px-React-icon.svg.png' },
    backAudio: { current: null, upcoming: 'https://actions.google.com/sounds/v1/cartoon/clang_and_wobble.ogg' },
  },
  {
    cardId: 42,
    changeType: 'DELETED',
    currentVersion: 1,
    upcomingVersion: 1, // Doesn't matter
    type: null,
    frontText: { current: 'Old deprecated AWS service', upcoming: null },
    frontImage: null,
    frontAudio: { current: 'https://actions.google.com/sounds/v1/alarms/beep_short.ogg', upcoming: null },
    backText: { current: 'Some answer', upcoming: null },
    backImage: null,
    backAudio: null,
  }
];

export const SyncDeck: React.FC = () => {
  const { savedDeckId } = useParams<{ savedDeckId: string }>();
  const navigate = useNavigate();
  const [isSyncing, setIsSyncing] = useState(false);
  const [isSynced, setIsSynced] = useState(false);

  const handleSyncAll = () => {
    setIsSyncing(true);
    // Simulate API call
    setTimeout(() => {
      setIsSyncing(false);
      setIsSynced(true);
    }, 1500);
  };

  const renderMediaValue = (val: string | null, type: 'image' | 'audio', isCurrent: boolean) => {
    if (!val) return <span className="text-slate-600 italic text-xs">None</span>;
    if (type === 'image') {
      return <div className={cn("h-16 w-16 bg-slate-900 rounded overflow-hidden border", isCurrent ? "border-slate-700 opacity-50" : "border-slate-600")}><img src={val} alt="preview" className="w-full h-full object-cover" /></div>;
    }
    if (type === 'audio') {
      return (
        <div className={cn("flex items-center gap-2", isCurrent ? "opacity-50" : "")}>
           <Music size={14} className="text-slate-400" />
           <audio controls src={val} className="h-8 max-w-[150px]" />
        </div>
      );
    }
    return null;
  };

  const renderDiffField = (label: string, diff: FieldDiff | null, type: 'text' | 'image' | 'audio' = 'text') => {
    if (!diff) return null;
    return (
      <div className="flex flex-col gap-2 mb-4">
        <div className="text-xs font-semibold text-blue-400">{label}</div>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
          <div className="flex flex-col gap-1">
            <span className="text-[10px] text-slate-500 uppercase">Current</span>
            <div className="text-sm text-slate-400 bg-slate-950 p-3 rounded-lg border border-slate-800">
              {type === 'text' ? (
                <span className={cn(diff.current && "line-through decoration-red-500/50")}>{diff.current || <span className="text-slate-600 italic">None</span>}</span>
              ) : renderMediaValue(diff.current, type, true)}
            </div>
          </div>
          <div className="flex flex-col gap-1">
            <span className="text-[10px] text-green-400 uppercase">Upcoming</span>
            <div className="text-sm text-slate-200 bg-green-500/10 p-3 rounded-lg border border-green-500/30">
              {type === 'text' ? (
                <span>{diff.upcoming || <span className="text-slate-600 italic">None</span>}</span>
              ) : renderMediaValue(diff.upcoming, type, false)}
            </div>
          </div>
        </div>
      </div>
    );
  };

  const renderDiffContent = (diff: CardDiff) => {
    if (diff.changeType === 'NEW') {
      return (
        <div className="flex flex-col gap-3 p-4 bg-green-500/5 border border-green-500/20 rounded-xl">
          <div className="text-xs font-semibold text-green-400">FRONT</div>
          <div className="text-sm text-slate-300 bg-slate-950 p-3 rounded-lg border border-slate-800 flex flex-col gap-2">
            {diff.frontText?.upcoming && <p>{diff.frontText.upcoming}</p>}
            {diff.frontImage?.upcoming && renderMediaValue(diff.frontImage.upcoming, 'image', false)}
            {diff.frontAudio?.upcoming && renderMediaValue(diff.frontAudio.upcoming, 'audio', false)}
          </div>
          
          <div className="text-xs font-semibold text-green-400 mt-2">BACK</div>
          <div className="text-sm text-slate-300 bg-slate-950 p-3 rounded-lg border border-slate-800 flex flex-col gap-2">
            {diff.backText?.upcoming && <p>{diff.backText.upcoming}</p>}
            {diff.backImage?.upcoming && renderMediaValue(diff.backImage.upcoming, 'image', false)}
            {diff.backAudio?.upcoming && renderMediaValue(diff.backAudio.upcoming, 'audio', false)}
          </div>
        </div>
      );
    }

    if (diff.changeType === 'DELETED') {
      return (
        <div className="flex flex-col gap-3 p-4 bg-red-500/5 border border-red-500/20 rounded-xl">
          <div className="text-xs font-semibold text-red-400">FRONT</div>
          <div className="text-sm text-slate-500 bg-slate-950 p-3 rounded-lg border border-slate-800 flex flex-col gap-2">
            {diff.frontText?.current && <p className="line-through">{diff.frontText.current}</p>}
            {diff.frontImage?.current && renderMediaValue(diff.frontImage.current, 'image', true)}
            {diff.frontAudio?.current && renderMediaValue(diff.frontAudio.current, 'audio', true)}
          </div>

          <div className="text-xs font-semibold text-red-400 mt-2">BACK</div>
          <div className="text-sm text-slate-500 bg-slate-950 p-3 rounded-lg border border-slate-800 flex flex-col gap-2">
            {diff.backText?.current && <p className="line-through">{diff.backText.current}</p>}
            {diff.backImage?.current && renderMediaValue(diff.backImage.current, 'image', true)}
            {diff.backAudio?.current && renderMediaValue(diff.backAudio.current, 'audio', true)}
          </div>
        </div>
      );
    }

    if (diff.changeType === 'UPDATED') {
      return (
        <div className="flex flex-col p-4 bg-blue-500/5 border border-blue-500/20 rounded-xl">
          {renderDiffField('FRONT TEXT', diff.frontText, 'text')}
          {renderDiffField('FRONT IMAGE', diff.frontImage, 'image')}
          {renderDiffField('FRONT AUDIO', diff.frontAudio, 'audio')}
          
          {renderDiffField('BACK TEXT', diff.backText, 'text')}
          {renderDiffField('BACK IMAGE', diff.backImage, 'image')}
          {renderDiffField('BACK AUDIO', diff.backAudio, 'audio')}
        </div>
      );
    }

    return null;
  };

  if (isSynced) {
    return (
      <div className="flex flex-col items-center justify-center h-full min-h-[60vh] animate-in fade-in zoom-in duration-500">
        <div className="w-20 h-20 rounded-full bg-green-500/20 flex items-center justify-center mb-6">
          <CheckCircle2 size={40} className="text-green-500" />
        </div>
        <h1 className="text-2xl font-bold text-slate-100 mb-2">Sync Complete!</h1>
        <p className="text-slate-400 text-center max-w-md mb-8">
          Your saved deck has been successfully updated with the latest changes from the author.
        </p>
        <Button onClick={() => navigate('/library')} className="px-8">
          Return to Library
        </Button>
      </div>
    );
  }

  return (
    <div className="flex flex-col gap-6 max-w-5xl mx-auto h-full pb-12">
      {/* Header */}
      <div className="flex items-center justify-between">
        <button 
          onClick={() => navigate('/library')}
          className="flex items-center gap-2 text-slate-400 hover:text-slate-200 transition-colors text-sm font-medium"
        >
          <ArrowLeft size={16} />
          Back to Library
        </button>
        <div className="flex items-center gap-3">
          <Button variant="outline" onClick={() => navigate('/library')} disabled={isSyncing}>
            Cancel
          </Button>
          <Button 
            className="gap-2 bg-yellow-500 hover:bg-yellow-600 text-yellow-950 font-bold shadow-[0_0_15px_rgba(234,179,8,0.3)]"
            onClick={handleSyncAll}
            isLoading={isSyncing}
          >
            <RefreshCw size={16} className={cn(isSyncing && "animate-spin")} />
            Sync All Updates
          </Button>
        </div>
      </div>

      <div className="flex items-center gap-3 mt-2">
        <div className="w-12 h-12 rounded-xl bg-yellow-500/20 flex items-center justify-center text-yellow-500">
          <RefreshCw size={24} />
        </div>
        <div>
          <h1 className="text-2xl font-bold text-slate-100">Review Deck Updates</h1>
          <p className="text-sm text-slate-400">The original author has made changes to this deck.</p>
        </div>
      </div>

      {/* Summary Banner */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div className="bg-slate-900 border border-slate-800 rounded-xl p-5 flex items-center gap-4">
          <div className="w-12 h-12 rounded-full bg-green-500/10 flex items-center justify-center text-green-500">
            <Plus size={24} />
          </div>
          <div>
            <div className="text-2xl font-bold text-slate-100">{MOCK_SUMMARY.totalNewCards}</div>
            <div className="text-sm text-slate-500">New Cards</div>
          </div>
        </div>
        
        <div className="bg-slate-900 border border-slate-800 rounded-xl p-5 flex items-center gap-4">
          <div className="w-12 h-12 rounded-full bg-blue-500/10 flex items-center justify-center text-blue-500">
            <Edit3 size={24} />
          </div>
          <div>
            <div className="text-2xl font-bold text-slate-100">{MOCK_SUMMARY.totalUpdatedCards}</div>
            <div className="text-sm text-slate-500">Updated Cards</div>
          </div>
        </div>

        <div className="bg-slate-900 border border-slate-800 rounded-xl p-5 flex items-center gap-4">
          <div className="w-12 h-12 rounded-full bg-red-500/10 flex items-center justify-center text-red-500">
            <Minus size={24} />
          </div>
          <div>
            <div className="text-2xl font-bold text-slate-100">{MOCK_SUMMARY.totalDeletedCards}</div>
            <div className="text-sm text-slate-500">Deleted Cards</div>
          </div>
        </div>
      </div>

      {/* Warning */}
      {MOCK_SUMMARY.totalDeletedCards > 0 && (
        <div className="flex items-start gap-3 p-4 bg-orange-500/10 border border-orange-500/20 rounded-xl text-orange-400 text-sm">
          <AlertTriangle size={20} className="shrink-0 mt-0.5" />
          <p>
            <strong>Warning:</strong> Syncing will permanently remove deleted cards from your library, and you will lose your study progress for those specific cards.
          </p>
        </div>
      )}

      {/* Diff List */}
      <div className="mt-4 flex flex-col gap-6">
        <h2 className="text-lg font-bold text-slate-200 border-b border-slate-800 pb-2">Detailed Changes</h2>
        
        <div className="flex flex-col gap-6">
          {MOCK_DIFFS.map((diff, index) => (
            <div key={diff.cardId} className="flex flex-col bg-slate-900 border border-slate-800 rounded-xl overflow-hidden shadow-lg">
              <div className="flex items-center justify-between px-5 py-3 border-b border-slate-800 bg-slate-950/50">
                <span className="text-sm font-medium text-slate-400">Card #{index + 1}</span>
                <span className={cn(
                  "text-[10px] font-bold px-2.5 py-1 rounded-full uppercase tracking-wider",
                  diff.changeType === 'NEW' && "bg-green-500/20 text-green-400 border border-green-500/30",
                  diff.changeType === 'DELETED' && "bg-red-500/20 text-red-400 border border-red-500/30",
                  diff.changeType === 'UPDATED' && "bg-blue-500/20 text-blue-400 border border-blue-500/30"
                )}>
                  {diff.changeType}
                </span>
              </div>
              <div className="p-5">
                {renderDiffContent(diff)}
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};
