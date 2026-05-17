// src/components/layout/Sidebar.tsx
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';

interface SidebarProps {
  isOpen: boolean;
  onClose: () => void;
}

const menuItems = [
  {
    id: 'my-decks',
    label: 'Deck của tôi',
    icon: (
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth={1.8} className="w-5 h-5">
        <rect x="2" y="3" width="20" height="14" rx="2" />
        <line x1="8" y1="21" x2="16" y2="21" />
        <line x1="12" y1="17" x2="12" y2="21" />
      </svg>
    ),
    description: 'Quản lý bộ thẻ của bạn',
    path: '/my-decks',
    gradient: 'from-indigo-500 to-violet-500',
  },
  {
    id: 'saved-decks',
    label: 'Deck đã lưu',
    icon: (
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth={1.8} className="w-5 h-5">
        <path d="M19 21l-7-5-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2z" />
      </svg>
    ),
    description: 'Các bộ thẻ đã lưu về thư viện',
    path: '/saved-decks',
    gradient: 'from-cyan-500 to-blue-500',
  },
  {
    id: 'browse',
    label: 'Duyệt thẻ công khai',
    icon: (
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth={1.8} className="w-5 h-5">
        <circle cx="11" cy="11" r="8" />
        <line x1="21" y1="21" x2="16.65" y2="16.65" />
      </svg>
    ),
    description: 'Khám phá bộ thẻ cộng đồng',
    path: '/browse',
    gradient: 'from-emerald-500 to-teal-500',
  },
];

export default function Sidebar({ isOpen, onClose }: SidebarProps) {
  const { currentUser, logout } = useAuth();
  const navigate = useNavigate();

  const handleNavigate = (path: string) => {
    navigate(path);
    onClose();
  };

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <>
      {/* Backdrop */}
      <div
        onClick={onClose}
        className={`
          fixed inset-0 z-40 bg-black/50 backdrop-blur-sm
          transition-opacity duration-300
          ${isOpen ? 'opacity-100 pointer-events-auto' : 'opacity-0 pointer-events-none'}
        `}
      />

      {/* Sidebar panel */}
      <aside
        className={`
          fixed right-0 top-0 z-50 h-full w-72
          flex flex-col
          bg-slate-900/95 backdrop-blur-xl
          border-l border-slate-700/50
          shadow-2xl shadow-black/40
          transition-transform duration-300 ease-in-out
          ${isOpen ? 'translate-x-0' : 'translate-x-full'}
        `}
      >
        {/* Header */}
        <div className="flex items-center justify-between px-5 py-4 border-b border-slate-700/50">
          <div className="flex items-center gap-2.5">
            <div className="w-7 h-7 rounded-lg bg-gradient-to-br from-indigo-500 to-violet-600 flex items-center justify-center text-white font-bold text-xs">
              M
            </div>
            <span className="font-bold text-slate-100">MindCard</span>
          </div>
          <button
            id="sidebar-close"
            onClick={onClose}
            className="p-1.5 rounded-lg text-slate-400 hover:text-slate-200 hover:bg-slate-700/60 transition-all"
          >
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth={2} className="w-5 h-5">
              <line x1="18" y1="6" x2="6" y2="18" />
              <line x1="6" y1="6" x2="18" y2="18" />
            </svg>
          </button>
        </div>

        {/* Menu items */}
        <nav className="flex-1 p-4 space-y-1.5 overflow-y-auto">
          <p className="text-xs font-semibold text-slate-500 uppercase tracking-wider mb-3 px-1">Menu</p>
          {menuItems.map(item => (
            <button
              key={item.id}
              id={`sidebar-${item.id}`}
              onClick={() => handleNavigate(item.path)}
              className="
                w-full flex items-center gap-3.5 px-3.5 py-3 rounded-xl
                text-slate-300 hover:text-white
                hover:bg-slate-700/60
                transition-all duration-200 group text-left
                active:scale-[0.98]
              "
            >
              <div className={`
                flex-shrink-0 w-9 h-9 rounded-xl flex items-center justify-center
                bg-gradient-to-br ${item.gradient}
                text-white shadow-md
                group-hover:shadow-lg transition-shadow
              `}>
                {item.icon}
              </div>
              <div className="min-w-0">
                <div className="text-sm font-semibold truncate">{item.label}</div>
                <div className="text-xs text-slate-500 truncate">{item.description}</div>
              </div>
            </button>
          ))}
        </nav>

        {/* Footer – user info + logout */}
        <div className="p-4 border-t border-slate-700/50">
          {currentUser && (
            <div className="flex items-center gap-3 px-2 py-2 mb-3">
              <img
                src={currentUser.avatar}
                alt={currentUser.username}
                className="w-9 h-9 rounded-full border-2 border-indigo-500/50 bg-slate-700 shrink-0"
              />
              <div className="min-w-0">
                <div className="text-sm font-semibold text-slate-100 truncate">{currentUser.username}</div>
                <div className="text-xs text-slate-400 truncate">{currentUser.email}</div>
              </div>
            </div>
          )}

          <button
            id="sidebar-logout"
            onClick={handleLogout}
            className="
              w-full flex items-center justify-center gap-2 py-2.5 px-4 rounded-xl
              text-sm font-semibold text-red-400
              border border-red-500/30 hover:border-red-500/60
              hover:bg-red-500/10 hover:text-red-300
              transition-all duration-200 active:scale-[0.98]
            "
          >
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth={2} className="w-4 h-4">
              <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4" />
              <polyline points="16 17 21 12 16 7" />
              <line x1="21" y1="12" x2="9" y2="12" />
            </svg>
            Đăng xuất
          </button>
        </div>
      </aside>
    </>
  );
}
