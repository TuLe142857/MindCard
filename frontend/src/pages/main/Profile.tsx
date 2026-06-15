import React from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useAppSelector } from '@/store/hooks';
import { User as UserIcon, BookOpen, Calendar, Camera } from 'lucide-react';
import { DeckCard } from '@/features/decks/components/DeckCard';
import {
  useGetUserProfile,
  useGetUserDecks,
  useGetSelfDecks,
  useUpdateAvatar,
} from '@/features/users/hooks/useUsers';

export const Profile: React.FC = () => {
  const { username } = useParams<{ username: string }>();
  const navigate = useNavigate();
  const { user: currentUser } = useAppSelector((state) => state.auth);

  const isSelf = currentUser?.username === username;

  // If visiting other user's profile
  const { data: publicProfileData, isLoading: isLoadingPublicProfile } = useGetUserProfile(
    username || '',
    { enabled: !isSelf }
  );
  const { data: publicDecksData, isLoading: isLoadingPublicDecks } = useGetUserDecks(
    username || '',
    undefined,
    { enabled: !isSelf }
  );

  // If visiting self profile
  const { data: selfDecksData, isLoading: isLoadingSelfDecks } = useGetSelfDecks(
    {},
    { enabled: isSelf }
  );
  const { mutate: updateAvatar } = useUpdateAvatar();

  const handleAvatarChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file && isSelf) {
      updateAvatar(file);
    }
  };

  const displayUser = isSelf ? currentUser : publicProfileData;
  const decks = isSelf ? selfDecksData?.data : publicDecksData?.data;
  const isLoading = isSelf ? isLoadingSelfDecks : isLoadingPublicProfile || isLoadingPublicDecks;

  if (!displayUser && !isLoading) {
    return (
      <div className="flex flex-col items-center justify-center py-20 text-center">
        <UserIcon size={48} className="text-slate-600 mb-4" />
        <h2 className="text-2xl font-bold text-slate-200">User not found</h2>
        <p className="text-slate-400 mt-2">The user you are looking for does not exist.</p>
      </div>
    );
  }

  return (
    <div className="flex flex-col gap-8 max-w-6xl mx-auto h-full pb-12">
      {/* Profile Header */}
      <div className="bg-slate-900 border border-slate-800 rounded-3xl p-8 flex flex-col md:flex-row items-center md:items-start gap-6 relative overflow-hidden">
        {/* Background decorative element */}
        <div className="absolute top-0 right-0 w-64 h-64 bg-blue-500/5 rounded-full blur-3xl -translate-y-1/2 translate-x-1/2" />

        <div className="relative group z-10 shrink-0">
          <div className="w-28 h-28 rounded-full overflow-hidden border-4 border-slate-800 bg-slate-950 flex items-center justify-center shadow-xl">
            {displayUser?.avatarUrl ? (
              <img
                src={displayUser.avatarUrl}
                alt="Avatar"
                className="w-full h-full object-cover"
              />
            ) : (
              <UserIcon size={40} className="text-slate-600" />
            )}
          </div>
          {isSelf && (
            <label className="absolute inset-0 flex items-center justify-center bg-slate-950/60 rounded-full opacity-0 group-hover:opacity-100 transition-opacity cursor-pointer">
              <Camera size={24} className="text-slate-200" />
              <input
                type="file"
                className="hidden"
                accept="image/*"
                onChange={handleAvatarChange}
              />
            </label>
          )}
        </div>

        <div className="flex flex-col items-center md:items-start text-center md:text-left z-10 flex-1">
          <h1 className="text-3xl font-bold text-slate-100">
            {displayUser?.username || 'Loading...'}
          </h1>
          <div className="flex flex-wrap items-center justify-center md:justify-start gap-4 mt-4 text-sm text-slate-400">
            <div className="flex items-center gap-1.5 bg-slate-950/50 px-3 py-1.5 rounded-full border border-slate-800/50">
              <BookOpen size={14} className="text-blue-400" />
              <span className="font-medium text-slate-300">{decks?.length || 0}</span> Decks
            </div>
            <div className="flex items-center gap-1.5 bg-slate-950/50 px-3 py-1.5 rounded-full border border-slate-800/50">
              <Calendar size={14} className="text-purple-400" />
              <span>Joined recently</span>
            </div>
          </div>
        </div>
      </div>

      {/* Decks List */}
      <div className="flex flex-col gap-6">
        <div className="flex items-center justify-between border-b border-slate-800 pb-2">
          <h2 className="text-xl font-bold text-slate-200">
            {isSelf ? 'My Decks' : 'Public Decks'}
          </h2>
        </div>

        {isLoading ? (
          <div className="flex items-center justify-center py-20">
            <div className="w-8 h-8 border-4 border-blue-500 border-t-transparent rounded-full animate-spin" />
          </div>
        ) : decks && decks.length > 0 ? (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
            {decks.map((deck) => (
              <DeckCard key={deck.id} deck={deck} onClick={() => navigate(`/deck/${deck.id}`)} />
            ))}
          </div>
        ) : (
          <div className="flex flex-col items-center justify-center py-20 px-4 border border-dashed border-slate-800 rounded-2xl bg-slate-900/50 text-center">
            <div className="w-16 h-16 rounded-full bg-slate-800 flex items-center justify-center mb-4">
              <BookOpen size={24} className="text-slate-500" />
            </div>
            <h3 className="text-lg font-medium text-slate-300 mb-1">No decks found</h3>
            <p className="text-slate-500 max-w-sm">
              {isSelf
                ? "You haven't created any decks yet."
                : `${displayUser?.username} hasn't published any decks yet.`}
            </p>
          </div>
        )}
      </div>
    </div>
  );
};
