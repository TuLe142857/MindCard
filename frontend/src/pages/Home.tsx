// src/pages/Home.tsx
import { useMemo, useState } from 'react';
import MainLayout from '../components/layout/MainLayout';
import TopicRow from '../components/ui/TopicRow';
import { getPublicDecksWithMeta, groupDecksByTopic, topics } from '../data/mockData';
import { useAuth } from '../hooks/useAuth';

export default function HomePage() {
  const { currentUser } = useAuth();
  const userId = currentUser?.id ?? 1;

  // Track saved state locally (will be server state later)
  const [savedIds, setSavedIds] = useState<Set<number>>(() => {
    const initial = getPublicDecksWithMeta(userId)
      .filter(d => d.is_saved)
      .map(d => d.id);
    return new Set(initial);
  });

  const handleSaveToggle = (deckId: number, saved: boolean) => {
    setSavedIds(prev => {
      const next = new Set(prev);
      if (saved) next.add(deckId); else next.delete(deckId);
      return next;
    });
  };

  const topicGroups = useMemo(() => {
    const allDecks = getPublicDecksWithMeta(userId).map(d => ({
      ...d,
      is_saved: savedIds.has(d.id),
    }));
    const grouped = groupDecksByTopic(allDecks);
    // Preserve topic order from topics array
    return topics
      .map(t => grouped.get(t.id))
      .filter(Boolean) as Array<{ topic: typeof topics[0]; decks: ReturnType<typeof getPublicDecksWithMeta> }>;
  }, [userId, savedIds]);

  return (
    <MainLayout>
      {/* Hero section */}
      <div className="relative overflow-hidden rounded-2xl bg-gradient-to-br from-indigo-600/20 via-violet-600/10 to-slate-900/0 border border-indigo-500/20 px-6 py-8 md:px-10 md:py-10">
        {/* Background decoration */}
        <div className="absolute -top-20 -right-20 w-72 h-72 bg-indigo-500/10 rounded-full blur-3xl pointer-events-none" />
        <div className="absolute -bottom-10 -left-10 w-56 h-56 bg-violet-500/10 rounded-full blur-3xl pointer-events-none" />

        <div className="relative z-10 max-w-xl">
          <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-indigo-500/15 border border-indigo-500/25 text-xs text-indigo-300 font-medium mb-3">
            <span className="w-1.5 h-1.5 rounded-full bg-indigo-400 animate-pulse" />
            Khám phá bộ thẻ mới hôm nay
          </div>
          <h1 className="text-2xl md:text-3xl font-bold text-white leading-snug mb-2">
            Xin chào, <span className="bg-gradient-to-r from-indigo-400 to-violet-400 bg-clip-text text-transparent">
              {currentUser?.username.split(' ').slice(-1)[0] ?? 'bạn'}
            </span>! 👋
          </h1>
          <p className="text-slate-400 text-sm md:text-base leading-relaxed">
            Tiếp tục hành trình học tập. Khám phá hàng trăm bộ thẻ từ cộng đồng MindCard.
          </p>

          {/* Quick stats */}
          <div className="flex flex-wrap gap-4 mt-5">
            {[
              { label: 'Chủ đề', value: topics.length, icon: '🏷️' },
              { label: 'Bộ thẻ công khai', value: topicGroups.reduce((s, g) => s + g.decks.length, 0), icon: '📚' },
              { label: 'Đã lưu', value: savedIds.size, icon: '🔖' },
            ].map(stat => (
              <div key={stat.label} className="flex items-center gap-2 px-3.5 py-2 rounded-xl bg-slate-800/60 border border-slate-700/40">
                <span className="text-base">{stat.icon}</span>
                <div>
                  <div className="text-base font-bold text-white leading-none">{stat.value}</div>
                  <div className="text-xs text-slate-400">{stat.label}</div>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* Topic rows */}
      <div className="space-y-8">
        {topicGroups.map(({ topic, decks }) => (
          <TopicRow
            key={topic.id}
            topic={topic}
            decks={decks}
            onSaveToggle={handleSaveToggle}
          />
        ))}
      </div>
    </MainLayout>
  );
}
