// src/data/mockData.ts
import type {
  User,
  Topic,
  Deck,
  Card,
  SavedDeck,
  DeckRating,
  UserCardProgress,
  UserDeckSettings,
  DeckWithMeta,
} from '../types';

// ─── USERS ────────────────────────────────────────────────────────────────────
export const DEMO_PASSWORD = 'demo123';

export const users: User[] = [
  {
    id: 1,
    username: 'Nguyễn Hữu Hiếu',
    email: 'demo@mindcard.vn',
    hashed_password: 'demo123',
    avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Hieu',
  },
  {
    id: 2,
    username: 'Trần Minh Khoa',
    email: 'khoa@mindcard.vn',
    hashed_password: 'pass123',
    avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Khoa',
  },
  {
    id: 3,
    username: 'Lê Thu Hà',
    email: 'ha@mindcard.vn',
    hashed_password: 'pass123',
    avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Ha',
  },
];

export const CURRENT_USER = users[0];

// ─── TOPICS ───────────────────────────────────────────────────────────────────
export const topics: Topic[] = [
  { id: 1, name: 'Tiếng Anh',    icon: '🇬🇧', color: 'from-blue-500 to-cyan-400' },
  { id: 2, name: 'Toán học',     icon: '📐', color: 'from-violet-500 to-purple-400' },
  { id: 3, name: 'Địa lý',       icon: '🌍', color: 'from-green-500 to-emerald-400' },
  { id: 4, name: 'Lịch sử',      icon: '📜', color: 'from-amber-500 to-orange-400' },
  { id: 5, name: 'Lập trình',    icon: '💻', color: 'from-pink-500 to-rose-400' },
  { id: 6, name: 'Sinh học',     icon: '🧬', color: 'from-teal-500 to-cyan-400' },
  { id: 7, name: 'Vật lý',       icon: '⚛️', color: 'from-indigo-500 to-blue-400' },
];

// ─── DECKS ────────────────────────────────────────────────────────────────────
export const decks: Deck[] = [
  // Tiếng Anh (topic 1)
  { id: 1,  owner_id: 2, topic_id: 1, name: 'IELTS Vocabulary – Band 7+',      visibility: 'PUBLIC',  is_deleted: false, card_count: 120, description: 'Từ vựng IELTS thông dụng cho band 7 trở lên.',          created_at: '2025-01-10' },
  { id: 2,  owner_id: 3, topic_id: 1, name: 'English Phrasal Verbs',             visibility: 'PUBLIC',  is_deleted: false, card_count: 85,  description: 'Các phrasal verbs phổ biến trong giao tiếp hàng ngày.', created_at: '2025-02-14' },
  { id: 3,  owner_id: 2, topic_id: 1, name: 'TOEIC 900 – Listening Vocab',       visibility: 'PUBLIC',  is_deleted: false, card_count: 200, description: 'Từ vựng luyện nghe TOEIC đạt 900 điểm.',              created_at: '2025-03-01' },
  { id: 4,  owner_id: 1, topic_id: 1, name: 'Business English – Emails',         visibility: 'PUBLIC',  is_deleted: false, card_count: 60,  description: 'Mẫu câu tiếng Anh thương mại qua email.',             created_at: '2025-03-20' },
  { id: 5,  owner_id: 3, topic_id: 1, name: 'Idioms & Expressions',              visibility: 'PUBLIC',  is_deleted: false, card_count: 75,  description: 'Thành ngữ và biểu đạt tiếng Anh tự nhiên.',           created_at: '2025-04-05' },

  // Toán học (topic 2)
  { id: 6,  owner_id: 2, topic_id: 2, name: 'Giải tích cơ bản',                  visibility: 'PUBLIC',  is_deleted: false, card_count: 90,  description: 'Công thức giải tích dành cho sinh viên năm nhất.',    created_at: '2025-01-20' },
  { id: 7,  owner_id: 3, topic_id: 2, name: 'Đại số tuyến tính',                 visibility: 'PUBLIC',  is_deleted: false, card_count: 110, description: 'Ma trận, không gian vecto và ánh xạ tuyến tính.',     created_at: '2025-02-01' },
  { id: 8,  owner_id: 1, topic_id: 2, name: 'Xác suất thống kê',                 visibility: 'PRIVATE', is_deleted: false, card_count: 70,  description: 'Xác suất và thống kê ứng dụng.',                     created_at: '2025-02-28' },
  { id: 9,  owner_id: 2, topic_id: 2, name: 'Hình học giải tích',                visibility: 'PUBLIC',  is_deleted: false, card_count: 55,  description: 'Đường thẳng, mặt phẳng và các hình trong không gian.',created_at: '2025-04-10' },

  // Địa lý (topic 3)
  { id: 10, owner_id: 3, topic_id: 3, name: 'Địa lý Việt Nam',                   visibility: 'PUBLIC',  is_deleted: false, card_count: 80,  description: 'Các vùng lãnh thổ, địa hình và khí hậu Việt Nam.',   created_at: '2025-01-15' },
  { id: 11, owner_id: 2, topic_id: 3, name: 'Địa lý thế giới – Châu Á',          visibility: 'PUBLIC',  is_deleted: false, card_count: 95,  description: 'Quốc gia, thủ đô và đặc điểm địa lý Châu Á.',       created_at: '2025-03-05' },
  { id: 12, owner_id: 3, topic_id: 3, name: 'Khí hậu & Môi trường',              visibility: 'PUBLIC',  is_deleted: false, card_count: 50,  description: 'Biến đổi khí hậu và các vấn đề môi trường.',         created_at: '2025-04-20' },

  // Lịch sử (topic 4)
  { id: 13, owner_id: 2, topic_id: 4, name: 'Lịch sử Việt Nam cận đại',          visibility: 'PUBLIC',  is_deleted: false, card_count: 100, description: 'Từ thời Pháp thuộc đến thống nhất đất nước.',         created_at: '2025-02-10' },
  { id: 14, owner_id: 3, topic_id: 4, name: 'Lịch sử thế giới – Thế chiến II',   visibility: 'PUBLIC',  is_deleted: false, card_count: 88,  description: 'Diễn biến và hậu quả của Chiến tranh thế giới II.',  created_at: '2025-03-15' },
  { id: 15, owner_id: 2, topic_id: 4, name: 'Các triều đại phong kiến VN',       visibility: 'PUBLIC',  is_deleted: false, card_count: 65,  description: 'Từ Hồng Bàng đến nhà Nguyễn.',                       created_at: '2025-04-01' },

  // Lập trình (topic 5)
  { id: 16, owner_id: 1, topic_id: 5, name: 'JavaScript ES6+ Flashcards',         visibility: 'PUBLIC',  is_deleted: false, card_count: 130, description: 'Arrow function, destructuring, async/await và hơn thế.', created_at: '2025-01-25' },
  { id: 17, owner_id: 2, topic_id: 5, name: 'Python Cơ Bản đến Nâng Cao',        visibility: 'PUBLIC',  is_deleted: false, card_count: 150, description: 'List comprehension, decorator, generator...',           created_at: '2025-02-20' },
  { id: 18, owner_id: 3, topic_id: 5, name: 'SQL & Database Design',              visibility: 'PUBLIC',  is_deleted: false, card_count: 75,  description: 'Câu lệnh SQL, thiết kế CSDL quan hệ.',                created_at: '2025-03-25' },
  { id: 19, owner_id: 2, topic_id: 5, name: 'Data Structures & Algorithms',       visibility: 'PUBLIC',  is_deleted: false, card_count: 200, description: 'Cấu trúc dữ liệu và giải thuật cơ bản đến nâng cao.', created_at: '2025-04-12' },

  // Sinh học (topic 6)
  { id: 20, owner_id: 3, topic_id: 6, name: 'Sinh học tế bào',                    visibility: 'PUBLIC',  is_deleted: false, card_count: 90,  description: 'Cấu trúc tế bào, nhân, và các bào quan.',             created_at: '2025-02-05' },
  { id: 21, owner_id: 2, topic_id: 6, name: 'Di truyền học Mendel',               visibility: 'PUBLIC',  is_deleted: false, card_count: 60,  description: 'Các quy luật di truyền cổ điển.',                     created_at: '2025-03-10' },

  // Vật lý (topic 7)
  { id: 22, owner_id: 3, topic_id: 7, name: 'Cơ học Newton',                      visibility: 'PUBLIC',  is_deleted: false, card_count: 85,  description: 'Ba định luật Newton và ứng dụng.',                    created_at: '2025-01-30' },
  { id: 23, owner_id: 2, topic_id: 7, name: 'Điện từ học',                         visibility: 'PUBLIC',  is_deleted: false, card_count: 95,  description: 'Điện trường, từ trường và sóng điện từ.',             created_at: '2025-03-18' },
];

// ─── RATINGS ──────────────────────────────────────────────────────────────────
export const deckRatings: DeckRating[] = [
  { deck_id: 1,  user_id: 1, stars: 5 },
  { deck_id: 1,  user_id: 3, stars: 4 },
  { deck_id: 2,  user_id: 1, stars: 4 },
  { deck_id: 2,  user_id: 2, stars: 5 },
  { deck_id: 3,  user_id: 1, stars: 3 },
  { deck_id: 3,  user_id: 3, stars: 4 },
  { deck_id: 4,  user_id: 2, stars: 4 },
  { deck_id: 4,  user_id: 3, stars: 5 },
  { deck_id: 5,  user_id: 1, stars: 5 },
  { deck_id: 6,  user_id: 1, stars: 4 },
  { deck_id: 6,  user_id: 3, stars: 5 },
  { deck_id: 7,  user_id: 2, stars: 3 },
  { deck_id: 9,  user_id: 1, stars: 4 },
  { deck_id: 10, user_id: 2, stars: 5 },
  { deck_id: 10, user_id: 1, stars: 4 },
  { deck_id: 11, user_id: 3, stars: 4 },
  { deck_id: 13, user_id: 1, stars: 5 },
  { deck_id: 13, user_id: 2, stars: 5 },
  { deck_id: 14, user_id: 3, stars: 4 },
  { deck_id: 16, user_id: 2, stars: 5 },
  { deck_id: 16, user_id: 3, stars: 5 },
  { deck_id: 17, user_id: 1, stars: 4 },
  { deck_id: 17, user_id: 3, stars: 5 },
  { deck_id: 19, user_id: 1, stars: 5 },
  { deck_id: 20, user_id: 2, stars: 3 },
  { deck_id: 22, user_id: 1, stars: 4 },
  { deck_id: 23, user_id: 3, stars: 4 },
];

// ─── SAVED DECKS (user 1) ─────────────────────────────────────────────────────
export const savedDecks: SavedDeck[] = [
  { user_id: 1, deck_id: 1,  saved_at: '2025-04-01T08:00:00Z' },
  { user_id: 1, deck_id: 3,  saved_at: '2025-04-05T10:00:00Z' },
  { user_id: 1, deck_id: 7,  saved_at: '2025-04-10T09:00:00Z' },
  { user_id: 1, deck_id: 13, saved_at: '2025-04-15T11:00:00Z' },
  { user_id: 1, deck_id: 16, saved_at: '2025-04-18T14:00:00Z' },
  { user_id: 1, deck_id: 17, saved_at: '2025-04-20T16:00:00Z' },
];

// ─── SAMPLE CARDS (for deck 1) ─────────────────────────────────────────────────
export const cards: Card[] = [
  { id: 1, deck_id: 1, card_type: 'BASIC', front_text: 'Ubiquitous',   back_text: '(adj) Hiện diện khắp nơi, phổ biến rộng rãi.' },
  { id: 2, deck_id: 1, card_type: 'BASIC', front_text: 'Proliferate',  back_text: '(v) Phát triển nhanh, sinh sôi nảy nở.' },
  { id: 3, deck_id: 1, card_type: 'BASIC', front_text: 'Mitigate',     back_text: '(v) Giảm nhẹ, làm dịu bớt.' },
  { id: 4, deck_id: 1, card_type: 'TYPE',  front_text: 'Nhìn định nghĩa và gõ từ: Sự tận tụy, kiên định', back_text: 'Perseverance' },
  { id: 5, deck_id: 1, card_type: 'BASIC', front_text: 'Scrutinize',   back_text: '(v) Xem xét kỹ lưỡng, kiểm tra tỉ mỉ.' },
];

// ─── USER_DECK_SETTINGS ───────────────────────────────────────────────────────
export const userDeckSettings: UserDeckSettings[] = [
  { user_id: 1, deck_id: 1,  daily_review_cards: 20 },
  { user_id: 1, deck_id: 16, daily_review_cards: 15 },
];

// ─── USER CARD PROGRESS ───────────────────────────────────────────────────────
export const userCardProgress: UserCardProgress[] = [
  { user_id: 1, card_id: 1, next_due: '2025-05-15T00:00:00Z', interval: 7,  ease_factor: 2.6, status: 'REVIEW' },
  { user_id: 1, card_id: 2, next_due: '2025-05-14T00:00:00Z', interval: 2,  ease_factor: 2.3, status: 'LEARNING' },
  { user_id: 1, card_id: 3, next_due: '2025-05-14T00:00:00Z', interval: 0,  ease_factor: 2.5, status: 'NEW' },
];

// ─── HELPER FUNCTIONS ─────────────────────────────────────────────────────────

export function getTopicById(id: number): Topic | undefined {
  return topics.find(t => t.id === id);
}

export function getUserById(id: number): User | undefined {
  return users.find(u => u.id === id);
}

export function getAverageRating(deckId: number): { avg: number; count: number } {
  const ratings = deckRatings.filter(r => r.deck_id === deckId);
  if (ratings.length === 0) return { avg: 0, count: 0 };
  const avg = ratings.reduce((sum, r) => sum + r.stars, 0) / ratings.length;
  return { avg: Math.round(avg * 10) / 10, count: ratings.length };
}

export function isSavedByUser(userId: number, deckId: number): boolean {
  return savedDecks.some(s => s.user_id === userId && s.deck_id === deckId);
}

export function getPublicDecksWithMeta(currentUserId: number): DeckWithMeta[] {
  return decks
    .filter(d => d.visibility === 'PUBLIC' && !d.is_deleted)
    .map(d => {
      const topic = getTopicById(d.topic_id)!;
      const owner = getUserById(d.owner_id)!;
      const { avg, count } = getAverageRating(d.id);
      const is_saved = isSavedByUser(currentUserId, d.id);
      return { ...d, topic, owner, avg_rating: avg, rating_count: count, is_saved };
    });
}

export function groupDecksByTopic(decksWithMeta: DeckWithMeta[]): Map<number, { topic: Topic; decks: DeckWithMeta[] }> {
  const map = new Map<number, { topic: Topic; decks: DeckWithMeta[] }>();
  for (const deck of decksWithMeta) {
    if (!map.has(deck.topic_id)) {
      map.set(deck.topic_id, { topic: deck.topic, decks: [] });
    }
    map.get(deck.topic_id)!.decks.push(deck);
  }
  return map;
}
